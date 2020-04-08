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

package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloadManager;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.util.concurrent.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3FilesystemMigrationServiceTest {

    @Mock
    JiraHome jiraHome;

    @Mock
    MigrationService migrationService;

    @Mock
    MigrationRunner migrationRunner;

    @Mock
    Supplier<S3AsyncClient> s3AsyncClientSupplier;

    @Mock
    S3SyncFileSystemDownloadManager downloadManager;

    @Mock
    AWSMigrationHelperDeploymentService migrationHelperDeploymentService;

    @InjectMocks
    S3FilesystemMigrationService fsService;

    @Test
    void shouldFailToStartMigrationWhenSharedHomeDirectoryIsInvalid() throws InvalidMigrationStageError
    {
        Path nonexistentDir = Paths.get(UUID.randomUUID().toString());
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);
        when(jiraHome.getHome()).thenReturn(nonexistentDir.toFile());

        fsService.startMigration();

        verify(migrationService).transition(MigrationStage.FS_MIGRATION_COPY_WAIT);
        verify(migrationService).error();
    }

    @Test
    void shouldFailToStartMigrationWhenMigrationStageIsInvalid() throws InvalidMigrationStageError {
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);
        when(this.jiraHome.getHome()).thenReturn(Paths.get("stub").toFile());
        Mockito.doThrow(InvalidMigrationStageError.class).when(migrationService).transition(any());
        assertThrows(InvalidMigrationStageError.class, () -> {
            fsService.startMigration();
        });

        assertEquals(FilesystemMigrationStatus.NOT_STARTED, fsService.getReport().getStatus());
    }

    @Test
    void shouldFailToStartMigrationWhenMigrationAlreadyInProgress() throws InvalidMigrationStageError {
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY_WAIT);
        when(this.jiraHome.getHome()).thenReturn(Paths.get("stub").toFile());

        fsService.startMigration();

        assertEquals(fsService.getReport().getStatus(), FilesystemMigrationStatus.NOT_STARTED);
    }

    @Test
    void shouldNotScheduleMigrationWhenCurrentMigrationStageIsNotFilesystemMigrationCopy() {
        Migration mockMigration = mock(Migration.class);

        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
        when(mockMigration.getStage()).thenReturn(MigrationStage.NOT_STARTED);

        assertThrows(InvalidMigrationStageError.class, fsService::scheduleMigration);
    }

    @Test
    void shouldScheduleMigrationWhenCurrentMigrationStageIsFsCopy() throws Exception {
        createStubMigration(MigrationStage.FS_MIGRATION_COPY);

        JobId id = JobId.of(S3UploadJobRunner.KEY + 42);
        when(migrationRunner.runMigration(any(), any())).thenReturn(true);

        Boolean isScheduled = fsService.scheduleMigration();
        assertTrue(isScheduled);
    }


    @Test
    void shouldAbortRunningMigration() throws Exception {
        mockJobDetailsAndMigration(MigrationStage.FS_MIGRATION_COPY_WAIT);

        final FilesystemUploader uploader = mock(FilesystemUploader.class);
        FieldSetter.setField(fsService, fsService.getClass().getDeclaredField("fsUploader"), uploader);

        fsService.abortMigration();

        verify(uploader).abort();
        verify(migrationService).error();
        assertEquals(fsService.getReport().getStatus(), FilesystemMigrationStatus.FAILED);
    }

    @Test
    void throwExceptionWhenTryToAbortNonRunningMigration() {
        mockJobDetailsAndMigration(MigrationStage.AUTHENTICATION);

        assertThrows(InvalidMigrationStageError.class, () -> fsService.abortMigration());
    }


    private Migration createStubMigration(MigrationStage migrationStage) {
        Migration mockMigration = mock(Migration.class);
        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
        when(mockMigration.getStage()).thenReturn(migrationStage);
        when(mockMigration.getID()).thenReturn(42);
        return mockMigration;
    }

    private void mockJobDetailsAndMigration(MigrationStage migrationStage) {
        Migration mockMigration = mock(Migration.class);
        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
        when(mockMigration.getID()).thenReturn(2);
        when(migrationService.getCurrentStage()).thenReturn(migrationStage);
    }
}
