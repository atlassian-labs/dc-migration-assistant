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
import com.atlassian.util.concurrent.Supplier;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright Atlassian: 10/03/2020
 */
public class DatabaseMigrationService
{
    private static final String TARGET_BUCKET_NAME = System.getProperty("S3_TARGET_BUCKET_NAME", "trebuchet-testing");

    private final Path tempDirectory;
    private S3AsyncClient s3AsyncClient;
    @Deprecated
    private final MigrationService migrationService;
    private Supplier<S3AsyncClient> s3AsyncClientSupplier;

    private AtomicReference<MigrationStage> status = new AtomicReference<>();
    private DatabaseArchivalService databaseArchivalService;
    private final DatabaseArchiveStageTransitionCallback stageTransitionCallback;


    public DatabaseMigrationService(Path tempDirectory, Supplier<S3AsyncClient> s3AsyncClientSupplier, MigrationService migrationService, DatabaseArchivalService databaseArchivalService, DatabaseArchiveStageTransitionCallback stageTransitionCallback)
    {
        this.tempDirectory = tempDirectory;
        this.s3AsyncClientSupplier = s3AsyncClientSupplier;
        this.migrationService = migrationService;
        this.databaseArchivalService = databaseArchivalService;
        this.stageTransitionCallback = stageTransitionCallback;
    }

    @PostConstruct
    public void postConstruct() {
        this.s3AsyncClient = this.s3AsyncClientSupplier.get();
    }

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob. The status of the migration can be queried via getStatus().
     */
    public FileSystemMigrationErrorReport performMigration() throws DatabaseMigrationFailure, InvalidMigrationStageError {
        Path pathToDatabaseFile = databaseArchivalService.archiveDatabase(tempDirectory, stageTransitionCallback);
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

