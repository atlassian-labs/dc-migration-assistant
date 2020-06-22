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
import com.atlassian.migration.datacenter.core.aws.infrastructure.migrationStack.MigrationStackInputGatheringStrategyFactory;
import com.atlassian.migration.datacenter.core.aws.infrastructure.migrationStack.QuickstartStandaloneMigrationStackInputGatheringStrategy;
import com.atlassian.migration.datacenter.core.aws.infrastructure.migrationStack.QuickstartWithVPCMigrationStackInputGatheringStrategy;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.model.Output;
import software.amazon.awssdk.services.cloudformation.model.Stack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static com.atlassian.migration.datacenter.core.aws.infrastructure.QuickstartDeploymentService.SERVICE_URL_STACK_OUTPUT_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
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

    static final String TEST_SERVICE_URL = "https://my.loadbalancer";
    static final List<Output> MOCK_OUTPUTS = new LinkedList<Output>() {{
        add(Output.builder().outputKey(SERVICE_URL_STACK_OUTPUT_KEY).outputValue(TEST_SERVICE_URL).build());
    }};
    public static final String CRITICAL_DEPLOYMENT_FAILURE = "Critical deployment failure";

    @Mock
    CfnApi mockCfnApi;

    @Mock
    MigrationService mockMigrationService;

    @Mock
    TargetDbCredentialsStorageService dbCredentialsStorageService;

    @Mock
    AWSMigrationHelperDeploymentService migrationHelperDeploymentService;

    @Mock
    QuickstartStandaloneMigrationStackInputGatheringStrategy standaloneMigrationStackInputGatheringStrategy;

    @Mock
    QuickstartWithVPCMigrationStackInputGatheringStrategy withVPCMigrationStackInputGatheringStrategy;

    @InjectMocks
    MigrationStackInputGatheringStrategyFactory strategyFactory;

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

        lenient().when(mockCfnApi.getStack(STACK_NAME)).thenReturn(Optional.of(Stack.builder().stackName(STACK_NAME).outputs(MOCK_OUTPUTS).build()));


        deploymentService = new QuickstartDeploymentService(
                mockCfnApi,
                mockMigrationService,
                dbCredentialsStorageService,
                migrationHelperDeploymentService,
                strategyFactory
        );
    }

    @Test
    void shouldDeployQuickStart() throws InvalidMigrationStageError {
        deploySimpleStack();

        verify(mockCfnApi).provisionStack(
                "https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc.template.yaml",
                STACK_NAME, STACK_PARAMS);
    }

    @Test
    void shouldDeployQuickStartWithVpc() throws InvalidMigrationStageError {
        deployWithVpcStack();

        verify(mockCfnApi).provisionStack(
                "https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml",
                STACK_NAME, STACK_PARAMS);
    }

    @Test
    void shouldStoreDBCredentials() throws InvalidMigrationStageError {
        deploymentService.deployApplication(STACK_NAME, STACK_PARAMS);

        verify(dbCredentialsStorageService).storeCredentials(TEST_DB_PASSWORD);
        verify(mockContext, atLeastOnce()).save();
    }

    @Test
    void shouldReturnInProgressWhileDeploying() throws InvalidMigrationStageError {
        when(mockContext.getApplicationDeploymentId()).thenReturn(STACK_NAME);
        givenStackDeploymentWillBeInProgress();

        deploySimpleStack();

        InfrastructureDeploymentState state = deploymentService.getDeploymentStatus();
        assertEquals(InfrastructureDeploymentState.CREATE_IN_PROGRESS, state);
    }

    @Test
    void shouldTransitionToWaitingForDeploymentWhileDeploymentIsCompleting() throws InvalidMigrationStageError, InterruptedException {
        givenStackDeploymentWillBeInProgress();

        deploySimpleStack();

        Thread.sleep(100);

        verify(mockMigrationService).transition(MigrationStage.PROVISION_APPLICATION_WAIT);
    }

    @Test
    void shouldTransitionMigrationServiceStateWhenDeploymentFinishes() throws InterruptedException, InvalidMigrationStageError {
        givenStackDeploymentWillComplete();
        deploySimpleStack();

        Thread.sleep(100);

        verify(mockMigrationService).transition(MigrationStage.PROVISION_MIGRATION_STACK);
    }

    @Test
    void shouldTransitionMigrationServiceToErrorWhenDeploymentFails() throws InterruptedException, InvalidMigrationStageError {
        givenStackDeploymentWillFail();

        deploySimpleStack();

        Thread.sleep(100);

        verify(mockMigrationService).error(CRITICAL_DEPLOYMENT_FAILURE);
    }

    @Test
    void shouldDeployMigrationStackWithApplicationStackOutputsAndResources() throws InvalidMigrationStageError, InterruptedException {
        givenStackDeploymentWillComplete();
        when(mockContext.getApplicationDeploymentId()).thenReturn(STACK_NAME);
        when(mockContext.getDeploymentMode()).thenReturn(ProvisioningConfig.DeploymentMode.STANDALONE);

        final String testS = "test-sg";
        final String testDbEndpoint = "my-db.com";
        final String testSubnet1 = "subnet-123";
        final String testVpc = "vpc-01234";
        final String testEfs = "fs-1234";

        Map<String, String> expectedMigrationStackParams = new HashMap<String, String>() {{
            put("NetworkPrivateSubnet", testSubnet1);
            put("EFSFileSystemId", testEfs);
            put("EFSSecurityGroup", testS);
            put("RDSSecurityGroup", testS);
            put("RDSEndpoint", testDbEndpoint);
            put("HelperInstanceType", "c5.large");
            put("HelperVpcId", testVpc);
        }};

        when(standaloneMigrationStackInputGatheringStrategy.gatherMigrationStackInputsFromApplicationStack(any())).thenReturn(expectedMigrationStackParams);

        deploySimpleStack();

        Thread.sleep(1200);

        verify(migrationHelperDeploymentService).deployMigrationInfrastructure(expectedMigrationStackParams);
    }

    @Test
    void shouldStoreServiceUrlInMigrationContext() throws InvalidMigrationStageError, InterruptedException {
        givenStackDeploymentWillComplete();
        when(mockContext.getApplicationDeploymentId()).thenReturn(STACK_NAME);
        final String testServiceUrl = "https://my.loadbalancer";

        deploySimpleStack();

        Thread.sleep(100);

        verify(mockContext).setServiceUrl(testServiceUrl);
        //Caters to the last save call in deployApplication com/atlassian/migration/datacenter/core/aws/infrastructure/QuickstartDeploymentService.java:106.
        // That call must be removed and each setter should save automatically, or the entire block needs to be run in a transaction
        verify(mockContext, times(3)).save();
    }

    @ParameterizedTest
    @EnumSource(value = ProvisioningConfig.DeploymentMode.class, names = {"WITH_NETWORK", "STANDALONE"})
    void shouldStoreDeploymentModeInContext(ProvisioningConfig.DeploymentMode mode) throws InvalidMigrationStageError {
        if (mode == ProvisioningConfig.DeploymentMode.WITH_NETWORK) {
            deploymentService.deployApplicationWithNetwork(STACK_NAME, STACK_PARAMS);
        } else if (mode == ProvisioningConfig.DeploymentMode.STANDALONE) {
            deploymentService.deployApplication(STACK_NAME, STACK_PARAMS);
        }

        verify(mockContext).setDeploymentMode(mode);
    }

    private void givenStackDeploymentWillBeInProgress() {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(InfrastructureDeploymentState.CREATE_IN_PROGRESS);
    }

    private void givenStackDeploymentWillComplete() {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(InfrastructureDeploymentState.CREATE_COMPLETE);
    }

    private void givenStackDeploymentWillFail() {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(InfrastructureDeploymentState.CREATE_FAILED);
        when(mockCfnApi.getStackErrorRootCause(STACK_NAME)).thenReturn(Optional.of(CRITICAL_DEPLOYMENT_FAILURE));
    }

    private void deploySimpleStack() throws InvalidMigrationStageError {
        deploymentService.deployApplication(STACK_NAME, STACK_PARAMS);
    }

    private void deployWithVpcStack() throws InvalidMigrationStageError {
        deploymentService.deployApplicationWithNetwork(STACK_NAME, STACK_PARAMS);
    }
}