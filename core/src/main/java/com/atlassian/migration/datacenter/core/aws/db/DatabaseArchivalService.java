/*
 * Copyright (c) 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and  limitations under the License.
 */

package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.aws.MigrationStageCallback;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;

import java.nio.file.Path;

public class DatabaseArchivalService {

    private DatabaseExtractor databaseExtractor;
    private MigrationStageCallback migrationStageCallback;

    public DatabaseArchivalService(DatabaseExtractor databaseExtractor, MigrationStageCallback migrationStageCallback) {
        this.databaseExtractor = databaseExtractor;
        this.migrationStageCallback = migrationStageCallback;
    }

    public Path archiveDatabase(Path tempDirectory) throws InvalidMigrationStageError {
        Path target = tempDirectory.resolve("db.dump");

        this.migrationStageCallback.assertInStartingStage();

        Process extractorProcess = this.databaseExtractor.startDatabaseDump(target);
        this.migrationStageCallback.transitionToServiceWaitStage();

        try {
            extractorProcess.waitFor();
        } catch (Exception e) {
            String msg = "Error while waiting for DB extractor to finish";
            this.migrationStageCallback.transitionToServiceErrorStage(e.getMessage());
            throw new DatabaseMigrationFailure(msg, e);
        }

        this.migrationStageCallback.transitionToServiceNextStage();
        return target;
    }
}

