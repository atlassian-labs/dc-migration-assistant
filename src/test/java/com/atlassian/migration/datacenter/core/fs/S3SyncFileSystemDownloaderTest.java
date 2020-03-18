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

package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.core.aws.SSMApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ssm.model.CommandInvocationStatus;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3SyncFileSystemDownloaderTest {

    @Mock
    SSMApi mockSsmApi;

    S3SyncFileSystemDownloader sut;

    @BeforeEach
    void setUp() {
        sut = new S3SyncFileSystemDownloader(mockSsmApi, 1);
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
                GetCommandInvocationResponse.builder()
                        .status(CommandInvocationStatus.DELAYED)
                        .build());

        assertThrows(S3SyncFileSystemDownloader.CannotLaunchCommandException.class, () -> sut.initiateFileSystemDownload());
    }
}