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
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3FilesystemMigrationServiceTest {

    @Mock
    JiraHome jiraHome;

    @Mock
    MigrationService migrationService;

    @Mock
    SchedulerService schedulerService;

    @Mock
    S3AsyncClient s3AsyncClient;

    @InjectMocks
    S3FilesystemMigrationService fsService;

    @Test
    void shouldFailToStartMigrationWhenSharedHomeDirectoryIsInvalid() throws InvalidMigrationStageError {
        Path nonexistentDir = Paths.get(UUID.randomUUID().toString());
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);
        when(jiraHome.getHome()).thenReturn(nonexistentDir.toFile());

        fsService.startMigration();

        assertEquals(FilesystemMigrationStatus.FAILED, fsService.getReport().getStatus());
        verify(migrationService).transition(MigrationStage.FS_MIGRATION_COPY, MigrationStage.WAIT_FS_MIGRATION_COPY);
    }

    @Test
    void shouldFailToStartMigrationWhenMigrationStageIsInvalid() throws InvalidMigrationStageError {
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);
        Mockito.doThrow(InvalidMigrationStageError.class).when(migrationService).transition(any(), any());

        assertThrows(InvalidMigrationStageError.class, () -> {
            fsService.startMigration();
        });

        assertEquals(FilesystemMigrationStatus.NOT_STARTED, fsService.getReport().getStatus());
    }

    @Test
    void shouldNotScheduleMigrationWhenCurrentMigrationStageIsNotFilesystemMigrationCopy() {
        Migration mockMigration = Mockito.mock(Migration.class);

        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
        when(mockMigration.getStage()).thenReturn(MigrationStage.NOT_STARTED);

        assertThrows(InvalidMigrationStageError.class, fsService::scheduleMigration);
    }

    @Test
    void shouldScheduleMigrationWhenCurrentMigrationStageIsFsCopy() throws Exception {
        createStubMigration(MigrationStage.FS_MIGRATION_COPY);

        Boolean isScheduled = fsService.scheduleMigration();
        assertEquals(true, isScheduled);

        verify(schedulerService).registerJobRunner(argThat(x -> x.compareTo(JobRunnerKey.of(S3UploadJobRunner.KEY)) == 0), any(S3UploadJobRunner.class));
        verify(schedulerService).scheduleJob(
                argThat(jobId -> jobId.compareTo(JobId.of(S3UploadJobRunner.KEY + 42)) == 0),
                argThat(jobConfig -> jobConfig.getRunMode() == RunMode.RUN_ONCE_PER_CLUSTER)
        );
        verify(migrationService).transition(MigrationStage.FS_MIGRATION_COPY, MigrationStage.WAIT_FS_MIGRATION_COPY);
    }

    @Test
    void shouldUnsetScheduledJobWhenSchedulerExceptionIsRaised() throws Exception {
        createStubMigration(MigrationStage.FS_MIGRATION_COPY);

        Mockito.doThrow(SchedulerServiceException.class)
                .when(schedulerService)
                .scheduleJob(any(), any());

        Boolean isScheduled = fsService.scheduleMigration();
        assertEquals(false, isScheduled);

        verify(migrationService).transition(MigrationStage.FS_MIGRATION_COPY, MigrationStage.WAIT_FS_MIGRATION_COPY);
        verify(schedulerService).unscheduleJob(argThat(jobId -> jobId.compareTo(JobId.of(S3UploadJobRunner.KEY + 42)) == 0));
        verify(migrationService).error();
    }

    @Test
    void shouldUnsetScheduledJobWhenInvalidMigrationStageExceptionIsRaised() throws Exception {
        createStubMigration(MigrationStage.FS_MIGRATION_COPY);

        Mockito.doThrow(InvalidMigrationStageError.class)
                .when(migrationService)
                .transition(any(), any());

        Boolean isScheduled = fsService.scheduleMigration();
        assertEquals(false, isScheduled);

        verify(schedulerService, never()).scheduleJob(any(), any());
        verify(schedulerService).unscheduleJob(argThat(jobId -> jobId.compareTo(JobId.of(S3UploadJobRunner.KEY + 42)) == 0));
        verify(migrationService).error();
    }

    @Test
    void shouldAbortRunningMigration() throws Exception {
        final FilesystemUploader uploader = mock(FilesystemUploader.class);
        when(migrationService.getCurrentStage()).thenReturn(MigrationStage.WAIT_FS_MIGRATION_COPY);
        FieldSetter.setField(fsService, fsService.getClass().getDeclaredField("fsUploader"), uploader);

        fsService.abortMigration();

        verify(uploader).abort();
        verify(migrationService).error();
        assertEquals(fsService.getReport().getStatus(), FilesystemMigrationStatus.FAILED);
    }

    @Test
    void throwExceptionWhenTryToAbortNonRunningMigration() {
        when(migrationService.getCurrentStage()).thenReturn(MigrationStage.AUTHENTICATION);

        assertThrows(InvalidMigrationStageError.class, () -> fsService.abortMigration());
    }

    private Migration createStubMigration(MigrationStage migrationStage) {
        Migration mockMigration = Mockito.mock(Migration.class);
        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
        when(mockMigration.getStage()).thenReturn(migrationStage);
        when(mockMigration.getID()).thenReturn(42);
        return mockMigration;
    }
}
