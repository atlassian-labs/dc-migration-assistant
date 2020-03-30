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

import com.atlassian.migration.datacenter.core.aws.db.restore.DatabaseRestoreStageTransitionCallback;
import com.atlassian.migration.datacenter.core.aws.db.restore.SsmPsqlDatabaseRestoreService;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;

import java.nio.file.Path;

/**
 * Copyright Atlassian: 10/03/2020
 */
public class DatabaseMigrationService {
    private static final String TARGET_BUCKET_NAME = System.getProperty("S3_TARGET_BUCKET_NAME", "trebuchet-testing");

    private final Path tempDirectory;
    private final DatabaseArchivalService databaseArchivalService;
    private final DatabaseArchiveStageTransitionCallback stageTransitionCallback;
    private final DatabaseArtifactS3UploadService s3UploadService;
    private final DatabaseUploadStageTransitionCallback uploadStageTransitionCallback;
    private final SsmPsqlDatabaseRestoreService restoreService;
    private final DatabaseRestoreStageTransitionCallback restoreStageTransitionCallback;


    public DatabaseMigrationService(Path tempDirectory, DatabaseArchivalService databaseArchivalService, DatabaseArchiveStageTransitionCallback stageTransitionCallback, DatabaseArtifactS3UploadService s3UploadService, DatabaseUploadStageTransitionCallback uploadStageTransitionCallback, SsmPsqlDatabaseRestoreService restoreService, DatabaseRestoreStageTransitionCallback restoreStageTransitionCallback)
    {
        this.tempDirectory = tempDirectory;
        this.databaseArchivalService = databaseArchivalService;
        this.stageTransitionCallback = stageTransitionCallback;
        this.s3UploadService = s3UploadService;
        this.uploadStageTransitionCallback = uploadStageTransitionCallback;
        this.restoreService = restoreService;
        this.restoreStageTransitionCallback = restoreStageTransitionCallback;
    }

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob. The status of the migration can be queried via getStatus().
     */
    public FileSystemMigrationErrorReport performMigration() throws DatabaseMigrationFailure, InvalidMigrationStageError {
        Path pathToDatabaseFile = databaseArchivalService.archiveDatabase(tempDirectory, stageTransitionCallback);

        FileSystemMigrationErrorReport report;

        try {
            report = s3UploadService.upload(pathToDatabaseFile, TARGET_BUCKET_NAME, this.uploadStageTransitionCallback);
        } catch (FilesystemUploader.FileUploadException e) {
            throw new DatabaseMigrationFailure("Error when uploading database dump to S3", e);
        }
        restoreService.restoreDatabase(restoreStageTransitionCallback);
        return report;
    }

}

