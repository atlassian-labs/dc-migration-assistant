package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
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
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3FilesystemMigrationServiceTest {
    @Mock
    RegionService regionService;

    @Mock
    AtlassianPluginAWSCredentialsProvider credentialsProvider;

    @Mock
    JiraHome jiraHome;

    @Mock
    MigrationService migrationService;


    @Mock
    SchedulerService schedulerService;

    @InjectMocks
    S3FilesystemMigrationService fsService;

    @Test
    void shouldFailToStartMigrationWhenSharedHomeDirectoryIsInvalid() throws InvalidMigrationStageError {
        Path nonexistentDir = Paths.get(UUID.randomUUID().toString());
        when(this.migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);
        when(jiraHome.getHome()).thenReturn(nonexistentDir.toFile());
        when(regionService.getRegion()).thenReturn(Region.US_EAST_1.toString());

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

    private Migration createStubMigration(MigrationStage migrationStage) {
        Migration mockMigration = Mockito.mock(Migration.class);
        when(migrationService.getCurrentMigration()).thenReturn(mockMigration);
        when(mockMigration.getStage()).thenReturn(migrationStage);
        when(mockMigration.getID()).thenReturn(42);
        return mockMigration;
    }
}
