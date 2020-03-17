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

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;

import java.nio.file.Path;

public class DatabaseArchivalService {

    private ApplicationConfiguration applicationConfiguration;
    private MigrationService migrationService;

    public DatabaseArchivalService(ApplicationConfiguration applicationConfiguration, MigrationService migrationService) {
        this.applicationConfiguration = applicationConfiguration;
        this.migrationService = migrationService;
    }

    public Path archiveDatabase(Path tempDirectory) throws InvalidMigrationStageError {
        DatabaseExtractor extractor = DatabaseExtractorFactory.getExtractor(applicationConfiguration);
        Path target = tempDirectory.resolve("db.dump");

        this.migrationService.transition(MigrationStage.OFFLINE_WARNING, MigrationStage.DB_MIGRATION_EXPORT);

        Process extractorProcess = extractor.startDatabaseDump(target);

        this.migrationService.transition(MigrationStage.DB_MIGRATION_EXPORT, MigrationStage.WAIT_DB_MIGRATION_EXPORT);
        try {
            extractorProcess.waitFor();
        } catch (Exception e) {
            String msg = "Error while waiting for DB extractor to finish";
            this.migrationService.error();
            throw new DatabaseMigrationFailure(msg, e);
        }

        this.migrationService.transition(MigrationStage.WAIT_DB_MIGRATION_EXPORT, MigrationStage.DB_MIGRATION_UPLOAD);
        return target;
    }
}
