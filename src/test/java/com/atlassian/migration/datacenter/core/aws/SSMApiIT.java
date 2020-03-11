package com.atlassian.migration.datacenter.core.aws;

import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.CommandInvocationStatus;
import software.amazon.awssdk.services.ssm.model.CommandStatus;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationRequest;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
//@ExtendWith({LocalstackDockerExtension.class})
//@LocalstackDockerProperties(services = {"cloudformation", "s3", "ssm"}, imageTag = "0.10.8")
public class SSMApiIT {

    private static final String LOCALSTACK_SSM_ENDPOINT = "http://localhost:4583";

    private SSMApi sut;

    @BeforeEach
    void setUp() {
        sut = new SSMApi(DefaultCredentialsProvider.create(), new RegionService() {
            @Override
            public String getRegion() {
                return "us-east-1";
            }

            @Override
            public void storeRegion(String string) throws InvalidAWSRegionException {

            }
        });
    }

    @Test
    @Disabled("Requires true authentication to AWS and a specific EC2 instance to be running")
    void shouldSendSsmCommandToAWS() {
        final String documentName = "AWS-RunShellScript";
        // Node ID of ConfluenceGiveMeAllTheData Confluence Node in us-east-1
        final String targetEc2InstanceId = "i-0d536acd983bade05";
        final HashMap<String, List<String>> commandParameters = new HashMap<String, List<String>>() {{
            put("commands", Collections.singletonList("echo 'hello, world'"));
        }};

        String commandId = sut.runSSMDocument(documentName, targetEc2InstanceId, commandParameters);

        assertNotNull(commandId);
    }

    @Test
    @Disabled("Requires true authentication to AWS")
    void shouldGetCommand() {
        // Command run by an earlier run of shouldSendSsmCommandToAWS
        final String commandID = "5c45181c-ed0a-45db-9fb7-3a4cd1edef9d";
        // Node ID of ConfluenceGiveMeAllTheData Confluence Node in us-east-1
        final String targetEc2InstanceId = "i-0d536acd983bade05";

        GetCommandInvocationResponse command = sut.getSSMCommand(commandID, targetEc2InstanceId);

        assertNotNull(command);

        assertEquals(CommandInvocationStatus.SUCCESS, command.status());
        assertEquals("hello, world\n", command.standardOutputContent());
    }

    @Test
    @Disabled("Localstack is giving 500s for SSM send-command")
    void shouldStartSSMCommand() {
        SsmClient client = SsmClient.builder()
                .endpointOverride(URI.create(LOCALSTACK_SSM_ENDPOINT))
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return TestUtils.TEST_ACCESS_KEY;
                    }

                    @Override
                    public String secretAccessKey() {
                        return TestUtils.TEST_SECRET_KEY;
                    }
                })
                .region(Region.of(TestUtils.DEFAULT_REGION))
                .build();

        SendCommandRequest request = SendCommandRequest.builder()
                .documentName("document")
                .documentVersion("$LATEST")
                .timeoutSeconds(600)
                .comment("running command to pull files down as part of migration")
                .parameters(new HashMap<>())
                .instanceIds("i-1234567")
                .outputS3BucketName("migration-bucket")
                .outputS3KeyPrefix("fs-copy-down-log")
                .build();
        client.sendCommand(request);
    }

    @Test
    @Disabled("Localstack is giving 400s for SSM getCommandInvocation")
    void shouldGetStatusOfRunningCommand() {
        SsmClient client = SsmClient.builder()
                .endpointOverride(URI.create(LOCALSTACK_SSM_ENDPOINT))
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return TestUtils.TEST_ACCESS_KEY;
                    }

                    @Override
                    public String secretAccessKey() {
                        return TestUtils.TEST_SECRET_KEY;
                    }
                })
                .region(Region.of(TestUtils.DEFAULT_REGION))
                .build();

        GetCommandInvocationRequest request = GetCommandInvocationRequest.builder()
                .commandId("commandID")
                .instanceId("theInstance")
                .build();

        GetCommandInvocationResponse response = client.getCommandInvocation(request);

        System.out.println(response.standardOutputContent());
    }

}