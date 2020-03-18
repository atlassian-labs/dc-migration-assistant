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
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.util.concurrent.Supplier;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

public class DatabaseArtifactS3UploadService {
    private final Supplier<S3AsyncClient> s3AsyncClientSupplier;
    private S3AsyncClient s3AsyncClient;
    private final FileSystemMigrationReport fileSystemMigrationReport;

    public DatabaseArtifactS3UploadService(Supplier<S3AsyncClient> s3AsyncClientSupplier) {
        this.s3AsyncClientSupplier = s3AsyncClientSupplier;
        this.fileSystemMigrationReport = new DefaultFileSystemMigrationReport();
    }

    @PostConstruct
    public void postConstruct(){
        this.s3AsyncClient = this.s3AsyncClientSupplier.get();
    }

    public FileSystemMigrationReport upload(Path target, String targetBucketName, DatabaseUploadStageTransitionCallback callback) throws InvalidMigrationStageError {
        callback.transitionToServiceStartStage();
        FilesystemUploader filesystemUploader = buildFileSystemUploader(target, targetBucketName, fileSystemMigrationReport, s3AsyncClient);

        callback.transitionToServiceWaitStage();
        filesystemUploader.uploadDirectory(target);

        callback.transitionToServiceNextStage();
        return fileSystemMigrationReport;
    }

    //TODO: Use builder pattern instead of creating dependencies like this.
    private static FilesystemUploader buildFileSystemUploader(Path target, String targetBucketName, FileSystemMigrationReport migrationReport, S3AsyncClient s3Client) {
        S3UploadConfig config = new S3UploadConfig(targetBucketName, s3Client, target.getParent());
        S3Uploader uploader = new S3Uploader(config, migrationReport);
        Crawler crawler = new DirectoryStreamCrawler(migrationReport);
        return new FilesystemUploader(crawler, uploader);
    }
}
