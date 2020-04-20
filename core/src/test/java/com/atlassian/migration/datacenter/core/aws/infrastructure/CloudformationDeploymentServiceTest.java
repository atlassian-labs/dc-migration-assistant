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
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudformationDeploymentServiceTest {

    final static String TEMPLATE_URL = "https://fake-url.com";
    final static String STACK_NAME = "test-stack";
    final static Map<String, String> STACK_PARAMS = Collections.emptyMap();

    @Mock
    CfnApi mockCfnApi;

    CloudformationDeploymentService sut;

    boolean deploymentFailed = false;
    boolean deploymentSucceeded = false;

    @BeforeEach
    void setup() {
        sut = new CloudformationDeploymentService(mockCfnApi) {
            @Override
            protected void handleFailedDeployment() {
                deploymentFailed = true;
            }

            @Override
            protected void handleSuccessfulDeployment() {
                deploymentSucceeded = true;
            }
        };
    }

    @Test
    void shouldDeployQuickStart() {
        deploySimpleStack();

        verify(mockCfnApi).provisionStack(TEMPLATE_URL, STACK_NAME, STACK_PARAMS);
    }

    @Test
    void shouldReturnInProgressWhileDeploying() throws InvalidMigrationStageError
    {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_IN_PROGRESS);

        deploySimpleStack();

        assertEquals(InfrastructureDeploymentStatus.CREATE_IN_PROGRESS, sut.getDeploymentStatus(STACK_NAME));
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.INCLUDE, names = {"CREATE_FAILED", "ROLLBACK_COMPLETE", "ROLLBACK_FAILED"}, value = StackStatus.class)
    void shouldCallHandleFailedDeploymentWhenDeploymentFails(StackStatus failedStatus) throws InterruptedException {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(failedStatus);

        deploySimpleStack();

        Thread.sleep(100);

        assertTrue(deploymentFailed);
        assertFalse(deploymentSucceeded);
    }

    @Test
    void shouldCallHandleSuccessfulDeploymentWhenDeploymentFails() throws InterruptedException {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_COMPLETE);

        deploySimpleStack();

        Thread.sleep(100);

        assertTrue(deploymentSucceeded);
        assertFalse(deploymentFailed);
    }

    private void deploySimpleStack() {
        sut.deployCloudformationStack(TEMPLATE_URL, STACK_NAME, STACK_PARAMS);
    }

}