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
import com.atlassian.migration.datacenter.core.aws.infrastructure.RemoteInstanceCommandRunnerService;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SsmPsqlDatabaseRestoreServiceTest {

    private final String mockCommandId = "fake-command";
    private final String mockInstance = "i-0353cc9a8ad7dafc2";
    private final String s3bucket = "s3bucket";
    private final String s3prefix = "s3prefix";

    @Mock
    SSMApi ssmApi;

    @Mock
    DatabaseRestoreStageTransitionCallback callback;

    @Mock
    AWSMigrationHelperDeploymentService migrationHelperDeploymentService;
    
    @Mock
    RemoteInstanceCommandRunnerService remoteInstanceCommandRunnerService;        

    SsmPsqlDatabaseRestoreService sut;

    @BeforeEach
    void setUp() {
        sut = new SsmPsqlDatabaseRestoreService(ssmApi, migrationHelperDeploymentService, callback, remoteInstanceCommandRunnerService);
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
        final String errorMessage = "error-message";
        
        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        setupExpectations(mockCommandId, mockInstance, errorMessage, s3bucket, s3prefix, spy);

        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();
        assertEquals("error-message", commandOutputs.errorMessage);
        assertFalse(commandOutputs.criticalError);
        assertEquals(String.format("https://console.aws.amazon.com/s3/buckets/%s/%s/%s/%s/awsrunShellScript/%s/",
                s3bucket, s3prefix, mockCommandId, mockInstance, spy.getRestoreDocumentName()), commandOutputs.consoleUrl);
    }

    @Test
    void shouldReturnOutputIndicatingTheErrorIsCriticalFor_CRITICAL_ERROR_001() throws SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException, InfrastructureDeploymentError {
        final String errorMessage = "could not connect to server: No route to host. Is the server running on host (10.0.7.109) and accepting TCP/IP connections on port 5432?";

        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        setupExpectations(mockCommandId, mockInstance, errorMessage, s3bucket, s3prefix, spy);
        
        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();
        assertTrue(commandOutputs.criticalError);
    }

    @Test
    void shouldReturnOutputIndicatingTheErrorIsCriticalFor_CRITICAL_ERROR_002() throws SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException, InfrastructureDeploymentError {
        final String errorMessage = "could not translate host name";

        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        setupExpectations(mockCommandId, mockInstance, errorMessage, s3bucket, s3prefix, spy);

        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();
        assertTrue(commandOutputs.criticalError);
    }

    @Test
    void shouldReturnOutputIndicatingTheErrorIsCriticalFor_CRITICAL_ERROR_003() throws SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException, InfrastructureDeploymentError {
        final String errorMessage = "Connection timed out";

        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        setupExpectations(mockCommandId, mockInstance, errorMessage, s3bucket, s3prefix, spy);

        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();
        assertTrue(commandOutputs.criticalError);
    }

    @Test
    void shouldReturnOutputIndicatingTheErrorIsCriticalFor_CRITICAL_ERROR_004() throws SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException, InfrastructureDeploymentError {
        final String errorMessage = "No route to host";

        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        setupExpectations(mockCommandId, mockInstance, errorMessage, s3bucket, s3prefix, spy);

        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();
        assertTrue(commandOutputs.criticalError);
    }

    @Test
    void shouldReturnOutputIndicatingTheErrorIsCriticalFor_CRITICAL_ERROR_005() throws SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException, InfrastructureDeploymentError {
        final String errorMessage = "Name or service not known";

        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        setupExpectations(mockCommandId, mockInstance, errorMessage, s3bucket, s3prefix, spy);

        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();
        assertTrue(commandOutputs.criticalError);
    }

    @Test
    void shouldReturnOutputIndicatingTheErrorIsNonCriticalWhenErrorContentBlank() throws SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException, InfrastructureDeploymentError {
        final String errorMessage = "";

        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        setupExpectations(mockCommandId, mockInstance, errorMessage, s3bucket, s3prefix, spy);
        
        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();
        assertFalse(commandOutputs.criticalError);
    }

    @Test
    void shouldReturnOutputIndicatingTheErrorIsNonCriticalWhenErrorContentNull() throws SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException, InfrastructureDeploymentError {
        final String errorMessage = null;

        final SsmPsqlDatabaseRestoreService spy = spy(sut);
        setupExpectations(mockCommandId, mockInstance, errorMessage, s3bucket, s3prefix, spy);

        final SsmPsqlDatabaseRestoreService.SsmCommandResult commandOutputs = spy.fetchCommandResult();
        assertFalse(commandOutputs.criticalError);
    }

    private void setupExpectations(String mockCommandId, String mockInstance, String errorMessage, String s3bucket, String s3prefix, SsmPsqlDatabaseRestoreService spy) throws InfrastructureDeploymentError {
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
    }

    private void givenCommandCompletesWithStatus(CommandInvocationStatus status) throws InfrastructureDeploymentError, S3SyncFileSystemDownloader.CannotLaunchCommandException {
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