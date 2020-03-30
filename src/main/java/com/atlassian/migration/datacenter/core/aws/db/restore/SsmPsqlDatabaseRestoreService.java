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

import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.aws.ssm.SuccessfulSSMCommandConsumer;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.EnsureSuccessfulSSMCommandConsumer;

import java.util.Collections;

public class SsmPsqlDatabaseRestoreService {

    // FIXME: These values should should come from the migration stack
    private static final String SSM_PLAYBOOK = System.getProperty("com.atlassian.migration.psql.documentName", "pending implementation");
    private static final String MIGRATION_STACK_INSTANCE = System.getProperty("com.atlassian.migration.instanceId", "i-0353cc9a8ad7dafc2");

    private final int maxCommandRetries;

    private final SSMApi ssm;

    public SsmPsqlDatabaseRestoreService(SSMApi ssm, int maxCommandRetries) {
        this.ssm = ssm;
        this.maxCommandRetries = maxCommandRetries;
    }

    public SsmPsqlDatabaseRestoreService(SSMApi ssm) {
        this(ssm, 10);
    }

    public void restoreDatabase(DatabaseRestoreStageTransitionCallback restoreStageTransitionCallback) throws DatabaseMigrationFailure, InvalidMigrationStageError {
        restoreStageTransitionCallback.assertInStartingStage();

        String commandId = ssm.runSSMDocument(SSM_PLAYBOOK, MIGRATION_STACK_INSTANCE, Collections.emptyMap());

        SuccessfulSSMCommandConsumer consumer = new EnsureSuccessfulSSMCommandConsumer(ssm, commandId, MIGRATION_STACK_INSTANCE);

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
