package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
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
    private final ApplicationConfiguration applicationConfiguration;
    private final Path tempDirectory;
    private final S3AsyncClient s3AsyncClient;
    private final MigrationService migrationService;

    private Process extractorProcess;
    private AtomicReference<MigrationStatus> status = new AtomicReference();

    public DatabaseMigrationService(ApplicationConfiguration applicationConfiguration,
                                    Path tempDirectory,
                                    S3AsyncClient s3AsyncClient, MigrationService migrationService)
    {
        this.applicationConfiguration = applicationConfiguration;
        this.tempDirectory = tempDirectory;
        this.s3AsyncClient = s3AsyncClient;
        this.migrationService = migrationService;
        this.setStatus(MigrationStatus.NOT_STARTED);
    }

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob. The status of the migration can be queried via getStatus().
     */
    public FileSystemMigrationErrorReport performMigration() throws DatabaseMigrationFailure, InvalidMigrationStageError {
        Path pathToDatabaseFile = dumpDatabaseToFilesystem();
        FileSystemMigrationReport report = uploadDatabaseArtifactToS3(pathToDatabaseFile);
        return report;
    }

    //TODO: This should be decomposed as a standalone service for easier testing
    private Path dumpDatabaseToFilesystem() throws InvalidMigrationStageError {
        DatabaseExtractor extractor = DatabaseExtractorFactory.getExtractor(applicationConfiguration);
        Path target = tempDirectory.resolve("db.dump");

        this.migrationService.transition(MigrationStage.OFFLINE_WARNING, MigrationStage.DB_MIGRATION_EXPORT);

        extractorProcess = extractor.startDatabaseDump(target);
        setStatus(MigrationStatus.DUMP_IN_PROGRESS);
        this.migrationService.transition(MigrationStage.DB_MIGRATION_EXPORT, MigrationStage.WAIT_DB_MIGRATION_EXPORT);
        try {
            extractorProcess.waitFor();
        } catch (Exception e) {
            String msg = "Error while waiting for DB extractor to finish";
            setStatus(MigrationStatus.error(msg, e));
            throw new DatabaseMigrationFailure(msg, e);
        }

        setStatus(MigrationStatus.DUMP_COMPLETE);
        this.migrationService.transition(MigrationStage.WAIT_DB_MIGRATION_EXPORT, MigrationStage.DB_MIGRATION_UPLOAD);
        return target;
    }

    //TODO: This should be decomposed as a standalone service for easier testing
    private FileSystemMigrationReport uploadDatabaseArtifactToS3(Path target) throws InvalidMigrationStageError {
        FileSystemMigrationReport report = new DefaultFileSystemMigrationReport();
        S3UploadConfig config = new S3UploadConfig(TARGET_BUCKET_NAME, this.s3AsyncClient, target.getParent());

        S3Uploader uploader = new S3Uploader(config, report);
        Crawler crawler = new DirectoryStreamCrawler(report);

        FilesystemUploader filesystemUploader = new FilesystemUploader(crawler, uploader);

        setStatus(MigrationStatus.UPLOAD_IN_PROGRESS);

        filesystemUploader.uploadDirectory(target);

        this.migrationService.transition(MigrationStage.DB_MIGRATION_UPLOAD, MigrationStage.DB_MIGRATION_UPLOAD_COMPLETE);
        setStatus(MigrationStatus.UPLOAD_COMPLETE);
        setStatus(MigrationStatus.FINISHED);
        return report;
    }

    private void setStatus(MigrationStatus status) {
        this.status.set(status);
    }

    public MigrationStatus getStatus() {
        return status.get();
    }
}
