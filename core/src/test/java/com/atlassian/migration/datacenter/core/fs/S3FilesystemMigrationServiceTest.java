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

import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.datacenter.core.fs.copy.S3BulkCopy;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloadManager;
import com.atlassian.migration.datacenter.core.fs.jira.captor.AttachmentCaptor;
import com.atlassian.migration.datacenter.core.fs.jira.listener.JiraIssueAttachmentListener;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import com.atlassian.scheduler.config.JobId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3FilesystemMigrationServiceTest {

    @Mock
    MigrationService migrationService;

    @Mock
    MigrationRunner migrationRunner;

    @Mock
    S3SyncFileSystemDownloadManager downloadManager;

    @Mock
    Environment mockEnv;

    JiraIssueAttachmentListener attachmentListener;

    @Mock
    S3BulkCopy bulkCopy;

    @InjectMocks
    S3FilesystemMigrationService fsService;

    FileSystemMigrationReportManager reportManager = new DefaultFileSystemMigrationReportManager();

    @BeforeEach
    void setUp() {
        attachmentListener = new JiraIssueAttachmentListener(
                mock(EventPublisher.class),
                mock(AttachmentCaptor.class)
        );
        fsService = new S3FilesystemMigrationService(mockEnv, downloadManager, migrationService, migrationRunner, attachmentListener, bulkCopy, reportManager);
    }

    @Test
    void shouldStartAttachmentListener() throws InvalidMigrationStageError {
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);

        fsService.startMigration();

        assertTrue(attachmentListener.isStarted(), "attachment listener was not started");
    }

    @Test
    void shouldFailToStartMigrationWhenSharedHomeDirectoryIsInvalid() throws InvalidMigrationStageError, FileUploadException
    {
        final String errorMessage = "Failed to migrate content. File not found: abc";
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);
        doThrow(
            new FileUploadException(errorMessage)
        ).when(bulkCopy).copySharedHomeToS3();

        fsService.startMigration();

        verify(migrationService).transition(MigrationStage.FS_MIGRATION_COPY_WAIT);
        verify(migrationService).error(errorMessage);
    }

    @Test
    void shouldFailToStartMigrationWhenMigrationStageIsInvalid() throws InvalidMigrationStageError {
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);
        Mockito.doThrow(InvalidMigrationStageError.class).when(migrationService).transition(any());
        assertThrows(InvalidMigrationStageError.class, () -> {
            fsService.startMigration();
        });

        FileSystemMigrationReport report = reportManager.getCurrentReport(ReportType.Filesystem);
        assertEquals(FilesystemMigrationStatus.NOT_STARTED, report.getStatus());
    }

    @Test
    void shouldFailToStartMigrationWhenMigrationAlreadyInProgress() throws InvalidMigrationStageError {
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY_WAIT);

        fsService.startMigration();

        FileSystemMigrationReport report = reportManager.getCurrentReport(ReportType.Filesystem);
        assertEquals(report.getStatus(), FilesystemMigrationStatus.NOT_STARTED);
    }

    @Test
    void shouldNotScheduleMigrationWhenCurrentMigrationStageIsNotFilesystemMigrationCopy() throws InvalidMigrationStageError {
        doThrow(new InvalidMigrationStageError("wrong stage")).when(migrationService).assertCurrentStage(MigrationStage.FS_MIGRATION_COPY);

        assertThrows(InvalidMigrationStageError.class, fsService::scheduleMigration);
    }

    @Test
    void shouldScheduleMigrationWhenCurrentMigrationStageIsFsCopy() throws Exception {
        createStubMigration();

        when(migrationRunner.runMigration(any(), any())).thenReturn(true);

        Boolean isScheduled = fsService.scheduleMigration();
        assertTrue(isScheduled);
        verify(migrationService).assertCurrentStage(MigrationStage.FS_MIGRATION_COPY);
    }

    @Test
    void shouldAbortRunningMigration() throws Exception {
        mockJobDetailsAndMigration(MigrationStage.FS_MIGRATION_COPY_WAIT);

        fsService.abortMigration();

        verify(migrationService).error("File system migration was aborted");
        FileSystemMigrationReport report = reportManager.getCurrentReport(ReportType.Filesystem);
        assertEquals(report.getStatus(), FilesystemMigrationStatus.FAILED);
    }

    @Test
    void shouldUnscheduleMigrationGivenMigrationId() throws Exception {
        int migrationId = 42;

        fsService.unscheduleMigration(migrationId);

        verify(migrationRunner).abortJobIfPresent(argThat(argument -> argument.equals(JobId.of(S3UploadJobRunner.KEY + migrationId))));
    }

    @Test
    void throwExceptionWhenTryToAbortNonRunningMigration() {
        mockJobDetailsAndMigration(MigrationStage.AUTHENTICATION);

        assertThrows(InvalidMigrationStageError.class, () -> fsService.abortMigration());
    }

    private Migration createStubMigration() {
        Migration mockMigration = mock(Migration.class);
        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
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
