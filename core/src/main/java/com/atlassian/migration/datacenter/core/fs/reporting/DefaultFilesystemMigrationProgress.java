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

package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;

import java.util.concurrent.atomic.AtomicLong;

public class DefaultFilesystemMigrationProgress implements FileSystemMigrationProgress {

    private AtomicLong numFilesUploaded = new AtomicLong(0);

    private AtomicLong filesFound = new AtomicLong(0);

    private AtomicLong fileUploadsCommenced = new AtomicLong(0);

    private AtomicLong fileDownloadsCompleted = new AtomicLong(0);

    @Override
    public Long getNumberOfFilesFound() {
        return filesFound.get();
    }

    @Override
    public void reportFileFound() {
        filesFound.incrementAndGet();
    }

    @Override
    public Long getNumberOfCommencedFileUploads() {
        return fileUploadsCommenced.get();
    }

    @Override
    public void reportFileUploadCommenced() {
        fileUploadsCommenced.incrementAndGet();
    }

    @Override
    public Long getCountOfUploadedFiles() {
        return numFilesUploaded.get();
    }

    @Override
    public void reportFileUploaded() {
        numFilesUploaded.incrementAndGet();
    }

    @Override
    public Long getCountOfDownloadFiles() {
        return fileDownloadsCompleted.get();
    }

    @Override
    public void setNumberOfFilesDownloaded(long downloadedFiles) {
        fileDownloadsCompleted.set(downloadedFiles);
    }
}
