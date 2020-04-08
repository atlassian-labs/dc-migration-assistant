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

package com.atlassian.migration.datacenter.core.fs.download.s3sync;

import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.aws.ssm.SuccessfulSSMCommandConsumer;
import com.atlassian.migration.datacenter.spi.exceptions.FileSystemMigrationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class S3SyncFileSystemDownloader {

    private static final Logger logger = LoggerFactory.getLogger(S3SyncFileSystemDownloader.class);

    private int maxCommandStatusRetries;

    private final SSMApi ssmApi;
    private final AWSMigrationHelperDeploymentService migrationHelperDeploymentService;

    public S3SyncFileSystemDownloader(SSMApi ssmApi, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        this(ssmApi, migrationHelperDeploymentService, 10);
    }

    S3SyncFileSystemDownloader(SSMApi ssmApi, AWSMigrationHelperDeploymentService migrationHelperDeploymentService, int maxCommandStatusRetries) {
        this.ssmApi = ssmApi;
        this.maxCommandStatusRetries = maxCommandStatusRetries;
        this.migrationHelperDeploymentService = migrationHelperDeploymentService;
    }

    public void initiateFileSystemDownload() throws CannotLaunchCommandException {
        String fsRestoreDocument = migrationHelperDeploymentService.getFsRestoreDocument();
        String migrationHost = getMigrationHostId();

        String commandID = ssmApi.runSSMDocument(fsRestoreDocument, migrationHost, Collections.emptyMap());

        SuccessfulSSMCommandConsumer consumer = new EnsureSuccessfulSSMCommandConsumer(ssmApi, commandID, migrationHost);

        try {
            consumer.handleCommandOutput(maxCommandStatusRetries);
        } catch (SuccessfulSSMCommandConsumer.UnsuccessfulSSMCommandInvocationException e) {
            logger.error("error launching s3 sync command", e);
            throw new CannotLaunchCommandException("unable to launch file system download command successfully.", e);
        } catch (SuccessfulSSMCommandConsumer.SSMCommandInvocationProcessingError never) {
        }
    }

    /**
     * Gets the current status of the running download in the new stack
     *
     * @return the status of the S3 sync or null if the status was not able to be retrieved.
     */
    public S3SyncCommandStatus getFileSystemDownloadStatus() {
        String fsRestoreStatusDocument = migrationHelperDeploymentService.getFsRestoreStatusDocument();
        String migrationHostId = getMigrationHostId();

        String statusCommandId = ssmApi.runSSMDocument(fsRestoreStatusDocument, migrationHostId, Collections.emptyMap());

        SuccessfulSSMCommandConsumer<S3SyncCommandStatus> consumer = new UnmarshalS3SyncStatusSSMCommandConsumer(ssmApi, statusCommandId, migrationHostId);

        try {
            return consumer.handleCommandOutput(maxCommandStatusRetries);
        } catch (SuccessfulSSMCommandConsumer.UnsuccessfulSSMCommandInvocationException e) {
            logger.error("Status command did not complete successfully", e);
            return null;
        } catch (SuccessfulSSMCommandConsumer.SSMCommandInvocationProcessingError e) {
            logger.error("Unable to read status of s3 sync command", e);
            return null;
        }
    }

    private String getMigrationHostId() {
        return migrationHelperDeploymentService.getMigrationHostInstanceId();
    }

    public static class IndeterminateS3SyncStatusException extends FileSystemMigrationFailure {

        IndeterminateS3SyncStatusException(String message) {
            super(message);
        }

        public IndeterminateS3SyncStatusException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class CannotLaunchCommandException extends FileSystemMigrationFailure {
        CannotLaunchCommandException(String message) {
            super(message);
        }

        public CannotLaunchCommandException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
