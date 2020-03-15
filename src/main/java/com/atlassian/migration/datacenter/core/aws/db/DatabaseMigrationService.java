package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.fs.Crawler;
import com.atlassian.migration.datacenter.core.fs.DirectoryStreamCrawler;
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig;
import com.atlassian.migration.datacenter.core.fs.S3Uploader;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFilesystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright Atlassian: 10/03/2020
 */
public class DatabaseMigrationService
{
    private final ApplicationConfiguration applicationConfiguration;
    private final Optional<URI> endpointOverride;
    private final Path tempDirectory;
    private final S3AsyncClient s3AsyncClient;

    private Process extractorProcess;
    private AtomicReference<MigrationStatus> status = new AtomicReference();

    //TODO: Move tempdirectory away from the constructor and pass that into the method instead
    public DatabaseMigrationService(ApplicationConfiguration applicationConfiguration,
                                    Path tempDirectory, S3AsyncClient client)
    {
        this(applicationConfiguration, tempDirectory, client, null);
    }

    DatabaseMigrationService(ApplicationConfiguration applicationConfiguration,
                             Path tempDirectory,
                             S3AsyncClient s3AsyncClient,
                             URI endpointOverride) {
        this.applicationConfiguration = applicationConfiguration;
        this.tempDirectory = tempDirectory;
        this.s3AsyncClient = s3AsyncClient;
        this.endpointOverride = Optional.ofNullable(endpointOverride);
        this.setStatus(MigrationStatus.NOT_STARTED);
    }

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob. The status of the migration can be queried via getStatus().
     */
    public FileSystemMigrationErrorReport performMigration() throws DatabaseMigrationFailure
    {
        DatabaseExtractor extractor = DatabaseExtractorFactory.getExtractor(applicationConfiguration);
        Path target = tempDirectory.resolve("db.dump");

        extractorProcess = extractor.startDatabaseDump(target);
        setStatus(MigrationStatus.DUMP_IN_PROGRESS);
        try {
            extractorProcess.waitFor();
        } catch (Exception e) {
            String msg = "Error while waiting for DB extractor to finish";
            setStatus(MigrationStatus.error(msg, e));
            throw new DatabaseMigrationFailure(msg, e);
        }
        setStatus(MigrationStatus.DUMP_COMPLETE);


        ConcurrentLinkedQueue<Path> uploadQueue = new ConcurrentLinkedQueue<>();
        FileSystemMigrationProgress progress = new DefaultFilesystemMigrationProgress();
        FileSystemMigrationErrorReport report = new DefaultFileSystemMigrationErrorReport();

        String bucket = System.getProperty("S3_TARGET_BUCKET_NAME", "trebuchet-testing");
        S3UploadConfig config = new S3UploadConfig(bucket, this.s3AsyncClient, target.getParent());

        S3Uploader uploader = new S3Uploader(config, report, progress);
        Crawler crawler = new DirectoryStreamCrawler(report, progress);

        setStatus(MigrationStatus.UPLOAD_IN_PROGRESS);
        try {
            crawler.crawlDirectory(target, uploadQueue);
        } catch (IOException e) {
            String msg = "Failed to read the database dump directory.";
            setStatus(MigrationStatus.error(msg, e));
            throw new DatabaseMigrationFailure(msg, e);
        }
        uploader.upload(uploadQueue, new AtomicBoolean(true));
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
