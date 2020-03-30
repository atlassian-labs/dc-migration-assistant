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
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
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
    private DatabaseExtractor databaseExtractor;
    @Mock
    private Process process;
    @Mock
    private MigrationStageCallback migrationStageCallback;

    @BeforeEach
    void setUp() {
        service = new DatabaseArchivalService(databaseExtractor);
    }

    @Test
    void shouldArchiveDatabaseSuccessfully() throws Exception {
        when(this.databaseExtractor.startDatabaseDump(tempDir.resolve("db.dump"))).thenReturn(process);
        when(process.waitFor()).thenReturn(0);
        Path target = service.archiveDatabase(tempDir, migrationStageCallback);
        assertTrue(target.endsWith("db.dump"));

        verify(this.migrationStageCallback).assertInStartingStage();
        verify(this.migrationStageCallback).transitionToServiceWaitStage();
        verify(this.migrationStageCallback).transitionToServiceNextStage();
    }

    @Test
    void shouldThrowExceptionWhenStateTransitionToStartStageIsNotSuccessful() throws Exception {
        doThrow(InvalidMigrationStageError.class).when(migrationStageCallback).assertInStartingStage();

        assertThrows(InvalidMigrationStageError.class, () -> {
            service.archiveDatabase(tempDir, migrationStageCallback);
        });
    }

    @Test
    void shouldThrowExceptionWhenStateTransitionToWaitStageIsNotSuccessful() throws Exception {
        doThrow(InvalidMigrationStageError.class).when(migrationStageCallback).transitionToServiceWaitStage();

        assertThrows(InvalidMigrationStageError.class, () -> {
            service.archiveDatabase(tempDir, migrationStageCallback);
        });
        verify(migrationStageCallback).assertInStartingStage();
    }


    @Test
    void shouldThrowExceptionWhenStateTransitionToEndStageIsNotSuccessful() throws Exception {
        when(this.databaseExtractor.startDatabaseDump(tempDir.resolve("db.dump"))).thenReturn(process);
        when(process.waitFor()).thenReturn(0);
        doThrow(InvalidMigrationStageError.class).when(migrationStageCallback).transitionToServiceNextStage();

        assertThrows(InvalidMigrationStageError.class, () -> {
            service.archiveDatabase(tempDir, migrationStageCallback);
        });

        verify(migrationStageCallback).assertInStartingStage();
        verify(migrationStageCallback).transitionToServiceWaitStage();
    }

    @Test
    void shouldThrowExceptionWhenProcessExecutionFails() throws Exception {
        when(this.databaseExtractor.startDatabaseDump(tempDir.resolve("db.dump"))).thenReturn(process);
        when(process.waitFor()).thenThrow(new InterruptedException());

        assertThrows(DatabaseMigrationFailure.class, () -> {
            service.archiveDatabase(tempDir, migrationStageCallback);
        });

        verify(migrationStageCallback).assertInStartingStage();
        verify(migrationStageCallback).transitionToServiceWaitStage();
        verify(migrationStageCallback).transitionToServiceErrorStage();
    }
}