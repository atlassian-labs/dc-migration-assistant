package com.atlassian.migration.datacenter.core.aws.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

public class AtlassianPluginAWSCredentialsProvider implements AwsCredentialsProvider {

    private final ReadCredentialsService readCredentialsService;

    public AtlassianPluginAWSCredentialsProvider(ReadCredentialsService readCredentialsService) {
        this.readCredentialsService = readCredentialsService;
    }

    private boolean secretKeyIsDefined() {
        return readCredentialsService.getSecretAccessKey() != null && !readCredentialsService.getSecretAccessKey().equals("");
    }

    private boolean accessKeyIsDefined() {
        return readCredentialsService.getAccessKeyId() != null && !readCredentialsService.getAccessKeyId().equals("");
    }

    /**
     * AWS SDK V2 credentials API
     *
     * @return AWS Credentials to be used with SDK V2 clients
     */
    @Override
    public AwsCredentials resolveCredentials() {
        if (accessKeyIsDefined() && secretKeyIsDefined()) {
            return new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return readCredentialsService.getAccessKeyId();
                }

                @Override
                public String secretAccessKey() {
                    return readCredentialsService.getSecretAccessKey();
                }
            };
        }
        return DefaultCredentialsProvider.create().resolveCredentials();
    }
}
