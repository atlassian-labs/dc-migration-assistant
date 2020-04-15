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

import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.DONE;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.DOWNLOADING;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.FAILED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.NOT_STARTED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.UPLOADING;

public class DefaultFileSystemMigrationReport implements FileSystemMigrationReport {

    private Clock clock;

    private final FileSystemMigrationErrorReport errorReport;
    private final FileSystemMigrationProgress progress;

    private Instant startTime;
    private Instant completeTime;
    private FilesystemMigrationStatus currentStatus;

    public DefaultFileSystemMigrationReport() {
        this(new DefaultFileSystemMigrationErrorReport(), new DefaultFilesystemMigrationProgress());
    }

    public DefaultFileSystemMigrationReport(FileSystemMigrationErrorReport errorReport, FileSystemMigrationProgress progress) {
        this.errorReport = errorReport;
        this.progress = progress;
        this.currentStatus = NOT_STARTED;
        this.clock = Clock.systemUTC();
    }

    @Override
    public void setStatus(FilesystemMigrationStatus status) {
        if (isStartingMigration(status)) {
            startTime = Instant.now(clock);
        } else if (isEndingMigration(status)) {
            completeTime = Instant.now(clock);
        }

        this.currentStatus = status;
    }

    @Override
    public FilesystemMigrationStatus getStatus() {
        return currentStatus;
    }

    @Override
    public Duration getElapsedTime() {
        Instant end = completeTime;
        if (isRunning()) {
            end = Instant.now(clock);
        }
        return Duration.between(startTime, end);
    }

    private boolean isRunning() {
        return currentStatus == UPLOADING || currentStatus == DOWNLOADING;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    private boolean isStartingMigration(FilesystemMigrationStatus toStatus) {
        return !isRunning() && toStatus == UPLOADING;
    }

    private boolean isEndingMigration(FilesystemMigrationStatus toStatus) {
        return isRunning() && isTerminalState(toStatus);
    }

    private boolean isTerminalState(FilesystemMigrationStatus toStatus) {
        return toStatus == DONE || toStatus == FAILED;
    }

    @Override
    public String toString() {
        return String.format("Filesystem migration report = { status: %s, migratedFiles: %d, erroredFiles: %d }",
                currentStatus,
                progress.getCountOfUploadedFiles(),
                errorReport.getFailedFiles().size()
        );
    }

    /*
    DELEGATED METHODS FOLLOW
     */

    @Override
    public Set<FailedFileMigration> getFailedFiles() {
        return errorReport.getFailedFiles();
    }

    @Override
    public void reportFileNotMigrated(FailedFileMigration failedFileMigration) {
        errorReport.reportFileNotMigrated(failedFileMigration);
    }

    @Override
    public Long getNumberOfFilesFound() {
        return progress.getNumberOfFilesFound();
    }

    @Override
    public void reportFileFound() {
        progress.reportFileFound();
    }

    @Override
    public boolean isCrawlingFinished() {
        return progress.isCrawlingFinished();
    }

    @Override
    public void reportCrawlingFinished() {
        progress.reportCrawlingFinished();
    }

    @Override
    public Long getNumberOfCommencedFileUploads() {
        return progress.getNumberOfCommencedFileUploads();
    }

    @Override
    public void reportFileUploadCommenced() {
        progress.reportFileUploadCommenced();
    }

    @Override
    public Long getCountOfUploadedFiles() {
        return progress.getCountOfUploadedFiles();
    }

    @Override
    public void reportFileUploaded() {
        progress.reportFileUploaded();
    }

    @Override
    public Long getCountOfDownloadFiles() {
        return progress.getCountOfDownloadFiles();
    }

    @Override
    public void setNumberOfFilesDownloaded(long downloadedFiles) {
        progress.setNumberOfFilesDownloaded(downloadedFiles);
    }
}

