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

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFilesystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3SyncFileSystemDownloadManagerTest {

    @Mock
    private S3SyncFileSystemDownloader mockDownloader;

    @Mock
    private S3SyncCommandStatus mockStatus;

    @InjectMocks
    private S3SyncFileSystemDownloadManager sut;

    @Test
    void shouldSetDownloadedFiles() throws S3SyncFileSystemDownloader.CannotLaunchCommandException, InterruptedException {
        when(mockDownloader.getFileSystemDownloadStatus()).thenReturn(mockStatus);
        when(mockStatus.getFilesRemainingToDownload()).thenReturn(100);

        FileSystemMigrationProgress progress = new DefaultFilesystemMigrationProgress();
        for (int i = 0; i < 110; i++) {
            progress.reportFileUploaded();
        }

        sut.downloadFileSystem(progress);

        Thread.sleep(1000);

        assertEquals(10, progress.getCountOfDownloadFiles());
    }

}