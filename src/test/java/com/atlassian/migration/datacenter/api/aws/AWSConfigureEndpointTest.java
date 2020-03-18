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

package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.cloud.AWSConfigurationService;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AWSConfigureEndpointTest {

    @Mock
    AWSConfigurationService configurationService;

    @InjectMocks
    AWSConfigureEndpoint sut;

    @Test
    void shouldConfigureAWS() throws InvalidMigrationStageError {
        AWSConfigureEndpoint.AWSConfigureWebObject payload = new AWSConfigureEndpoint.AWSConfigureWebObject();
        final String accessKeyId = "accessKeyId";
        final String secretKey = "secretKey";
        final String region = "us-east-1";
        payload.setAccessKeyId(accessKeyId);
        payload.setSecretAccessKey(secretKey);
        payload.setRegion(region);

        Response response = sut.storeAWSCredentials(payload);

        verify(configurationService).configureCloudProvider(accessKeyId, secretKey, region);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void shouldReturnFailedWhenInvalidMigrationStage() throws InvalidMigrationStageError {
        AWSConfigureEndpoint.AWSConfigureWebObject payload = new AWSConfigureEndpoint.AWSConfigureWebObject();
        payload.setAccessKeyId("accessKeyId");
        payload.setSecretAccessKey("secretKey");
        payload.setRegion("us-east-1");

        doThrow(new InvalidMigrationStageError("")).when(configurationService).configureCloudProvider(anyString(), anyString(), anyString());

        Response response = sut.storeAWSCredentials(payload);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

}