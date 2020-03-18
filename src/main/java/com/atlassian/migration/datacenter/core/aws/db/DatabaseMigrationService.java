package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.Crawler;
import com.atlassian.migration.datacenter.core.fs.DirectoryStreamCrawler;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig;
import com.atlassian.migration.datacenter.core.fs.S3Uploader;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright Atlassian: 10/03/2020
 */
public class DatabaseMigrationService
{
    private static final String TARGET_BUCKET_NAME = System.getProperty("S3_TARGET_BUCKET_NAME", "trebuchet-testing");
    //TODO: This will be moved into database archival service
    private final Path tempDirectory;
    private final S3AsyncClient s3AsyncClient;
    private final MigrationService migrationService;

    private AtomicReference<MigrationStage> status = new AtomicReference<>();
    private DatabaseArchivalService databaseArchivalService;

    public DatabaseMigrationService(Path tempDirectory, S3AsyncClient s3AsyncClient, MigrationService migrationService, DatabaseArchivalService databaseArchivalService)
    {
        this.tempDirectory = tempDirectory;
        this.s3AsyncClient = s3AsyncClient;
        this.migrationService = migrationService;
        this.databaseArchivalService = databaseArchivalService;
    }

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob. The status of the migration can be queried via getStatus().
     */
    public FileSystemMigrationErrorReport performMigration() throws DatabaseMigrationFailure, InvalidMigrationStageError {
        Path pathToDatabaseFile = databaseArchivalService.archiveDatabase(tempDirectory);
        return uploadDatabaseArtifactToS3(pathToDatabaseFile);
    }

    //TODO: This should be decomposed as a standalone service for easier testing
    private FileSystemMigrationReport uploadDatabaseArtifactToS3(Path target) throws InvalidMigrationStageError {
        FileSystemMigrationReport report = new DefaultFileSystemMigrationReport();
        S3UploadConfig config = new S3UploadConfig(TARGET_BUCKET_NAME, this.s3AsyncClient, target.getParent());

        S3Uploader uploader = new S3Uploader(config, report);
        Crawler crawler = new DirectoryStreamCrawler(report);

        FilesystemUploader filesystemUploader = new FilesystemUploader(crawler, uploader);

        migrationService.transition(MigrationStage.DB_MIGRATION_UPLOAD);

        filesystemUploader.uploadDirectory(target);

        this.migrationService.transition(MigrationStage.DB_MIGRATION_UPLOAD_COMPLETE);
        return report;
    }

    private void setStatus(MigrationStage status) {
        this.status.set(status);
    }

    public MigrationStage getStatus() {
        return status.get();
    }
}
