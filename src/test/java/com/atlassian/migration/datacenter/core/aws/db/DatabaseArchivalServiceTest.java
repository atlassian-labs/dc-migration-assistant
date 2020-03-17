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

import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseArchivalServiceTest {
    @TempDir
    Path tempDir;
    private DatabaseArchivalService service;
    @Mock
    private MigrationService migrationService;
    @Mock
    private DatabaseExtractor databaseExtractor;
    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        service = new DatabaseArchivalService(migrationService, databaseExtractor);
    }

    @Test
    void shouldArchiveDatabaseSuccessfully() throws Exception {
        when(this.databaseExtractor.startDatabaseDump(tempDir.resolve("db.dump"))).thenReturn(process);
        when(process.waitFor()).thenReturn(0);
        Path target = service.archiveDatabase(tempDir);
        assertTrue(target.endsWith("db.dump"));
        //TODO: Should this stage to transition to be called pending export?
        verify(this.migrationService).transition(MigrationStage.OFFLINE_WARNING, MigrationStage.DB_MIGRATION_EXPORT);
        verify(this.migrationService).transition(MigrationStage.DB_MIGRATION_EXPORT, MigrationStage.WAIT_DB_MIGRATION_EXPORT);
        verify(this.migrationService).transition(MigrationStage.WAIT_DB_MIGRATION_EXPORT, MigrationStage.DB_MIGRATION_UPLOAD);
    }

    @Test
    void shouldThrowExceptionWhenStateTransitionIsNotSuccessful() throws Exception {
        doThrow(InvalidMigrationStageError.class).when(migrationService).transition(MigrationStage.OFFLINE_WARNING, MigrationStage.DB_MIGRATION_EXPORT);

        assertThrows(InvalidMigrationStageError.class, () -> {
            service.archiveDatabase(tempDir);
        });
    }

    @Test
    void shouldThrowExceptionWhenProcessExecutionFails() throws Exception {
        when(this.databaseExtractor.startDatabaseDump(tempDir.resolve("db.dump"))).thenReturn(process);
        when(process.waitFor()).thenThrow(new InterruptedException());
        assertThrows(DatabaseMigrationFailure.class, () -> {
            service.archiveDatabase(tempDir);
        });
    }
}