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

package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.core.fs.copy.S3BulkCopy;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloadManager;
import com.atlassian.migration.datacenter.core.fs.jira.listener.JiraIssueAttachmentListener;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.FileSystemMigrationFailure;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.scheduler.config.JobId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import static com.atlassian.migration.datacenter.spi.MigrationStage.FS_MIGRATION_COPY;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.DONE;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.DOWNLOADING;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.FAILED;

public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(S3FilesystemMigrationService.class);


    private final Environment environment;
    private final MigrationService migrationService;
    private final MigrationRunner migrationRunner;
    private final S3SyncFileSystemDownloadManager fileSystemDownloadManager;
    private final JiraIssueAttachmentListener attachmentListener;
    private final S3BulkCopy bulkCopy;

    private FileSystemMigrationReportManager reportManager;

    public S3FilesystemMigrationService(Environment environment,
                                        S3SyncFileSystemDownloadManager fileSystemDownloadManager,
                                        MigrationService migrationService,
                                        MigrationRunner migrationRunner,
                                        JiraIssueAttachmentListener attachmentListener,
                                        S3BulkCopy bulkCopy,
                                        FileSystemMigrationReportManager reportManager) {
        this.environment = environment;
        this.migrationService = migrationService;
        this.migrationRunner = migrationRunner;
        this.fileSystemDownloadManager = fileSystemDownloadManager;
        this.attachmentListener = attachmentListener;
        this.bulkCopy = bulkCopy;
        this.reportManager = reportManager;
    }

    @Override
    public boolean isRunning() {
        return this.migrationService.getCurrentStage().equals(MigrationStage.FS_MIGRATION_COPY_WAIT);
    }

    @Override
    public boolean scheduleMigration() throws InvalidMigrationStageError {
        migrationService.assertCurrentStage(FS_MIGRATION_COPY);

        JobId jobId = getScheduledJobIdForMigration(migrationService.getCurrentMigration().getID());
        S3UploadJobRunner jobRunner = new S3UploadJobRunner(this, reportManager);

        boolean result = migrationRunner.runMigration(jobId, jobRunner);

        if (!result) {
            migrationService.error("Error starting filesystem migration job.");
        }
        return result;
    }

    /**
     * Start filesystem migration to S3 bucket. This is a blocking operation and
     * should be started from ExecutorService or preferably from ScheduledJob
     */
    @Override
    public void startMigration() throws InvalidMigrationStageError {
        if (isRunning()) {
            logger.warn("Filesystem migration is currently in progress, aborting new execution.");
            return;
        }

        migrationService.transition(MigrationStage.FS_MIGRATION_COPY_WAIT);

        logger.info("Enabling file listener to listen to attachment updates while migration is in progress");
        attachmentListener.start();

        FileSystemMigrationReport report = reportManager.resetReport(ReportType.Filesystem);

        logger.info("commencing upload of shared home");
        try {
            bulkCopy.copySharedHomeToS3();

            logger.info("upload of shared home complete. commencing shared home download");
            report.setStatus(DOWNLOADING);
            fileSystemDownloadManager.downloadFileSystem(report);

            report.setStatus(DONE);

            logger.info("Completed file system migration. Transitioning to next stage.");
            migrationService.transition(MigrationStage.OFFLINE_WARNING);
        } catch (FileSystemMigrationFailure e) {
            logger.error("Encountered critical error during file system migration");
            report.setStatus(FAILED);
            migrationService.error(e.getMessage());
        }
    }

    @Override
    public void abortMigration() throws InvalidMigrationStageError {
        // We always try to remove scheduled job if the system is in inconsistent state
        int migrationId = migrationService.getCurrentMigration().getID();
        unscheduleMigration(migrationId);

        if (!isRunning()) {
            throw new InvalidMigrationStageError(
                    String.format("Invalid migration stage when cancelling filesystem migration: %s",
                            migrationService.getCurrentStage()));
        }

        logger.warn("Aborting running filesystem migration");
        FileSystemMigrationReport report = reportManager.resetReport(ReportType.Filesystem);
        report.setStatus(FAILED);
        bulkCopy.abortCopy();

        migrationService.error("File system migration was aborted");
    }

    private JobId getScheduledJobIdForMigration(int migrationId) {
        return JobId.of(S3UploadJobRunner.KEY + migrationId);
    }

    @Override
    public boolean unscheduleMigration(int migrationId) {
        return migrationRunner.abortJobIfPresent(getScheduledJobIdForMigration(migrationId));
    }
}
