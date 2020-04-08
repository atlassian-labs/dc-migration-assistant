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

package com.atlassian.migration.datacenter.core.aws.db.restore;

import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.ssm.model.CommandInvocationStatus;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SsmPsqlDatabaseRestoreServiceTest {

    @Mock
    SSMApi ssmApi;

    @Mock
    DatabaseRestoreStageTransitionCallback callback;

    @Mock
    AWSMigrationHelperDeploymentService migrationHelperDeploymentService;

    SsmPsqlDatabaseRestoreService sut;

    @BeforeEach
    void setUp() {
        sut = new SsmPsqlDatabaseRestoreService(ssmApi, 1, migrationHelperDeploymentService);
    }

    @Test
    void shouldBeSuccessfulWhenCommandStatusIsSuccessful() throws InvalidMigrationStageError
    {
        givenCommandCompletesWithStatus(CommandInvocationStatus.SUCCESS);

        sut.restoreDatabase(callback);
    }

    @Test
    void shouldThrowWhenCommandStatusIsFailed() {
        givenCommandCompletesWithStatus(CommandInvocationStatus.FAILED);

        assertThrows(DatabaseMigrationFailure.class, () -> sut.restoreDatabase(callback));
    }

    private void givenCommandCompletesWithStatus(CommandInvocationStatus status) {
        final String mockCommandId = "fake-command";
        final String mockInstance = "i-0353cc9a8ad7dafc2";
        final String mocument = "ssm-document";
        when(migrationHelperDeploymentService.getDbRestoreDocument()).thenReturn(mocument);
        when(migrationHelperDeploymentService.getMigrationHostInstanceId()).thenReturn(mockInstance);

        when(ssmApi.runSSMDocument(mocument, mockInstance, Collections.emptyMap())).thenReturn(mockCommandId);

        when(ssmApi.getSSMCommand(mockCommandId, mockInstance)).thenReturn(
                (GetCommandInvocationResponse) GetCommandInvocationResponse.builder()
                        .status(status)
                        .sdkHttpResponse(SdkHttpResponse.builder().statusText("it failed").build())
                        .build()
        );
    }

}