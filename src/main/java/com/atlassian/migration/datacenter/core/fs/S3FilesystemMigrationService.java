package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFilesystemMigrationProgress;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static com.atlassian.migration.datacenter.spi.MigrationStage.FS_MIGRATION_COPY;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.DONE;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.FAILED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.RUNNING;

@Component
public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(S3FilesystemMigrationService.class);

    private static final int NUM_UPLOAD_THREADS = Integer.getInteger("NUM_UPLOAD_THREADS", 1);
    private static final String BUCKET_NAME = System.getProperty("S3_TARGET_BUCKET_NAME", "trebuchet-testing");

    private final AwsCredentialsProvider credentialsProvider;
    private final RegionService regionService;
    private final JiraHome jiraHome;
    private final MigrationService migrationService;
    private final SchedulerService schedulerService;

    private FileSystemMigrationReport report;
    private AtomicBoolean isDoneCrawling;
    private ConcurrentLinkedQueue<Path> uploadQueue;
    private S3UploadConfig s3UploadConfig;

    //TODO: Region Service and provider will be replaced by the S3 Client
    public S3FilesystemMigrationService(RegionService regionService,
                                        AwsCredentialsProvider credentialsProvider,
                                        JiraHome jiraHome,
                                        MigrationService migrationService, SchedulerService schedulerService) {
        this.regionService = regionService;
        this.credentialsProvider = credentialsProvider;
        this.jiraHome = jiraHome;
        this.migrationService = migrationService;
        this.schedulerService = schedulerService;
    }

    @Override
    public boolean isRunning() {
        return this.migrationService.getCurrentStage().equals(MigrationStage.WAIT_FS_MIGRATION_COPY);
    }

    @Override
    public FileSystemMigrationReport getReport() {
        return report;
    }

    @Override
    public Boolean scheduleMigration() {
        Migration currentMigration = this.migrationService.getCurrentMigration();
        if (currentMigration.getStage() != FS_MIGRATION_COPY) {
            return false;
        }

        final JobRunnerKey runnerKey = JobRunnerKey.of(S3UploadJobRunner.KEY);
        JobId jobId = JobId.of(S3UploadJobRunner.KEY + currentMigration.getID());
        logger.info("Starting filesystem migration");

        //TODO: Can the job runner be injected? It has no state
        schedulerService.registerJobRunner(runnerKey, new S3UploadJobRunner(this));
        logger.info("Registered new job runner for S3");

        JobConfig jobConfig = JobConfig.forJobRunnerKey(runnerKey)
                .withSchedule(null) // run now
                .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
        try {
            logger.info("Scheduling new job for S3 upload runner");

            this.migrationService.transition(MigrationStage.FS_MIGRATION_COPY, MigrationStage.WAIT_FS_MIGRATION_COPY);

            schedulerService.scheduleJob(jobId, jobConfig);
        } catch (SchedulerServiceException | InvalidMigrationStageError e) {
            logger.error("Exception when scheduling S3 upload job", e);
            this.schedulerService.unscheduleJob(jobId);
            migrationService.error();
            return false;
        }
        return true;
    }

    /**
     * Start filesystem migration to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob
     */
    @Override
    public void startMigration() throws InvalidMigrationStageError {
        if (isRunning()) {
            logger.warn("Filesystem migration is currently in progress, aborting new execution.");
            return;
        }
        initialiseMigration();

        CompletionService<Void> uploadResults = startUploadingFromQueue();

        populateUploadQueue();

        waitForUploadsToComplete(uploadResults);

        finaliseMigration();
    }

    private void initialiseMigration() throws InvalidMigrationStageError {
        report = new DefaultFileSystemMigrationReport(new DefaultFileSystemMigrationErrorReport(), new DefaultFilesystemMigrationProgress());
        isDoneCrawling = new AtomicBoolean(false);
        uploadQueue = new ConcurrentLinkedQueue<>();

        migrationService.transition(MigrationStage.FS_MIGRATION_COPY, MigrationStage.WAIT_FS_MIGRATION_COPY);

        report.setStatus(RUNNING);

        S3AsyncClient s3AsyncClient = buildS3Client();
        s3UploadConfig = new S3UploadConfig(getS3Bucket(), s3AsyncClient, getSharedHomeDir());
    }

    private S3AsyncClient buildS3Client() {
        return S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
    }

    private CompletionService<Void> startUploadingFromQueue() {
        ExecutorService uploadExecutorService = Executors.newFixedThreadPool(NUM_UPLOAD_THREADS);
        CompletionService<Void> completionService = new ExecutorCompletionService<>(uploadExecutorService);

        Runnable uploaderFunction = () -> {
            Uploader uploader = new S3Uploader(s3UploadConfig, report, report);
            uploader.upload(uploadQueue, isDoneCrawling);
        };

        IntStream.range(0, NUM_UPLOAD_THREADS).forEach(x -> completionService.submit(uploaderFunction, null));

        return completionService;
    }

    private void populateUploadQueue() {
        Crawler homeCrawler = new DirectoryStreamCrawler(report, report);
        try {
            homeCrawler.crawlDirectory(getSharedHomeDir(), uploadQueue);
        } catch (IOException e) {
            logger.error("Failed to traverse home directory for S3 transfer", e);
            report.setStatus(FAILED);
        } finally {
            // FIXME: the uploader will continue uploading until the queue is empty even though we probably need to abort in this scenario as it's indeterminate whether all files have been uploaded or not (should we try fix this now or create a bug and follow up?)
            isDoneCrawling.set(true);
            logger.info("Finished traversing directory [{}], {} files are remaining to upload", s3UploadConfig.getSharedHome(), uploadQueue.size());
        }
    }

    private void waitForUploadsToComplete(CompletionService<Void> uploadResults) {
        IntStream.range(0, NUM_UPLOAD_THREADS)
                .forEach(i -> {
                    try {
                        uploadResults.take().get();
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Failed to upload home directory to S3", e);
                        report.setStatus(FAILED);
                    }
                });
    }

    private void finaliseMigration() throws InvalidMigrationStageError {
        if (report.getStatus().equals(DONE)) {
            this.migrationService.transition(MigrationStage.WAIT_FS_MIGRATION_COPY, MigrationStage.OFFLINE_WARNING);
        } else if (!report.getStatus().equals(FAILED)) {
            this.migrationService.error();
            report.setStatus(DONE);
        }
    }

    private String getS3Bucket() {
        return BUCKET_NAME;
    }

    private Path getSharedHomeDir() {
        return jiraHome.getHome().toPath();
    }
}