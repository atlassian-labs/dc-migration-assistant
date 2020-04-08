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

package com.atlassian.migration.datacenter.core.fs.download.s3sync;

import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader.IndeterminateS3SyncStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.ssm.model.CommandInvocationStatus;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3SyncFileSystemDownloaderTest {

    private static final String SYNC_STATUS_SUCCESS_COMPLETE_JSON = "{\"finished\": true, \"code\": \"0\", \"status\": {}}\n";
    private static final String SYNC_STATUS_DETERMINED_PARTIAL_JSON = "{\"status\": {\"progress\": 49492787.2, \"files_remaining\": 528, \"total\": 451411968.0, \"isCalculating\": false}}\n";
    private static final String SYNC_STATUS_COMPLETE_ERROR_JSON = "{\"finished\": true, \"code\": \"1\", \"status\": {}, \"errors\": [\"fatal error: Unable to locate credentials\\n\"]}\n";
    private static final String SYNC_STATUS_PARTIAL_CALCULATING_WITH_ERROR_JSON = "{\"status\": {\"progress\": 4724464025.6, \"files_remaining\": 1004, \"total\": 4724464025.6, \"isCalculating\": true}, \"errors\": [\"Oh dang it broke\\n\"]}\n";

    @Mock
    SSMApi mockSsmApi;

    @Mock
    AWSMigrationHelperDeploymentService migrationHelperDeploymentService;

    S3SyncFileSystemDownloader sut;

    @BeforeEach
    void setUp() {
        lenient().when(migrationHelperDeploymentService.getFsRestoreDocument()).thenReturn("fs-restore-doc");
        lenient().when(migrationHelperDeploymentService.getFsRestoreStatusDocument()).thenReturn("fs-restore-status-do");
        lenient().when(migrationHelperDeploymentService.getMigrationHostInstanceId()).thenReturn("i-0123456789");

        sut = new S3SyncFileSystemDownloader(mockSsmApi, migrationHelperDeploymentService, 1);
    }

    @Test
    void shouldIssueCommandToInstance() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        when(mockSsmApi.getSSMCommand(any(), anyString())).thenReturn(
                GetCommandInvocationResponse.builder()
                        .status(CommandInvocationStatus.SUCCESS)
                        .build());

        sut.initiateFileSystemDownload();

        verify(mockSsmApi).runSSMDocument(anyString(), anyString(), anyMap());
    }

    @Test
    void shouldNotThrowWhenCommandIsIssuedAndSucceeds() {
        when(mockSsmApi.getSSMCommand(any(), anyString())).thenReturn(
                GetCommandInvocationResponse.builder()
                        .status(CommandInvocationStatus.SUCCESS)
                        .build());

        try {
            sut.initiateFileSystemDownload();
        } catch (S3SyncFileSystemDownloader.CannotLaunchCommandException e) {
            fail();
        }
    }

    @Test
    void shouldThrowWhenCommandDoesNotSucceedWithinTimeout() {
        when(mockSsmApi.getSSMCommand(any(), anyString())).thenReturn(
                (GetCommandInvocationResponse) GetCommandInvocationResponse.builder()
                        .status(CommandInvocationStatus.DELAYED)
                        .sdkHttpResponse(SdkHttpResponse.builder().statusText("whoopsie dooopsie").build())
                        .build());

        assertThrows(S3SyncFileSystemDownloader.CannotLaunchCommandException.class, () -> sut.initiateFileSystemDownload());
    }

    @Test
    void shouldGetStatusOfSsmCommand() throws IndeterminateS3SyncStatusException {
        givenSyncCommandIsRunning();

        givenStatusCommandCompletesSuccessfullyWithOutput(SYNC_STATUS_SUCCESS_COMPLETE_JSON);

        S3SyncCommandStatus status = whenStatusCommandIsInvoked();

        assertTrue(status.isComplete());
        assertEquals(status.getExitCode(), 0);
        assertFalse(status.hasErrors());
    }

    @Test
    void shouldGetStatusWhenSyncIsPartiallyCompleteButNoLongerCalculating() throws IndeterminateS3SyncStatusException {
        givenSyncCommandIsRunning();

        givenStatusCommandCompletesSuccessfullyWithOutput(SYNC_STATUS_DETERMINED_PARTIAL_JSON);

        S3SyncCommandStatus status = whenStatusCommandIsInvoked();

        assertFalse(status.isCalculating());
        assertFalse(status.hasErrors());
        assertEquals(49492787.2, status.getBytesDownloaded());
        assertEquals(451411968.0, status.getTotalBytesToDownload());
        assertEquals(528, status.getFilesRemainingToDownload());
    }

    @Test
    void shouldGetStatusWhenSyncIsCompleteWithErrors() throws IndeterminateS3SyncStatusException {
        givenSyncCommandIsRunning();

        givenStatusCommandCompletesSuccessfullyWithOutput(SYNC_STATUS_COMPLETE_ERROR_JSON);

        S3SyncCommandStatus status = whenStatusCommandIsInvoked();

        assertTrue(status.hasErrors());
        assertTrue(status.isComplete());
        assertFalse(status.isCalculating());
        assertEquals(1, status.getExitCode());
        assertEquals(1, status.getErrors().size());
        assertThat(status.getErrors(), hasItem("fatal error: Unable to locate credentials\n"));
    }

    @Test
    void shouldGetStatusWhenSyncIsPartiallyCompleteWithErrors() throws IndeterminateS3SyncStatusException {
        givenSyncCommandIsRunning();

        givenStatusCommandCompletesSuccessfullyWithOutput(SYNC_STATUS_PARTIAL_CALCULATING_WITH_ERROR_JSON);

        final S3SyncCommandStatus status = whenStatusCommandIsInvoked();

        assertTrue(status.hasErrors());
        assertTrue(status.isCalculating());
        assertFalse(status.isComplete());
        assertEquals(4724464025.6, status.getBytesDownloaded());
        assertEquals(4724464025.6, status.getTotalBytesToDownload());
        assertEquals(1004, status.getFilesRemainingToDownload());
        assertThat(status.getErrors(), hasItem("Oh dang it broke\n"));
    }

    private void givenSyncCommandIsRunning() {
        when(mockSsmApi.runSSMDocument(anyString(), anyString(), anyMap())).thenReturn("status-command-invocation");
    }

    private void givenStatusCommandCompletesSuccessfullyWithOutput(String syncStatusDeterminedPartial) {
        GetCommandInvocationResponse mockStatusResponse = GetCommandInvocationResponse.builder()
                .status(CommandInvocationStatus.SUCCESS)
                .standardOutputContent(syncStatusDeterminedPartial)
                .build();
        when(mockSsmApi.getSSMCommand(anyString(), anyString())).thenReturn(mockStatusResponse);
    }

    private S3SyncCommandStatus whenStatusCommandIsInvoked() throws IndeterminateS3SyncStatusException {
        return sut.getFileSystemDownloadStatus();
    }
}