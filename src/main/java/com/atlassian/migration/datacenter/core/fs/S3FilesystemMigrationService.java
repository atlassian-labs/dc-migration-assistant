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

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.exceptions.FileSystemMigrationFailure;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloadManager;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.util.concurrent.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.atlassian.migration.datacenter.spi.MigrationStage.FS_MIGRATION_COPY;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.DONE;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.FAILED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.RUNNING;

public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(S3FilesystemMigrationService.class);

    private static final String OVERRIDE_UPLOAD_DIRECTORY = System.getProperty("com.atlassian.migration.datacenter.fs.overrideJiraHome", "");
    private static final String BUCKET_NAME = System.getProperty("S3_TARGET_BUCKET_NAME", "trebuchet-testing");

    private S3AsyncClient s3AsyncClient;
    private final JiraHome jiraHome;
    private final MigrationService migrationService;
    private final MigrationRunner migrationRunner;
    private final S3SyncFileSystemDownloadManager fileSystemDownloadManager;
    private Supplier<S3AsyncClient> s3AsyncClientSupplier;

    private FileSystemMigrationReport report;
    private FilesystemUploader fsUploader;

    public S3FilesystemMigrationService(Supplier<S3AsyncClient> s3AsyncClientSupplier,
                                        JiraHome jiraHome,
                                        S3SyncFileSystemDownloadManager fileSystemDownloadManager,
                                        MigrationService migrationService,
                                        MigrationRunner migrationRunner) {
        this.s3AsyncClientSupplier = s3AsyncClientSupplier;
        this.jiraHome = jiraHome;
        this.migrationService = migrationService;
        this.migrationRunner = migrationRunner;
        this.fileSystemDownloadManager = fileSystemDownloadManager;

        this.report = new DefaultFileSystemMigrationReport();
    }

    @PostConstruct
    public void postConstruct() {
        this.s3AsyncClient = this.s3AsyncClientSupplier.get();
    }

    @Override
    public boolean isRunning() {
        return this.migrationService.getCurrentStage().equals(MigrationStage.FS_MIGRATION_COPY_WAIT);
    }

    @Override
    public FileSystemMigrationReport getReport() {
        return report;
    }

    @Override
    public Boolean scheduleMigration() throws InvalidMigrationStageError {
        Migration currentMigration = this.migrationService.getCurrentMigration();
        if (currentMigration.getStage() != FS_MIGRATION_COPY) {
            throw new InvalidMigrationStageError(
                    String.format(
                            "Cannot start filesystem migration as the system is not ready. Required state should be %s but is %s",
                            FS_MIGRATION_COPY,
                            currentMigration.getStage()));
        }

        JobId jobId = getScheduledJobId();
        S3UploadJobRunner jobRunner = new S3UploadJobRunner(this);

        boolean result = migrationRunner.runMigration(jobId, jobRunner);

        if (!result) {
            migrationService.error();
        }
        return result;
    }

    /**
     * Start filesystem migration to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob
     */
    @Override
    public void startMigration() throws InvalidMigrationStageError {
        logger.trace("Beginning migration. Uploading shared home dir {} to S3 bucket {}", getSharedHomeDir(), getS3Bucket());
        if (isRunning()) {
            logger.warn("Filesystem migration is currently in progress, aborting new execution.");
            return;
        }

        s3AsyncClient = this.s3AsyncClientSupplier.get();
        report = new DefaultFileSystemMigrationReport();

        migrationService.transition(MigrationStage.FS_MIGRATION_COPY_WAIT);
        report.setStatus(RUNNING);

        Crawler homeCrawler = new DirectoryStreamCrawler(report);

        S3UploadConfig s3UploadConfig = new S3UploadConfig(getS3Bucket(), s3AsyncClient, getSharedHomeDir());
        Uploader s3Uploader = new S3Uploader(s3UploadConfig, report);

        fsUploader = new FilesystemUploader(homeCrawler, s3Uploader);

        logger.info("commencing upload of shared home");
        try {
            fsUploader.uploadDirectory(getSharedHomeDir());

            logger.info("upload of shared home complete. commencing shared home download");
            fileSystemDownloadManager.downloadFileSystem();

            report.setStatus(DONE);

            logger.info("Completed file system migration. Transitioning to next stage.");
            migrationService.transition(MigrationStage.OFFLINE_WARNING);
        } catch (FileSystemMigrationFailure e) {
            logger.error("Encountered critical error during file system migration");
            report.setStatus(FAILED);
            migrationService.error();
        }
    }

    @Override
    public void abortMigration() throws InvalidMigrationStageError {
        // We always try to remove scheduled job if the system is in inconsistent state
        migrationRunner.abortJobIfPresesnt(getScheduledJobId());

        if (!isRunning() || fsUploader == null) {
            throw new InvalidMigrationStageError(String.format("Invalid migration stage when cancelling filesystem migration: %s", migrationService.getCurrentStage()));
        }

        logger.warn("Aborting running filesystem migration");
        fsUploader.abort();
        report.setStatus(FAILED);

        migrationService.error();
    }

    private String getS3Bucket() {
        return BUCKET_NAME;
    }

    private JobId getScheduledJobId() {
        return JobId.of(S3UploadJobRunner.KEY + migrationService.getCurrentMigration().getID());
    }

    private Path getSharedHomeDir() {
        if (!OVERRIDE_UPLOAD_DIRECTORY.equals("")) {
            return Paths.get(OVERRIDE_UPLOAD_DIRECTORY);
        }
        return jiraHome.getHome().toPath();
    }
}