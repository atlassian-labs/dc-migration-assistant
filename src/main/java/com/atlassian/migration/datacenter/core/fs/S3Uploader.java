package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class S3Uploader implements Uploader {
    private static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);
    private static final int MS_TO_WAIT_FOR_CRAWLER = 500;
    private static final int MAX_OPEN_CONNECTIONS = 50;
    private static final long MAXIMUM_FILE_SIZE_TO_UPLOAD = 5 * 1024 * 1024 * 1024L; // 5GB  https://docs.aws.amazon.com/AmazonS3/latest/dev/UploadingObjects.html

    private final FileSystemMigrationErrorReport report;
    private final FileSystemMigrationProgress progress;
    private final Queue<S3UploadOperation> responsesQueue = new LinkedList<>();
    private final S3UploadConfig config;

    public S3Uploader(S3UploadConfig config, FileSystemMigrationErrorReport report, FileSystemMigrationProgress progress) {
        this.config = config;
        this.report = report;
        this.progress = progress;
    }

    @Override
    public void upload(ConcurrentLinkedQueue<Path> queue, AtomicBoolean isCrawlDone) {
        Path path;
        while ((path = queue.poll()) != null || !isCrawlDone.get()) {
            if (responsesQueue.size() >= MAX_OPEN_CONNECTIONS) {
                responsesQueue.forEach(this::handlePutObjectResponse);
            }
            if (path == null) {
                try {
                    Thread.sleep(MS_TO_WAIT_FOR_CRAWLER);
                } catch (InterruptedException e) {
                    logger.error("Interrupted S3 upload, adding all remaining files to failed collection");
                    queue.forEach(p -> addFailedFile(p, e.getMessage()));
                }
            } else {
                if (Files.exists(path)) {
                    String key = config.getSharedHome().relativize(path).toString();
                    if (path.toFile().length() > MAXIMUM_FILE_SIZE_TO_UPLOAD) {
                        logger.debug("File {} is larger than {}, running multipart upload", path, FileUtils.byteCountToDisplaySize(MAXIMUM_FILE_SIZE_TO_UPLOAD));
                        final S3MultiPartUploader multiPartUploader = new S3MultiPartUploader(config);
                        try {
                            multiPartUploader.multiPartUpload(path.toFile(), key);
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error("Error when running multi-part upload for file {} with exception {}", path, e.getMessage());
                        }
                    }
                    final PutObjectRequest putRequest = PutObjectRequest.builder()
                            .bucket(config.getBucketName())
                            .key(key)
                            .build();
                    final CompletableFuture<PutObjectResponse> response = config.getS3AsyncClient().putObject(putRequest, path);
                    progress.reportFileUploadCommenced();
                    final S3UploadOperation uploadOperation = new S3UploadOperation(path, response);

                    responsesQueue.add(uploadOperation);
                } else {
                    addFailedFile(path, String.format("File doesn't exist: %s", path));
                }
            }
        }
        responsesQueue.forEach(this::handlePutObjectResponse);
        logger.info("Finished uploading files to S3");
    }

    private void handlePutObjectResponse(S3UploadOperation operation) {
        try {
            final PutObjectResponse evaluatedResponse = operation.response.get();
            if (!evaluatedResponse.sdkHttpResponse().isSuccessful()) {
                final String errorMessage = String.format(
                        "Error when uploading %s to S3, %s",
                        operation.path,
                        evaluatedResponse.sdkHttpResponse().statusText());
                addFailedFile(operation.path, errorMessage);
            } else {
                progress.reportFileMigrated();
            }
        } catch (InterruptedException | ExecutionException e) {
            addFailedFile(operation.path, e.getMessage());
        }
    }

    private void addFailedFile(Path path, String reason) {
        report.reportFileNotMigrated(new FailedFileMigration(path, reason));
        logger.error("File {} wasn't uploaded. Reason: {}", path, reason);
    }

    private static class S3UploadOperation {
        Path path;
        CompletableFuture<PutObjectResponse> response;

        S3UploadOperation(Path path, CompletableFuture<PutObjectResponse> response) {
            this.path = path;
            this.response = response;
        }
    }
}
