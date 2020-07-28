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

package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudformationDeploymentServiceTest {

    final static String TEMPLATE_URL = "https://fake-url.com";
    final static String STACK_NAME = "test-stack";
    final static Map<String, String> STACK_PARAMS = Collections.emptyMap();

    @Mock
    CfnApi mockCfnApi;

    @Mock
    MigrationService migrationService;

    @Mock
    MigrationContext context;

    CloudformationDeploymentService sut;

    boolean deploymentFailed = false;
    boolean deploymentSucceeded = false;

    @BeforeEach
    void setup() {
        lenient().doNothing().when(context).save();
        when(migrationService.getCurrentContext()).thenReturn(context);
        sut = new CloudformationDeploymentService(mockCfnApi, migrationService) {
            @Override
            protected void handleFailedDeployment(String message) {
                deploymentFailed = true;
            }

            @Override
            protected void handleSuccessfulDeployment() {
                deploymentSucceeded = true;
            }
        };
    }

    @Test
    void shouldDeployQuickStart() throws InfrastructureDeploymentError {
        deploySimpleStack();

        verify(mockCfnApi).provisionStack(TEMPLATE_URL, STACK_NAME, STACK_PARAMS);
    }

    @Test
    void shouldReturnInProgressWhileDeploying() throws InfrastructureDeploymentError {
        givenDeploymentStateIs(InfrastructureDeploymentState.CREATE_IN_PROGRESS);
        deploySimpleStack();

        InfrastructureDeploymentState state = sut.getDeploymentStatus(STACK_NAME);
        assertEquals(InfrastructureDeploymentState.CREATE_IN_PROGRESS, state);
    }

    @Test
    void shouldCallHandleFailedDeploymentWhenDeploymentFails() throws InterruptedException, InfrastructureDeploymentError {
        final String badStatus = "it broke";
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(InfrastructureDeploymentState.CREATE_FAILED);

        deploySimpleStack();

        Thread.sleep(100);

        assertTrue(deploymentFailed);
        assertFalse(deploymentSucceeded);

        verify(context).setDeploymentState(InfrastructureDeploymentState.CREATE_FAILED);
    }

    @Test
    void shouldBeFailedWhenStatusIsDeleted() throws InterruptedException, InfrastructureDeploymentError {
        givenDeploymentStateIs(InfrastructureDeploymentState.CREATE_FAILED);

        InfrastructureDeploymentState state = sut.getDeploymentStatus(STACK_NAME);
        assertEquals(InfrastructureDeploymentState.CREATE_FAILED, state);
    }

    @Test
    void shouldCallHandleSuccessfulDeploymentWhenDeploymentSucceeds() throws InterruptedException, InfrastructureDeploymentError {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(InfrastructureDeploymentState.CREATE_COMPLETE);

        deploySimpleStack();

        Thread.sleep(100);

        assertTrue(deploymentSucceeded);
        assertFalse(deploymentFailed);
    }

    private void deploySimpleStack() throws InfrastructureDeploymentError {
        sut.deployCloudformationStack(TEMPLATE_URL, STACK_NAME, STACK_PARAMS);
    }

    private void givenDeploymentStateIs(InfrastructureDeploymentState state) {
        when(migrationService.getCurrentContext()).thenReturn(context);
        when(context.getDeploymentState()).thenReturn(state);
    }

}