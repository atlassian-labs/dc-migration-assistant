/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.migration.datacenter.core.aws.db.restore;

import com.atlassian.migration.datacenter.core.aws.MigrationStageCallback;
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.RemoteInstanceCommandRunnerService;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.aws.ssm.SuccessfulSSMCommandConsumer;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.EnsureSuccessfulSSMCommandConsumer;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;

import java.util.Arrays;
import java.util.Collections;

public class SsmPsqlDatabaseRestoreService {

    private static final Logger logger = LoggerFactory.getLogger(SsmPsqlDatabaseRestoreService.class);

    private final SSMApi ssm;
    private final AWSMigrationHelperDeploymentService migrationHelperDeploymentService;
    private final MigrationStageCallback migrationStageCallback;
    private final RemoteInstanceCommandRunnerService remoteInstanceCommandRunnerService;
    private final String CRITICAL_ERROR_001 = "could not connect to server";
    private final String CRITICAL_ERROR_002 = "could not translate host name";
    private final String CRITICAL_ERROR_003 = "Connection timed out";
    private final String CRITICAL_ERROR_004 = "No route to host";
    private final String CRITICAL_ERROR_005 = "Name or service not known";
    
    // An array of critical errors that can be encountered when performing the RDS restore
    private final String[] CRITICAL_ERRORS = {
            CRITICAL_ERROR_001,
            CRITICAL_ERROR_002,
            CRITICAL_ERROR_003,
            CRITICAL_ERROR_004,
            CRITICAL_ERROR_005,
    };

    private final String restoreDocumentName = "restoreDatabaseBackupToRDS";
    
    private String commandId;

    public SsmPsqlDatabaseRestoreService(SSMApi ssm, AWSMigrationHelperDeploymentService migrationHelperDeploymentService, DatabaseRestoreStageTransitionCallback migrationStageCallback, RemoteInstanceCommandRunnerService remoteInstanceCommandRunnerService) {
        this.ssm = ssm;
        this.migrationHelperDeploymentService = migrationHelperDeploymentService;
        this.migrationStageCallback = migrationStageCallback;
        this.remoteInstanceCommandRunnerService = remoteInstanceCommandRunnerService;
    }

    public void restoreDatabase() throws DatabaseMigrationFailure, InvalidMigrationStageError {
        logger.info("Starting the database restore operation");

        String dbRestorePlaybook;
        String migrationInstanceId;
        try {
            dbRestorePlaybook = migrationHelperDeploymentService.getDbRestoreDocument();
            migrationInstanceId = migrationHelperDeploymentService.getMigrationHostInstanceId();
        } catch (InfrastructureDeploymentError infrastructureDeploymentError) {
            throw new DatabaseMigrationFailure("unable to get outputs from migration stack", infrastructureDeploymentError);
        }

        this.migrationStageCallback.assertInStartingStage();

        /*
         * Stop Jira prior to restoring the DB 
         */
        remoteInstanceCommandRunnerService.setJiraRunStateTo(RemoteServiceState.STOP);
        
        try {
            this.commandId = ssm.runSSMDocument(dbRestorePlaybook, migrationInstanceId, Collections.emptyMap());
        } catch (S3SyncFileSystemDownloader.CannotLaunchCommandException e) {
            throw new DatabaseMigrationFailure("unable to run db restore SSM playbook");
        }

        SuccessfulSSMCommandConsumer consumer = new EnsureSuccessfulSSMCommandConsumer(ssm, commandId,
                migrationInstanceId);
        
        migrationStageCallback.transitionToServiceWaitStage();
        
        try {
            consumer.handleCommandOutput();
            checkForCriticalError(migrationInstanceId);
            migrationStageCallback.transitionToServiceNextStage();
        } catch (SuccessfulSSMCommandConsumer.UnsuccessfulSSMCommandInvocationException
                | SuccessfulSSMCommandConsumer.SSMCommandInvocationProcessingError e) {
            final String errorMessage = "Error restoring database. Either download of database dump from S3 failed or pg_restore failed";
            throw new DatabaseMigrationFailure(errorMessage, e);
        } finally {
            /*
             * Start Jira
             */
            remoteInstanceCommandRunnerService.setJiraRunStateTo(RemoteServiceState.START);
        }
    }

    public SsmCommandResult fetchCommandResult() throws SsmCommandNotInitialisedException {
        if (getCommandId() == null) {
            throw new SsmCommandNotInitialisedException("SSM command was not executed");
        }
        String migrationInstanceId;
        try {
            migrationInstanceId = migrationHelperDeploymentService.getMigrationHostInstanceId();
        } catch (InfrastructureDeploymentError infrastructureDeploymentError) {
            final String msg = "migration host is lost";
            logger.error(msg, infrastructureDeploymentError);
            throw new SsmCommandNotInitialisedException(msg, infrastructureDeploymentError);
        }

        final GetCommandInvocationResponse response = ssm.getSSMCommand(getCommandId(), migrationInstanceId);

        final SsmCommandResult ssmCommandOutputs = new SsmCommandResult();
        ssmCommandOutputs.errorMessage = response.standardErrorContent();
        ssmCommandOutputs.criticalError = isCritical(response.standardErrorContent());
        
        final String migrationS3BucketName;
        try {
            migrationS3BucketName = migrationHelperDeploymentService.getMigrationS3BucketName();
        } catch (InfrastructureDeploymentError infrastructureDeploymentError) {
            final String msg = "unable to get migration s3 bucket";
            logger.error(msg, infrastructureDeploymentError);
            throw new SsmCommandNotInitialisedException(msg);
        }

        ssmCommandOutputs.consoleUrl = String.format(
                "https://console.aws.amazon.com/s3/buckets/%s/%s/%s/%s/awsrunShellScript/%s/",
                migrationS3BucketName,
                ssm.getSsmS3KeyPrefix(),
                response.commandId(),
                response.instanceId(),
                restoreDocumentName);

        return ssmCommandOutputs;
    }

    public String getCommandId() {
        return commandId;
    }

    public String getRestoreDocumentName() {
        return restoreDocumentName;
    }

    public static class SsmCommandResult {
        public String consoleUrl;
        public String errorMessage;
        public boolean criticalError;
    }

    public static class SsmCommandNotInitialisedException extends Exception {
        public SsmCommandNotInitialisedException(String message) {
            super(message);
        }

        public SsmCommandNotInitialisedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void checkForCriticalError(String migrationInstanceId) throws DatabaseMigrationFailure {
        String error = ssm.getSSMCommand(getCommandId(), migrationInstanceId).standardErrorContent();
        if(isCritical(error)) {
            logger.error("Encountered a critical error when performing DB restore: {}", error);
            throw new DatabaseMigrationFailure(error);
        }
    }

    public boolean isCritical(String error) {
        if(StringUtils.isNotBlank(error)) {
            return Arrays
                    .stream(CRITICAL_ERRORS)
                    .parallel()
                    .anyMatch(error::contains);
        }
        return false;
    }
}
