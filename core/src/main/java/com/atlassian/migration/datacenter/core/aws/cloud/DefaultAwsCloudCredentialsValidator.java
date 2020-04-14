package com.atlassian.migration.datacenter.core.aws.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.utils.Md5Utils;

public class DefaultAwsCloudCredentialsValidator implements CloudCredentialsValidator {
    private static Logger logger = LoggerFactory.getLogger(DefaultAwsCloudCredentialsValidator.class);

    /**
     * Verifies if supplied accessKey and secretAccessKey are valid.
     *
     * @param accessKeyId     an identifier for the entity accessing the cloud provider e.g. AWS access key ID
     * @param secretAccessKey the secret to authenticate the entity to the cloud provider e.g. AWS secret access key
     * @return true when supplied keys are valid. false when the any one of the keys, or both, are not valid.
     */
//    TODO: Evaluate if we can use the simulate policy API ( This does not validate IAM policy as defined here {@link https://docs.aws.amazon.com/IAM/latest/APIReference/API_SimulatePrincipalPolicy.html} as invoking that). Invoking this API requires passing in a required `policySourceARN` that points to the user/group/role for which the simulation has to run for. We do not accept this as an input from the user yet.
//    TODO: getCallerIdentity may not work for users using MFA and/or when the role does not have privileges to call getUser. Verify this
    @Override
    public Boolean validate(String accessKeyId, String secretAccessKey) {
        try {
            AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            StsClient stsClient = buildStsClient(awsBasicCredentials);
            GetCallerIdentityResponse callerIdentity = stsClient.getCallerIdentity();
            logCallerIdentityMetadata(callerIdentity);
            logger.debug("Successfully retrieved AWS credentials from ");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected StsClient buildStsClient(AwsBasicCredentials awsBasicCredentials) {
        return StsClient.builder().credentialsProvider(() -> awsBasicCredentials).build();
    }

    private static void logCallerIdentityMetadata(GetCallerIdentityResponse callerIdentity) {
        try {
            String userId = callerIdentity.userId();
            String userIdBase64 = Md5Utils.md5AsBase64(userId.getBytes());
            logger.debug("Credentials validation completed successfully for user (MD5, base64 encoded) - {}", userIdBase64);
        } catch (Exception e) {
            logger.debug("Error while computing md5 of user ID. Ignoring this error", e);
        }
    }
}
