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

package com.atlassian.migration.datacenter.core.aws.cloud;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
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
        Boolean isValid = new StubAwsCloudCredentialsValidator(stsClient).validate("valid-foo", "valid-bar", "valid-region");
        Assertions.assertTrue(isValid);
    }

    @Test
    void shouldNotValidateWhenCredentialsAreInValid() {
        doThrow(StsException.class).when(stsClient).getCallerIdentity();
        Boolean isValid = new StubAwsCloudCredentialsValidator(stsClient).validate("valid-foo", "invalid-bar", "valid-region");
        Assertions.assertFalse(isValid);

    }

    static class StubAwsCloudCredentialsValidator extends DefaultAwsCloudCredentialsValidator {
        private final StsClient stsClient;

        public StubAwsCloudCredentialsValidator(StsClient stsClient) {
            super();
            this.stsClient = stsClient;
        }

        @Override
        protected StsClient buildStsClient(AwsBasicCredentials awsBasicCredentials, Region region) {
            return stsClient;
        }
    }
}

