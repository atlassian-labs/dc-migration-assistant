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
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloadManager;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.util.concurrent.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
    Supplier<S3AsyncClient> s3AsyncClientSupplier;

    @Mock
    S3SyncFileSystemDownloadManager downloadManager;

    @InjectMocks
    S3FilesystemMigrationService fsService;

    @Test
    void shouldFailToStartMigrationWhenSharedHomeDirectoryIsInvalid() throws InvalidMigrationStageError {
        Path nonexistentDir = Paths.get(UUID.randomUUID().toString());
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);
        when(jiraHome.getHome()).thenReturn(nonexistentDir.toFile());

        fsService.startMigration();

        verify(migrationService).transition(MigrationStage.FS_MIGRATION_COPY, MigrationStage.WAIT_FS_MIGRATION_COPY);
        verify(migrationService).error();
    }

    @Test
    void shouldFailToStartMigrationWhenMigrationAlreadyInProgress() throws InvalidMigrationStageError {
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.WAIT_FS_MIGRATION_COPY);
        when(this.jiraHome.getHome()).thenReturn(Paths.get("stub").toFile());

        fsService.startMigration();

        assertNull(fsService.getReport());
    }

    @Test
    void shouldNotScheduleMigrationWhenCurrentMigrationStageIsNotFilesystemMigrationCopy() {
        Migration mockMigration = Mockito.mock(Migration.class);

        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
        when(mockMigration.getStage()).thenReturn(MigrationStage.NOT_STARTED);

        Boolean isScheduled = fsService.scheduleMigration();
        assertEquals(false, isScheduled);
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
    }

    @Test
    void shouldUnsetScheduledJobWhenSchedulerExceptionIsRaised() throws Exception {
        createStubMigration(MigrationStage.FS_MIGRATION_COPY);

        Mockito.doThrow(SchedulerServiceException.class)
                .when(schedulerService)
                .scheduleJob(any(), any());

        Boolean isScheduled = fsService.scheduleMigration();
        assertEquals(false, isScheduled);

        verify(schedulerService).unscheduleJob(argThat(jobId -> jobId.compareTo(JobId.of(S3UploadJobRunner.KEY + 42)) == 0));
        verify(migrationService).error();
    }

    private Migration createStubMigration(MigrationStage migrationStage) {
        Migration mockMigration = Mockito.mock(Migration.class);
        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
        when(mockMigration.getStage()).thenReturn(migrationStage);
        when(mockMigration.getID()).thenReturn(42);
        return mockMigration;
    }
}
