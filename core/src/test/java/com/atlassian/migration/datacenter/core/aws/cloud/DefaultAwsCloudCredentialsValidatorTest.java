package com.atlassian.migration.datacenter.core.aws.cloud;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.sts.model.StsException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultAwsCloudCredentialsValidatorTest {

    @Mock
    StsClient stsClient;

    @Test
    void shouldValidateSuccessfullyWhenCredentialsAreValid() {
        when(stsClient.getCallerIdentity()).thenReturn(GetCallerIdentityResponse.builder().userId("foo").build());
        Boolean isValid = new StubAwsCloudCredentialsValidator(stsClient).validate("valid-foo", "valid-bar");
        Assertions.assertTrue(isValid);
    }

    @Test
    void shouldNotValidateWhenCredentialsAreInValid() {
        doThrow(StsException.class).when(stsClient).getCallerIdentity();
        Boolean isValid = new StubAwsCloudCredentialsValidator(stsClient).validate("valid-foo", "invalid-bar");
        Assertions.assertFalse(isValid);

    }

    static class StubAwsCloudCredentialsValidator extends DefaultAwsCloudCredentialsValidator {
        private final StsClient stsClient;

        public StubAwsCloudCredentialsValidator(StsClient stsClient) {
            super();
            this.stsClient = stsClient;
        }

        @Override
        protected StsClient buildStsClient(AwsBasicCredentials awsBasicCredentials) {
            return stsClient;
        }
    }
}

