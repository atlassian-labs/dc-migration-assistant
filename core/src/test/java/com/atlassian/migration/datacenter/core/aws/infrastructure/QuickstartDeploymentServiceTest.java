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
import com.atlassian.migration.datacenter.core.aws.db.restore.TargetDbCredentialsStorageService;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuickstartDeploymentServiceTest {

    static final String STACK_NAME = "my-stack";
    static final String TEST_DB_PASSWORD = "myDatabasePassword";
    static final HashMap<String, String> STACK_PARAMS = new HashMap<String, String>() {{
        put("parameter", "value");
        put("DBPassword", TEST_DB_PASSWORD);
    }};

    @Mock
    CfnApi mockCfnApi;

    @Mock
    MigrationService mockMigrationService;

    @Mock
    TargetDbCredentialsStorageService dbCredentialsStorageService;

    @InjectMocks
    QuickstartDeploymentService deploymentService;

    @Mock
    MigrationContext mockContext;

    @BeforeEach
    void setUp() {
        Properties properties = new Properties();
        final String passwordPropertyKey = "password";
        doAnswer(invocation -> {
            properties.setProperty(passwordPropertyKey, invocation.getArgument(0));
            return null;
        }).when(dbCredentialsStorageService).storeCredentials(anyString());
        when(mockMigrationService.getCurrentContext()).thenReturn(mockContext);
    }

    @Test
    void shouldDeployQuickStart() throws InvalidMigrationStageError
    {
        deploySimpleStack();

        verify(mockCfnApi).provisionStack("https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml", STACK_NAME, STACK_PARAMS);
    }

    @Test
    void shouldStoreDBCredentials() throws InvalidMigrationStageError {
        deploymentService.deployApplication(STACK_NAME, STACK_PARAMS);

        verify(dbCredentialsStorageService).storeCredentials(TEST_DB_PASSWORD);
    }

    @Test
    void shouldReturnInProgressWhileDeploying() throws InvalidMigrationStageError {
        when(mockContext.getApplicationDeploymentId()).thenReturn(STACK_NAME);
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_IN_PROGRESS);
        when(mockContext.getApplicationDeploymentId()).thenReturn(STACK_NAME);

        deploySimpleStack();

        assertEquals(InfrastructureDeploymentStatus.CREATE_IN_PROGRESS, deploymentService.getDeploymentStatus());
    }

    @Test
    void shouldTransitionToWaitingForDeploymentWhileDeploymentIsCompleting() throws InvalidMigrationStageError, InterruptedException {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_IN_PROGRESS);

        deploySimpleStack();

        Thread.sleep(100);

        verify(mockMigrationService).transition(MigrationStage.PROVISION_APPLICATION_WAIT);
    }

    @Test
    void shouldTransitionMigrationServiceStateWhenDeploymentFinishes() throws InterruptedException, InvalidMigrationStageError {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_COMPLETE);

        deploySimpleStack();

        Thread.sleep(100);

        verify(mockMigrationService).transition(MigrationStage.PROVISION_MIGRATION_STACK);
    }

    @Test
    void shouldTransitionMigrationServiceToErrorWhenDeploymentFails() throws InterruptedException, InvalidMigrationStageError {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_FAILED);

        deploySimpleStack();

        Thread.sleep(100);

        verify(mockMigrationService).error();
    }

    private void deploySimpleStack() throws InvalidMigrationStageError {
        deploymentService.deployApplication(STACK_NAME, STACK_PARAMS);
    }
}