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
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;

public class DatabaseRestoreStageTransitionCallback implements MigrationStageCallback {

    private final MigrationService migrationService;

    public DatabaseRestoreStageTransitionCallback(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void assertInStartingStage() throws InvalidMigrationStageError {
        migrationService.assertCurrentStage(MigrationStage.DATA_MIGRATION_IMPORT);
    }

    @Override
    public void transitionToServiceWaitStage() throws InvalidMigrationStageError {
        migrationService.transition(MigrationStage.DATA_MIGRATION_IMPORT_WAIT);
    }

    @Override
    public void transitionToServiceNextStage() throws InvalidMigrationStageError {
        migrationService.transition(MigrationStage.VALIDATE);
    }

    @Override
    public void transitionToServiceErrorStage() {
        migrationService.error();
    }
}
