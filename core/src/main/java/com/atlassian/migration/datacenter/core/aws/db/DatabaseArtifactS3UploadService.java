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

import com.atlassian.migration.datacenter.core.aws.MigrationStageCallback;
import com.atlassian.migration.datacenter.core.fs.Crawler;
import com.atlassian.migration.datacenter.core.fs.DefaultFilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.DirectoryStreamCrawler;
import com.atlassian.migration.datacenter.core.fs.FileSystemMigrationReportManager;
import com.atlassian.migration.datacenter.core.fs.FileUploadException;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.ReportType;
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig;
import com.atlassian.migration.datacenter.core.fs.S3Uploader;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.function.Supplier;

public class DatabaseArtifactS3UploadService {
    private final Supplier<S3AsyncClient> s3AsyncClientSupplier;
    private final MigrationStageCallback migrationStageCallback;
    private S3AsyncClient s3AsyncClient;
    private final FileSystemMigrationReportManager reportManager;

    public DatabaseArtifactS3UploadService(Supplier<S3AsyncClient> s3AsyncClientSupplier,
                                           MigrationStageCallback migrationStageCallback,
                                           FileSystemMigrationReportManager reportManager) {
        this.s3AsyncClientSupplier = s3AsyncClientSupplier;
        this.migrationStageCallback = migrationStageCallback;
        this.reportManager = reportManager;
    }

    @PostConstruct
    public void postConstruct() {
        this.s3AsyncClient = this.s3AsyncClientSupplier.get();
    }

    public FileSystemMigrationReport upload(Path target, String targetBucketName) throws InvalidMigrationStageError, FileUploadException
    {
        s3AsyncClient = s3AsyncClientSupplier.get();
        this.migrationStageCallback.assertInStartingStage();

        FileSystemMigrationReport report = reportManager.resetReport(ReportType.Database);
        FilesystemUploader filesystemUploader = buildFileSystemUploader(target, targetBucketName, report, s3AsyncClient);

        this.migrationStageCallback.transitionToServiceWaitStage();
        filesystemUploader.uploadDirectory(target);

        this.migrationStageCallback.transitionToServiceNextStage();
        return report;
    }

    //TODO: Use builder pattern instead of creating dependencies like this.
    private static FilesystemUploader buildFileSystemUploader(Path target, String targetBucketName, FileSystemMigrationReport migrationReport, S3AsyncClient s3Client) {
        S3UploadConfig config = new S3UploadConfig(targetBucketName, s3Client, target.getParent());
        S3Uploader uploader = new S3Uploader(config, migrationReport);
        Crawler crawler = new DirectoryStreamCrawler(migrationReport);
        return new DefaultFilesystemUploader(crawler, uploader);
    }
}
