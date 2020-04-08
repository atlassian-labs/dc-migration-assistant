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

import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.aws.ssm.SuccessfulSSMCommandConsumer;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.EnsureSuccessfulSSMCommandConsumer;
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;

import java.util.Collections;

public class SsmPsqlDatabaseRestoreService {

    private final int maxCommandRetries;

    private final SSMApi ssm;
    private final AWSMigrationHelperDeploymentService migrationHelperDeploymentService;

    SsmPsqlDatabaseRestoreService(SSMApi ssm, int maxCommandRetries, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        this.ssm = ssm;
        this.maxCommandRetries = maxCommandRetries;
        this.migrationHelperDeploymentService = migrationHelperDeploymentService;
    }

    public SsmPsqlDatabaseRestoreService(SSMApi ssm, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        this(ssm, 10, migrationHelperDeploymentService);
    }

    public void restoreDatabase(DatabaseRestoreStageTransitionCallback restoreStageTransitionCallback) throws DatabaseMigrationFailure, InvalidMigrationStageError {
        String dbRestorePlaybook = migrationHelperDeploymentService.getDbRestoreDocument();
        String migrationInstanceId = migrationHelperDeploymentService.getMigrationHostInstanceId();

        restoreStageTransitionCallback.assertInStartingStage();

        String commandId = ssm.runSSMDocument(dbRestorePlaybook, migrationInstanceId, Collections.emptyMap());

        SuccessfulSSMCommandConsumer consumer = new EnsureSuccessfulSSMCommandConsumer(ssm, commandId, migrationInstanceId);

        restoreStageTransitionCallback.transitionToServiceWaitStage();

        try {
            consumer.handleCommandOutput(maxCommandRetries);
            restoreStageTransitionCallback.transitionToServiceNextStage();
        } catch (SuccessfulSSMCommandConsumer.UnsuccessfulSSMCommandInvocationException | SuccessfulSSMCommandConsumer.SSMCommandInvocationProcessingError e) {
            restoreStageTransitionCallback.transitionToServiceErrorStage();
            throw new DatabaseMigrationFailure("Unable to invoke database download command", e);
        }
    }

}
