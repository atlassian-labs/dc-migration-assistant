package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.DockerComposeContainer;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import static org.mockito.Mockito.*;
import java.io.File;
import java.net.URI;
import java.util.Objects;

@ExtendWith({MockitoExtension.class})
class RemoteInstanceCommandRunnerServiceIT {
    
    private static final String DOCKER_COMPOSE_FILE = "localstack/docker-compose.yml";
    private static final File dockerFile = new File(
            Objects.requireNonNull(RemoteInstanceCommandRunnerServiceIT.class.getClassLoader().getResource(DOCKER_COMPOSE_FILE)).getFile()
    );
    private static final DockerComposeContainer localstackForEc2 = new DockerComposeContainer(dockerFile);
    private static final String LOCAL_EC2_ENDPOINT = "http://localhost:4597";
    public static final String JIRA_STACK_NAME = "JIRA_STACK_001";

    @Mock
    MigrationContext migrationContext;

    @Mock
    MigrationService migrationService;

    @Mock
    private AwsCredentialsProvider mockCredentialsProvider;

    @Mock
    SSMApi ssmApi;

    @Mock
    private Ec2Client ec2Client;

    private RemoteInstanceCommandRunnerService remoteInstanceCommandRunnerService;

    @BeforeAll
    public static void setup() throws InterruptedException {
        localstackForEc2.start();
        //Wait for container to come up
        Thread.sleep(10000);
    }

    @BeforeEach
    public void setUp() {
        ec2Client = Ec2Client.builder()
                .credentialsProvider(mockCredentialsProvider)
                .endpointOverride(URI.create(LOCAL_EC2_ENDPOINT))
                .region(Region.US_EAST_1)
                .build();

        when(mockCredentialsProvider.resolveCredentials()).thenReturn(new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return "fake-access-key";
            }

            @Override
            public String secretAccessKey() {
                return "fake-secret-key";
            }
        });
        createEC2Instance(ec2Client);
    }
    
    @Test
    public void shouldCallRestartOnStackInstance() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(JIRA_STACK_NAME);
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(1)).runSSMDocument(anyString(), anyString(), any());
    }
    
    @Test
    public void shouldNotCallRestartOnStackInstanceIfMigrationStackNotFound() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn("NOT_JIRA_STACK");
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformRestartIfNoEc2ClientAvailable() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> null);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(JIRA_STACK_NAME);
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformRestartIfMigrationContextIsNull() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(null);
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformRestartIfDeploymentIdIsNull() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(null);
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    private void createEC2Instance(Ec2Client ec2) {

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId("ami-031a03cb800ecb0d5")
                .instanceType(InstanceType.T1_MICRO)
                .build();

        RunInstancesResponse response = ec2.runInstances(runRequest);
        String instanceId = response.instances().get(0).instanceId();

        Tag tag = Tag.builder()
                .key("tag:aws:cloudformation:stack-name")
                .value("JIRA_STACK_001")
                .build();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(tag)
                .build();

        ec2.createTags(tagRequest);
    }
}