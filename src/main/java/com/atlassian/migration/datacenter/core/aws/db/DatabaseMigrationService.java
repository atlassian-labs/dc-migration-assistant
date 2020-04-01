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
import com.atlassian.migration.datacenter.core.db.DatabaseMigrationJobRunner;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.S3UploadJobRunner;
import com.atlassian.migration.datacenter.core.util.MigrationJobRunner;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.config.JobId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.FAILED;

/**
 * Copyright Atlassian: 10/03/2020
 */
public class DatabaseMigrationService
{
    private static Logger logger = LoggerFactory.getLogger(DatabaseMigrationService.class);

    private static final String TARGET_BUCKET_NAME = System.getProperty("S3_TARGET_BUCKET_NAME", "trebuchet-testing");

    private final Path tempDirectory;
    private final DatabaseArchivalService databaseArchivalService;
    private final DatabaseArchiveStageTransitionCallback stageTransitionCallback;
    private final DatabaseArtifactS3UploadService s3UploadService;
    private final DatabaseUploadStageTransitionCallback uploadStageTransitionCallback;
    private final SsmPsqlDatabaseRestoreService restoreService;
    private final DatabaseRestoreStageTransitionCallback restoreStageTransitionCallback;
    private final MigrationService migrationService;
    private final MigrationRunner migrationRunner;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public DatabaseMigrationService(Path tempDirectory,
                                    MigrationService migrationService,
                                    MigrationRunner migrationRunner,
                                    DatabaseArchivalService databaseArchivalService,
                                    DatabaseArchiveStageTransitionCallback stageTransitionCallback,
                                    DatabaseArtifactS3UploadService s3UploadService,
                                    DatabaseUploadStageTransitionCallback uploadStageTransitionCallback,
                                    SsmPsqlDatabaseRestoreService restoreService,
                                    DatabaseRestoreStageTransitionCallback restoreStageTransitionCallback)
    {
        this.tempDirectory = tempDirectory;
        this.databaseArchivalService = databaseArchivalService;
        this.stageTransitionCallback = stageTransitionCallback;
        this.s3UploadService = s3UploadService;
        this.uploadStageTransitionCallback = uploadStageTransitionCallback;
        this.restoreService = restoreService;
        this.restoreStageTransitionCallback = restoreStageTransitionCallback;
        this.migrationService = migrationService;
        this.migrationRunner = migrationRunner;
    }

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob. The status of the migration can be queried via getStatus().
     */
    public FileSystemMigrationErrorReport performMigration() throws DatabaseMigrationFailure, InvalidMigrationStageError {
        migrationService.transition(MigrationStage.DB_MIGRATION_EXPORT);

        Path pathToDatabaseFile = databaseArchivalService.archiveDatabase(tempDirectory, stageTransitionCallback);

        migrationService.transition(MigrationStage.DB_MIGRATION_EXPORT_WAIT);

        FileSystemMigrationErrorReport report;

        migrationService.transition(MigrationStage.DB_MIGRATION_UPLOAD);
        try {
            report = s3UploadService.upload(pathToDatabaseFile, TARGET_BUCKET_NAME, this.uploadStageTransitionCallback);
        } catch (FilesystemUploader.FileUploadException e) {
            migrationService.error(e);
            throw new DatabaseMigrationFailure("Error when uploading database dump to S3", e);
        }
        migrationService.transition(MigrationStage.DB_MIGRATION_UPLOAD_WAIT);

        migrationService.transition(MigrationStage.DATA_MIGRATION_IMPORT);
        try {
            restoreService.restoreDatabase(restoreStageTransitionCallback);
        } catch (Exception e) {
            migrationService.error(e);
            throw new DatabaseMigrationFailure("Error when restoring database", e);
        }
        migrationService.transition(MigrationStage.DB_MIGRATION_UPLOAD_WAIT);

        return report;
    }

    public Boolean scheduleMigration() {

        JobId jobId = getScheduledJobId();
        DatabaseMigrationJobRunner jobRunner = new DatabaseMigrationJobRunner(this);

        boolean result = migrationRunner.runMigration(jobId, jobRunner);

        if (!result) {
            migrationService.error();
        }
        return result;
    }

    public void abortMigration() throws InvalidMigrationStageError {
        // We always try to remove scheduled job if the system is in inconsistent state
        migrationRunner.abortJobIfPresesnt(getScheduledJobId());

        if (!migrationService.getCurrentStage().isDBPhase() || s3UploadService == null) {
            throw new InvalidMigrationStageError(String.format("Invalid migration stage when cancelling filesystem migration: %s", migrationService.getCurrentStage()));
        }

        logger.warn("Aborting running filesystem migration");

        migrationService.error();
    }

    private JobId getScheduledJobId() {
        return JobId.of(DatabaseMigrationJobRunner.KEY + migrationService.getCurrentMigration().getID());
    }

}

