package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.db.restore.RemoteServiceState;
import com.atlassian.migration.datacenter.core.aws.infrastructure.util.Ec2Api;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.google.common.collect.ImmutableMap;
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
import java.util.*;

@ExtendWith({MockitoExtension.class})
class RemoteInstanceCommandRunnerServiceIT {
    
    private static final String DOCKER_COMPOSE_FILE = "localstack/docker-compose.yml";
    private static final File dockerFile = new File(
            Objects.requireNonNull(RemoteInstanceCommandRunnerServiceIT.class.getClassLoader().getResource(DOCKER_COMPOSE_FILE)).getFile()
    );
    private static final DockerComposeContainer localstackForEc2 = new DockerComposeContainer(dockerFile);
    private static final String LOCAL_EC2_ENDPOINT = "http://localhost:4597";
    private static final String JIRA_STACK_NAME = "JIRA_STACK_001";
    private static final String AWS_RUN_SHELL_SCRIPT = "AWS-RunShellScript";
    private static final String SYSTEMCTL_START_JIRA = "sudo systemctl start jira";
    private static final String SYSTEMCTL_STOP_JIRA = "sudo systemctl stop jira";
    
    private RemoteInstanceCommandRunnerService remoteInstanceCommandRunnerService;


    @Mock
    MigrationContext migrationContext;

    @Mock
    MigrationService migrationService;

    @Mock
    private AwsCredentialsProvider mockCredentialsProvider;

    @Mock
    SSMApi ssmApi;

    String instanceId;

    @Mock
    private Ec2Client ec2Client;
    
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

         instanceId = Ec2Api.getInstanceId(JIRA_STACK_NAME, ec2Client).get();
    }

    @Test
    public void shouldCallStartOnStackInstance() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(JIRA_STACK_NAME);
        remoteInstanceCommandRunnerService.setJiraRunStateTo(RemoteServiceState.START);
        verify(ssmApi, times(1)).runSSMDocument(AWS_RUN_SHELL_SCRIPT, instanceId, ImmutableMap.of("commands", Collections.singletonList(SYSTEMCTL_START_JIRA)));
    }
    
    @Test
    public void shouldCallStopOnStackInstance() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(JIRA_STACK_NAME);
        remoteInstanceCommandRunnerService.setJiraRunStateTo(RemoteServiceState.STOP);
        verify(ssmApi, times(1)).runSSMDocument(AWS_RUN_SHELL_SCRIPT, instanceId, ImmutableMap.of("commands", Collections.singletonList(SYSTEMCTL_STOP_JIRA)));
    }
    
    @Test
    public void shouldNotCallStopOnStackInstanceIfMigrationStackNotFound() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn("NOT_JIRA_STACK");
        remoteInstanceCommandRunnerService.setJiraRunStateTo(RemoteServiceState.STOP);
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformStopIfNoEc2ClientAvailable() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> null);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(JIRA_STACK_NAME);
        remoteInstanceCommandRunnerService.setJiraRunStateTo(RemoteServiceState.STOP);
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformStopIfMigrationContextIsNull() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(null);
        remoteInstanceCommandRunnerService.setJiraRunStateTo(RemoteServiceState.STOP);
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformStopIfDeploymentIdIsNull() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(null);
        remoteInstanceCommandRunnerService.setJiraRunStateTo(RemoteServiceState.STOP);
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