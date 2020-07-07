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

package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.aws.MigrationStageCallback;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class DatabaseArchivalService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseExtractorFactory databaseExtractorFactory;
    private MigrationStageCallback migrationStageCallback;

    public DatabaseArchivalService(DatabaseExtractorFactory databaseExtractorFactory, MigrationStageCallback migrationStageCallback) {
        this.databaseExtractorFactory = databaseExtractorFactory;
        this.migrationStageCallback = migrationStageCallback;
    }

    public Path archiveDatabase(Path tempDirectory) throws InvalidMigrationStageError {
        Path target = tempDirectory.resolve("db.dump");
        logger.info("Dumping database to "+target);

        this.migrationStageCallback.assertInStartingStage();

        DatabaseExtractor databaseExtractor = databaseExtractorFactory.getExtractor();
        Process extractorProcess = databaseExtractor.startDatabaseDump(target);
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

