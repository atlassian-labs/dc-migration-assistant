package com.atlassian.migration.datacenter.core.fs;

import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import com.atlassian.scheduler.SchedulerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;

@Tag("integration")
@ExtendWith({LocalstackDockerExtension.class, MockitoExtension.class})
@LocalstackDockerProperties(services = {"s3"})
class S3FilesystemMigrationServiceIT {
    @TempDir
    Path dir;
    @Mock
    RegionService regionService;

    @Mock
    AtlassianPluginAWSCredentialsProvider credentialsProvider;

    @Mock
    MigrationService migrationService;

    @Mock
    JiraHome jiraHome;

    @Mock
    private SchedulerService schedulerService;

    @BeforeEach
    void setup() throws Exception {
        Path file = dir.resolve(UUID.randomUUID().toString());
        String rand = String.format("Testing string %s", Instant.now());
        Files.write(file, Collections.singleton(rand));
    }

    @Test
    void testSuccessfulDirectoryMigration(@TempDir Path dir) throws InvalidMigrationStageError {
        when(regionService.getRegion()).thenReturn(Region.US_EAST_1.toString());
        when(jiraHome.getHome()).thenReturn(dir.toFile());
        when(migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);

        FilesystemMigrationService fsService = new S3FilesystemMigrationService(regionService, credentialsProvider, jiraHome, migrationService, schedulerService);

        fsService.startMigration();

        Assertions.assertNotEquals(FilesystemMigrationStatus.FAILED, fsService.getReport().getStatus());
    }
}
