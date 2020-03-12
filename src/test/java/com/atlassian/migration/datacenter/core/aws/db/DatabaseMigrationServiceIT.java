package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.util.AwsCredentialsProviderShim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

/**
 * Copyright Atlassian: 12/03/2020
 */
@Testcontainers
@ExtendWith(MockitoExtension.class)
class DatabaseMigrationServiceIT
{
    @Container
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
        .withDatabaseName("jira")
        .withCopyFileToContainer(MountableFile.forClasspathResource("db/jira.sql"), "/docker-entrypoint-initdb.d/jira.sql");

    @Container
    public LocalStackContainer s3 = new LocalStackContainer()
        .withServices(S3)
        .withEnv("DEFAULT_REGION", regionService.getRegion());

    private static RegionService regionService = new RegionService() {
        public String getRegion() { return "us-east-1"; }
        public void storeRegion(String string) throws InvalidAWSRegionException { throw new UnsupportedOperationException(); }
    };

    private AwsCredentialsProvider credentialsProvider = new AwsCredentialsProviderShim(s3.getDefaultCredentialsProvider());
    private S3Client s3client;
    private String bucket = "trebuchet-testing";

    @Mock(lenient = true)
    ApplicationConfiguration configuration;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws URISyntaxException
    {
        when(configuration.getDatabaseConfiguration())
            .thenReturn(new DatabaseConfiguration(DatabaseConfiguration.DBType.POSTGRESQL,
                                                  postgres.getContainerIpAddress(),
                                                  postgres.getMappedPort(5432),
                                                  postgres.getDatabaseName(),
                                                  postgres.getUsername(),
                                                  postgres.getPassword()));

        s3client = S3Client.builder()
            .endpointOverride(new URI(s3.getEndpointConfiguration(S3).getServiceEndpoint()))
            .credentialsProvider(credentialsProvider)
            .region(Region.of(regionService.getRegion()))
            .build();
        CreateBucketRequest req = CreateBucketRequest.builder()
            .bucket(bucket)
            .build();
        CreateBucketResponse resp = s3client.createBucket(req);
        assertTrue(resp.sdkHttpResponse().isSuccessful());
    }


    @Test
    void testDatabaseMigration()
    {
        DatabaseMigrationService service = new DatabaseMigrationService(configuration, credentialsProvider, regionService, tempDir,
                                                                        URI.create(s3.getEndpointConfiguration(S3).getServiceEndpoint()));
        service.performMigration();

        HeadObjectRequest req = HeadObjectRequest.builder()
            .bucket(bucket)
            .key("db.dump/toc.dat")
            .build();
        HeadObjectResponse resp = s3client.headObject(req);
        assertTrue(resp.sdkHttpResponse().isSuccessful());
    }

}