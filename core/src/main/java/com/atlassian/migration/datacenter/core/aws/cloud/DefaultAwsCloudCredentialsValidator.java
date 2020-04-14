package com.atlassian.migration.datacenter.core.aws.cloud;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.ssm.SsmClient;

public class DefaultAwsCloudCredentialsValidator implements CloudCredentialsValidator {
    /**
     * Verifies if supplied accessKey and secretAccessKey are valid.
     *
     * @param accessKeyId an identifier for the entity accessing the cloud provider e.g. AWS access key ID
     * @param secretAccessKey the secret to authenticate the entity to the cloud provider e.g. AWS secret access key
     * @return true when supplied keys are valid. false when the any one of the keys, or both, are not valid.
     */
//    TODO: Evaluate if we can use the simulate policy API ( This does not validate IAM policy as defined here {@link https://docs.aws.amazon.com/IAM/latest/APIReference/API_SimulatePrincipalPolicy.html} as invoking that). Invoking this API requires passing in a required `policySourceARN` that points to the user/group/role for which the simulation has to run for. We do not accept this as an input from the user yet.
    @Override
    public Boolean validate(String accessKeyId, String secretAccessKey) {
        try {
            AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            CloudFormationAsyncClient.builder().credentialsProvider(() -> awsBasicCredentials).build();
            S3AsyncClient.builder().credentialsProvider(() -> awsBasicCredentials).build();
            SsmClient.builder().credentialsProvider(() -> awsBasicCredentials).build();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
