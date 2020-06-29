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

import com.atlassian.migration.datacenter.core.aws.db.restore.SsmPsqlDatabaseRestoreService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.db.DatabaseMigrationJobRunner;
import com.atlassian.migration.datacenter.core.fs.FileUploadException;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
import com.atlassian.migration.datacenter.spi.CancellableMigrationService;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError;
import com.atlassian.scheduler.config.JobId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseMigrationService implements CancellableMigrationService {
    private static Logger logger = LoggerFactory.getLogger(DatabaseMigrationService.class);

    private final Path tempDirectory;
    private final DatabaseArchivalService databaseArchivalService;
    private final DatabaseArtifactS3UploadService s3UploadService;
    private final SsmPsqlDatabaseRestoreService restoreService;
    private final MigrationService migrationService;
    private final MigrationRunner migrationRunner;
    private final AWSMigrationHelperDeploymentService migrationHelperDeploymentService;

    private final AtomicReference<Optional<LocalDateTime>> startTime = new AtomicReference<>(Optional.empty());

    public DatabaseMigrationService(Path tempDirectory, MigrationService migrationService,
                                    MigrationRunner migrationRunner, DatabaseArchivalService databaseArchivalService,
                                    DatabaseArtifactS3UploadService s3UploadService,
                                    SsmPsqlDatabaseRestoreService restoreService,
                                    AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        this.tempDirectory = tempDirectory;
        this.databaseArchivalService = databaseArchivalService;
        this.s3UploadService = s3UploadService;
        this.restoreService = restoreService;
        this.migrationService = migrationService;
        this.migrationRunner = migrationRunner;
        this.migrationHelperDeploymentService = migrationHelperDeploymentService;
    }

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and
     * should be started from ExecutorService or preferably from ScheduledJob. The
     * status of the migration can be queried via getStatus().
     */
    public FileSystemMigrationErrorReport performMigration()
            throws DatabaseMigrationFailure, InvalidMigrationStageError {
        migrationService.transition(MigrationStage.DB_MIGRATION_EXPORT);
        startTime.set(Optional.of(LocalDateTime.now()));

        Path pathToDatabaseFile;
        try {
            pathToDatabaseFile = databaseArchivalService.archiveDatabase(tempDirectory);
        } catch (DatabaseMigrationFailure e) {
            migrationService.error(e);
            throw e;
        }

        FileSystemMigrationErrorReport report;

        String bucketName = null;
        try {
            bucketName = migrationHelperDeploymentService.getMigrationS3BucketName();
        } catch (InfrastructureDeploymentError infrastructureDeploymentError) {
            migrationService.error(infrastructureDeploymentError);
            throw new DatabaseMigrationFailure("error getting migration bucket", infrastructureDeploymentError);
        }

        try {
            report = s3UploadService.upload(pathToDatabaseFile, bucketName);
        } catch (FileUploadException e) {
            migrationService.error(e);
            throw new DatabaseMigrationFailure("Error when uploading database dump to S3", e);
        }

        try {
            restoreService.restoreDatabase();
        } catch (Exception e) {
            migrationService.error(e);
            throw new DatabaseMigrationFailure("Error when restoring database", e);
        }

        return report;
    }

    public Optional<Duration> getElapsedTime() {
        Optional<LocalDateTime> start = startTime.get();
        if (!start.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(Duration.between(start.get(), LocalDateTime.now()));
    }

    public Boolean scheduleMigration() {

        JobId jobId = getScheduledJobId();
        DatabaseMigrationJobRunner jobRunner = new DatabaseMigrationJobRunner(this);

        boolean result = migrationRunner.runMigration(jobId, jobRunner);

        if (!result) {
            migrationService.error("Unable to start database migration job.");
        }
        return result;
    }

    public boolean unscheduleMigration(int migrationId) {
        JobId jobId = getScheduledJobId(migrationId);
        return migrationRunner.abortJobIfPresent(jobId);
    }

    public void abortMigration() throws InvalidMigrationStageError {
        // We always try to remove scheduled job if the system is in inconsistent state
        migrationRunner.abortJobIfPresent(getScheduledJobId());

        if (!migrationService.getCurrentStage().isDBPhase() || s3UploadService == null) {
            throw new InvalidMigrationStageError(
                    String.format("Invalid migration stage when cancelling db migration: %s",
                            migrationService.getCurrentStage()));
        }

        migrationService.transition(MigrationStage.FINAL_SYNC_ERROR);

        logger.warn("Aborting running DB migration");
    }

    private JobId getScheduledJobId() {
        return getScheduledJobId(migrationService.getCurrentMigration().getID());
    }

    private JobId getScheduledJobId(int migrationId) {
        return JobId.of(DatabaseMigrationJobRunner.KEY + migrationId);
    }
}
