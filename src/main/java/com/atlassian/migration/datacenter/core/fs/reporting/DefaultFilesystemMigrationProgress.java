package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;

import java.util.concurrent.atomic.AtomicLong;

public class DefaultFilesystemMigrationProgress implements FileSystemMigrationProgress {

    private AtomicLong numFilesMigrated = new AtomicLong(0);

    private AtomicLong filesFound = new AtomicLong(0);

    private AtomicLong fileUploadsCommenced = new AtomicLong(0);

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
    public Long getCountOfMigratedFiles() {
        return numFilesMigrated.get();
    }

    @Override
    public void reportFileMigrated() {
        numFilesMigrated.incrementAndGet();
    }
}
