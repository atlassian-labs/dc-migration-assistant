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
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloadManager;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import com.atlassian.migration.datacenter.util.AwsCredentialsProviderShim;
import com.atlassian.scheduler.SchedulerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Tag("integration")
@Testcontainers
@ExtendWith({MockitoExtension.class})
class S3FilesystemMigrationServiceIT {
    @TempDir Path dir;
    @Mock RegionService regionService;
    @Mock MigrationService migrationService;
    @Mock JiraHome jiraHome;
    @Mock SchedulerService schedulerService;
    @Mock S3SyncFileSystemDownloadManager fileSystemDownloader;

    private S3AsyncClient s3AsyncClient;
    private String bucket = "trebuchet-testing";

    @Container
    public LocalStackContainer s3 = new LocalStackContainer()
        .withServices(S3)
        .withEnv("DEFAULT_REGION", Region.US_EAST_1.toString());

    AtlassianPluginAWSCredentialsProvider credentialsProvider;

    @BeforeEach
    void setup() throws Exception {
        credentialsProvider = new AtlassianPluginAWSCredentialsProvider(new AwsCredentialsProviderShim(s3.getDefaultCredentialsProvider()));

        s3AsyncClient = S3AsyncClient.builder()
            .endpointOverride(new URI(s3.getEndpointConfiguration(S3).getServiceEndpoint()))
            .credentialsProvider(new AwsCredentialsProviderShim(s3.getDefaultCredentialsProvider()))
            .region(Region.US_EAST_1)
            .build();
        CreateBucketRequest req = CreateBucketRequest.builder()
            .bucket(bucket)
            .build();
        CreateBucketResponse resp = s3AsyncClient.createBucket(req).get();
        assertTrue(resp.sdkHttpResponse().isSuccessful());
    }

    private Path genRandFile() throws IOException
    {
        Path file = dir.resolve(UUID.randomUUID().toString());
        String rand = String.format("Testing string %s", Instant.now());
        Files.write(file, Collections.singleton(rand));
        return file;
    }

    @Test
    void testSuccessfulDirectoryMigration(@TempDir Path dir) throws Exception
    {
        when(jiraHome.getHome()).thenReturn(dir.toFile());
        when(migrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY);

        Path file = genRandFile();

         S3FilesystemMigrationService fsService = new S3FilesystemMigrationService(() -> s3AsyncClient, jiraHome, fileSystemDownloader, migrationService, schedulerService);
         fsService.postConstruct();

        fsService.startMigration();

        Assertions.assertNotEquals(FilesystemMigrationStatus.FAILED, fsService.getReport().getStatus());

        HeadObjectRequest req = HeadObjectRequest.builder()
            .bucket(bucket)
            .key(file.getFileName().toString())
            .build();
        HeadObjectResponse resp = s3AsyncClient.headObject(req).get();
        assertTrue(resp.sdkHttpResponse().isSuccessful());

    }
}
