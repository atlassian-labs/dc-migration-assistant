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
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingGroup;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsResponse;
import software.amazon.awssdk.services.autoscaling.model.Instance;
import software.amazon.awssdk.services.cloudformation.model.Output;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AWSMigrationHelperDeploymentServiceTest {

    static final String APPLICATION_DEPLOYMENT = "application-deployment";
    static final String DEPLOYMENT_ID = APPLICATION_DEPLOYMENT + "-migration";
    static final String FS_DOWNLOAD_DOC = "fs-download-doc";
    static final String FS_DOWNLOAD_STATUS_DOC = "fs-download-status-doc";
    static final String DB_RESTORE_DOC = "db-restore-doc";
    static final String MIGRATION_ASG = "migration-asg";
    static final String MIGRATION_BUCKET = "migration-bucket";
    static final String MIGRATION_HOST_INSTANCE_ID = "i-12345";

    @Mock
    CfnApi mockCfn;

    @Mock
    MigrationService mockMigrationService;

    AWSMigrationHelperDeploymentService sut;

    @Mock
    AutoScalingClient mockAutoscaling;

    @Mock
    MigrationContext mockContext;

    private AtomicReference<String> deploymentId;

    @BeforeEach
    void setUp() {
        when(mockMigrationService.getCurrentContext()).thenReturn(mockContext);
        when(mockContext.getApplicationDeploymentId()).thenReturn(APPLICATION_DEPLOYMENT);

        deploymentId = new AtomicReference<>();
        doAnswer(invocation -> {
            deploymentId.set(invocation.getArgument(0));
            return null;
        }).when(mockContext).setHelperStackDeploymentId(anyString());

        lenient().when(mockContext.getHelperStackDeploymentId()).thenReturn(deploymentId.get());

        sut = new AWSMigrationHelperDeploymentService(mockCfn, () -> mockAutoscaling, mockMigrationService, 1);
    }

    @Test
    void shouldNameMigrationStackAfterApplicationStackWithSuffixAndStoreInContext() {
        givenMigrationStackHasStartedDeploying();

        assertEquals(DEPLOYMENT_ID, deploymentId.get());
    }

    @Test
    void shouldProvisionCloudformationStack() {
        givenMigrationStackHasStartedDeploying();

        verify(mockCfn).provisionStack("https://trebuchet-aws-resources.s3.amazonaws.com/migration-helper.yml", DEPLOYMENT_ID, Collections.emptyMap());
    }

    @Test
    void shouldReturnInProgressWhileCloudformationDeploymentIsOngoing() {
        givenMigrationStackDeploymentWillBeInProgress();
        givenMigrationStackHasStartedDeploying();
        givenMigrationStackNameHasBeenStoredInContext();

        assertEquals(InfrastructureDeploymentStatus.CREATE_IN_PROGRESS, sut.getDeploymentStatus());
    }

    @Test
    void shouldReturnCompleteWhenCloudformationDeploymentSucceeds() throws InterruptedException {
        givenMigrationStackDeploymentWillCompleteSuccessfully();
        givenMigrationStackHasStartedDeploying();
        givenMigrationStackNameHasBeenStoredInContext();

        Thread.sleep(100);

        assertEquals(InfrastructureDeploymentStatus.CREATE_COMPLETE, sut.getDeploymentStatus());
    }

    @Test
    void shouldReturnErrorWhenCloudformationDeploymentFails() throws InterruptedException {
        givenMigrationStackDeploymentWillFail();
        givenMigrationStackHasStartedDeploying();
        givenMigrationStackNameHasBeenStoredInContext();

        Thread.sleep(100);

        assertEquals(InfrastructureDeploymentStatus.CREATE_FAILED, sut.getDeploymentStatus());
    }

    @Test
    void shoudGatherResourcesRequiredForMigration() throws InterruptedException {
        givenMigrationStackDeploymentWillCompleteSuccessfully();
        givenMigrationStackHasStartedDeploying();

        Thread.sleep(1100);

        assertStackOutputsAreAvailable();
    }

    @Test
    void shouldThrowErrorWhenGettingOutputsIfStackDidNotDeploySuccessfully() throws InterruptedException {
        givenMigrationStackDeploymentWillFail();
        givenMigrationStackHasStartedDeploying();

        Thread.sleep(100);

        assertGettingStackOutputsThrowsError();
    }

    private void assertGettingStackOutputsThrowsError() {
        ArrayList<Executable> outputGetters = new ArrayList<>();
        outputGetters.add(sut::getMigrationS3BucketName);
        outputGetters.add(sut::getDbRestoreDocument);
        outputGetters.add(sut::getFsRestoreDocument);
        outputGetters.add(sut::getFsRestoreStatusDocument);
        outputGetters.add(sut::getMigrationHostInstanceId);

        for (Executable outputGetter : outputGetters) {
            assertThrows(InfrastructureDeploymentError.class, outputGetter);
        }
    }

    private void assertStackOutputsAreAvailable() {
        assertEquals(FS_DOWNLOAD_DOC, sut.getFsRestoreDocument());
        assertEquals(FS_DOWNLOAD_STATUS_DOC, sut.getFsRestoreStatusDocument());
        assertEquals(DB_RESTORE_DOC, sut.getDbRestoreDocument());
        assertEquals(MIGRATION_BUCKET, sut.getMigrationS3BucketName());
        assertEquals(MIGRATION_HOST_INSTANCE_ID, sut.getMigrationHostInstanceId());
    }

    private void givenMigrationStackHasStartedDeploying() {
        when(mockMigrationService.getCurrentContext()).thenReturn(mockContext);
        try {
            sut.deployMigrationInfrastructure(Collections.emptyMap());
        } catch (InvalidMigrationStageError invalidMigrationStageError) {
            fail("invalid migration stage error thrown while deploying migration helper", invalidMigrationStageError);
        }
    }

    private void givenMigrationStackNameHasBeenStoredInContext() {
        when(mockContext.getHelperStackDeploymentId()).thenReturn(DEPLOYMENT_ID);
    }

    private void givenMigrationStackDeploymentWillBeInProgress() {
        givenContextContainsMigrationHelperStackId();
        when(mockCfn.getStatus(DEPLOYMENT_ID)).thenReturn(StackStatus.CREATE_IN_PROGRESS);
    }

    private void givenMigrationStackDeploymentWillCompleteSuccessfully() {
        givenContextContainsMigrationHelperStackId();
        when(mockCfn.getStatus(DEPLOYMENT_ID)).thenReturn(StackStatus.CREATE_COMPLETE);

        Stack completedStack = Stack.builder()
                .outputs(Output.builder().outputKey("DownloadSSMDocument").outputValue(FS_DOWNLOAD_DOC).build(),
                        Output.builder().outputKey("DownloadStatusSSMDocument").outputValue(FS_DOWNLOAD_STATUS_DOC).build(),
                        Output.builder().outputKey("RdsRestoreSSMDocument").outputValue(DB_RESTORE_DOC).build(),
                        Output.builder().outputKey("ServerGroup").outputValue(MIGRATION_ASG).build(),
                        Output.builder().outputKey("MigrationBucket").outputValue(MIGRATION_BUCKET).build()
                        ).build();

        when(mockCfn.getStack(DEPLOYMENT_ID)).thenReturn(Optional.of(completedStack));
        lenient().when(mockAutoscaling.describeAutoScalingGroups(
                DescribeAutoScalingGroupsRequest.builder()
                        .autoScalingGroupNames(MIGRATION_ASG)
                        .build()
        )).thenReturn(
                DescribeAutoScalingGroupsResponse.builder()
                        .autoScalingGroups(
                                AutoScalingGroup.builder()
                                        .instances(
                                                Instance.builder()
                                                        .instanceId(MIGRATION_HOST_INSTANCE_ID).build()
                                        ).build()
                        ).build()
        );
    }

    private void givenMigrationStackDeploymentWillFail() {
        givenContextContainsMigrationHelperStackId();
        when(mockCfn.getStatus(DEPLOYMENT_ID)).thenReturn(StackStatus.CREATE_FAILED);
    }

    private void givenContextContainsMigrationHelperStackId() {
        lenient().when(mockContext.getHelperStackDeploymentId()).thenReturn(DEPLOYMENT_ID);
    }
}