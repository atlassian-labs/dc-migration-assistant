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
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.ssm.model.CommandInvocationStatus;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
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
        sut = new SsmPsqlDatabaseRestoreService(ssmApi, 1, migrationHelperDeploymentService, callback);
    }

    @Test
    void shouldBeSuccessfulWhenCommandStatusIsSuccessful() throws InvalidMigrationStageError, S3SyncFileSystemDownloader.CannotLaunchCommandException, InfrastructureDeploymentError {
        givenCommandCompletesWithStatus(CommandInvocationStatus.SUCCESS);

        sut.restoreDatabase();
    }

    @Test
    void shouldThrowWhenCommandStatusIsFailed() throws S3SyncFileSystemDownloader.CannotLaunchCommandException, InfrastructureDeploymentError {
        givenCommandCompletesWithStatus(CommandInvocationStatus.FAILED);

        assertThrows(DatabaseMigrationFailure.class, () -> sut.restoreDatabase());
    }

    @Test
    void shouldReturnOutputAndErrorUrls() throws SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException, InfrastructureDeploymentError {
        final String mockCommandId = "fake-command";
        final String mockInstance = "i-0353cc9a8ad7dafc2";
        final String errorMessage = "error-message";
        final String s3bucket = "s3bucket";
        final String s3prefix = "s3prefix";

        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        when(spy.getCommandId()).thenReturn(mockCommandId);

        when(migrationHelperDeploymentService.getMigrationHostInstanceId()).thenReturn(mockInstance);
        when(migrationHelperDeploymentService.getMigrationS3BucketName()).thenReturn(s3bucket);
        when(ssmApi.getSsmS3KeyPrefix()).thenReturn(s3prefix);
        when(ssmApi.getSSMCommand(mockCommandId, mockInstance)).thenReturn(
                GetCommandInvocationResponse.builder()
                        .instanceId(mockInstance)
                        .commandId(mockCommandId)
                        .standardErrorContent(errorMessage)
                        .build()
        );

        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();

        assertEquals("error-message", commandOutputs.errorMessage);
        assertEquals(String.format("https://console.aws.amazon.com/s3/buckets/%s/%s/%s/%s/awsrunShellScript/%s/",
                s3bucket, s3prefix, mockCommandId, mockInstance, spy.getRestoreDocumentName()), commandOutputs.consoleUrl);
    }

    private void givenCommandCompletesWithStatus(CommandInvocationStatus status) throws InfrastructureDeploymentError, S3SyncFileSystemDownloader.CannotLaunchCommandException {
        final String mockCommandId = "fake-command";
        final String mockInstance = "i-0353cc9a8ad7dafc2";
        final String mocument = "ssm-document";
        final String outputUrl = "output-url";
        final String errorUrl = "error-url";

        when(migrationHelperDeploymentService.getDbRestoreDocument()).thenReturn(mocument);
        when(migrationHelperDeploymentService.getMigrationHostInstanceId()).thenReturn(mockInstance);

        when(ssmApi.runSSMDocument(mocument, mockInstance, Collections.emptyMap())).thenReturn(mockCommandId);

        when(ssmApi.getSSMCommand(mockCommandId, mockInstance)).thenReturn(
                (GetCommandInvocationResponse) GetCommandInvocationResponse.builder()
                        .status(status)
                        .standardOutputUrl(outputUrl)
                        .standardErrorUrl(errorUrl)
                        .sdkHttpResponse(SdkHttpResponse.builder().statusText("it failed").build())
                        .build()
        );
    }
}