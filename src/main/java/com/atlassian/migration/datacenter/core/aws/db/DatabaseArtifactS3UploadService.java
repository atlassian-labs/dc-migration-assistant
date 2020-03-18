/*
 * Copyright (c) 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and  limitations under the License.
 */

package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.Crawler;
import com.atlassian.migration.datacenter.core.fs.DirectoryStreamCrawler;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig;
import com.atlassian.migration.datacenter.core.fs.S3Uploader;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.util.concurrent.Supplier;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

public class DatabaseArtifactS3UploadService {
    private final Supplier<S3AsyncClient> s3AsyncClientSupplier;
    private S3AsyncClient s3AsyncClient;
    private final FileSystemMigrationReport fileSystemMigrationReport;
    private final MigrationService migrationService;

    public DatabaseArtifactS3UploadService(Supplier<S3AsyncClient> s3AsyncClientSupplier, MigrationService migrationService) {
        this.s3AsyncClientSupplier = s3AsyncClientSupplier;
        this.migrationService = migrationService;
        this.fileSystemMigrationReport = new DefaultFileSystemMigrationReport();
    }

    @PostConstruct
    public void postConstruct(){
        this.s3AsyncClient = this.s3AsyncClientSupplier.get();
    }

    public FileSystemMigrationReport uploadDatabaseArtifactToS3(Path target, String targetBucketName) throws InvalidMigrationStageError {
        FilesystemUploader filesystemUploader = buildFileSystemUploader(target, targetBucketName);

        migrationService.transition(MigrationStage.DB_MIGRATION_UPLOAD);

        filesystemUploader.uploadDirectory(target);

        this.migrationService.transition(MigrationStage.DB_MIGRATION_UPLOAD_COMPLETE);
        return fileSystemMigrationReport;
    }

    //TODO: Move to builder
    private FilesystemUploader buildFileSystemUploader(Path target, String targetBucketName) {
        S3UploadConfig config = new S3UploadConfig(targetBucketName, this.s3AsyncClient, target.getParent());
        S3Uploader uploader = new S3Uploader(config, fileSystemMigrationReport);
        Crawler crawler = new DirectoryStreamCrawler(fileSystemMigrationReport);
        return new FilesystemUploader(crawler, uploader);
    }
}
