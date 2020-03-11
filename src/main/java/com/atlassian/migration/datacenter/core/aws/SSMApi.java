package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationRequest;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;

import java.util.List;
import java.util.Map;

public class SSMApi {

    private final AwsCredentialsProvider credentialsService;
    private final RegionService regionService;

    // This may be null, use getClient() to safely get a non-null SSM client
    private SsmClient client;

    public SSMApi(AwsCredentialsProvider credentialsService, RegionService regionService) {
        this.credentialsService = credentialsService;
        this.regionService = regionService;
    }

    /**
     * Runs an SSM automatiom document against a specific EC2 instance with the specified parameters.
     * @param documentName The name of the document to run. The latest version will be used.
     * @param targetEc2InstanceId The instance ID of the EC2 instance to run this command on
     * @param commandParameters The parameters for the command. The key should be the parameter name and the value should
     *                          be a list filled with the lines to be used as the parameter value. If the parameter-type
     *                          is a plain string then the list only have one element in it.
     * @return the command ID of the invoked command.
     */
    public String runSSMDocument(String documentName, String targetEc2InstanceId, Map<String, List<String>> commandParameters) {
        SendCommandRequest request = SendCommandRequest.builder()
                .documentName(documentName)
                .documentVersion("$LATEST")
                .instanceIds(targetEc2InstanceId)
                .parameters(commandParameters)
                .timeoutSeconds(600)
                .comment("command run by Jira DC Migration Assistant")
                // FIXME: Pending migration stack
                .outputS3BucketName(System.getProperty("ssmDocumentLoggingBucket", "migration-bucket"))
                .outputS3KeyPrefix("trebuchet-ssm-document-logs")
                // END FIXME
                .build();

        SendCommandResponse response = getClient().sendCommand(request);

        return response.command().commandId();
    }

    /**
     * Gets the invocation of the specified command on the specified EC2 instance. You will want
     * to check the details of the command yourself. Noteworthy response fields include {@link GetCommandInvocationResponse#status()},
     * {@link GetCommandInvocationResponse#standardOutputContent()}
     *
     * @param commandId The id of the command from calling {@link SSMApi#runSSMDocument(String, String, Map)}
     * @param targetEc2InstanceId the EC2 instance the command is running on. Should be the same as the targetEc2InstanceId from calling {@link SSMApi#runSSMDocument(String, String, Map)}
     * @return The response from the AWS SDK v2
     *
     * @throws software.amazon.awssdk.services.ssm.model.InvalidCommandIdException (RuntimeException) - when the command ID is not a valid SSM Command ID
     * @throws software.amazon.awssdk.services.ssm.model.InvocationDoesNotExistException (RuntimeException) - when the command invocation (combination of command ID and instance ID) does not exist
     *
     * @see SsmClient#getCommandInvocation(GetCommandInvocationRequest) for other exception details
     */
    public GetCommandInvocationResponse getSSMCommand(String commandId, String targetEc2InstanceId) {
        GetCommandInvocationRequest request = GetCommandInvocationRequest.builder()
                .commandId(commandId)
                .instanceId(targetEc2InstanceId)
                .build();

        GetCommandInvocationResponse response = getClient().getCommandInvocation(request);

        return response;
    }

    // Lazily instantiates the ssm client
    private SsmClient getClient() {
        if (client == null) {
            client = SsmClient.builder()
                    .credentialsProvider(credentialsService)
                    .region(Region.of(regionService.getRegion()))
                    .build();
        }

        return client;
    }
}
