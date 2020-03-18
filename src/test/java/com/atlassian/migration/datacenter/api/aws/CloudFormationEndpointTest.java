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

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudFormationEndpointTest {
    @Mock
    private ApplicationDeploymentService deploymentService;

    @InjectMocks
    private CloudFormationEndpoint endpoint;

    @Test
    public void shouldAcceptRequestToProvisionCloudFormationStack() throws Exception {
        String stackName = "stack-name";
        ProvisioningConfig provisioningConfig = new ProvisioningConfig("url", stackName, new HashMap<>());

        Response response = endpoint.provisionInfrastructure(provisioningConfig);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        assertEquals(provisioningConfig.getStackName(), response.getEntity());

        verify(deploymentService).deployApplication(stackName, new HashMap<>());
    }

    @Test
    public void shouldBeConflictWhenCurrentMigrationStageIsNotValid() throws Exception {
        ProvisioningConfig provisioningConfig = new ProvisioningConfig("url", "stack-name", new HashMap<>());

        String errorMessage = "migration status is FUBAR";
        doThrow(new InvalidMigrationStageError(errorMessage)).when(deploymentService).deployApplication(provisioningConfig.getStackName(), provisioningConfig.getParams());


        Response response = endpoint.provisionInfrastructure(provisioningConfig);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals(errorMessage, ((Map<String, String>) response.getEntity()).get("error"));
    }

    @Test
    public void shouldGetCurrentProvisioningStatusForGivenStackId() {
        ApplicationDeploymentService.ApplicationDeploymentStatus expectedStatus = ApplicationDeploymentService.ApplicationDeploymentStatus.CREATE_IN_PROGRESS;
        when(this.deploymentService.getDeploymentStatus()).thenReturn(expectedStatus);

        Response response = endpoint.getInfrastructureStatus();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedStatus, ((Map<String, String>) response.getEntity()).get("status"));
    }

    @Test
    public void shouldGetHandleErrorWhenStatusCannotBeRetrieved() {
        String expectedErrorMessage = "stack Id not found";
        doThrow(StackInstanceNotFoundException.builder().message(expectedErrorMessage).build()).when(this.deploymentService).getDeploymentStatus();

        Response response = endpoint.getInfrastructureStatus();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(expectedErrorMessage, ((Map<String, String>) response.getEntity()).get("error"));
    }
}