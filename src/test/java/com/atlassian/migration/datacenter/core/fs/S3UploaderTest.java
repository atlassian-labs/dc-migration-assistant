package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFilesystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3UploaderTest {

    @Mock
    private S3AsyncClient s3AsyncClient;

    @Mock
    private CompletableFuture<PutObjectResponse> s3response;

    @Mock
    private SdkHttpResponse sdkHttpResponse;


    private ConcurrentLinkedQueue<Path> queue = new ConcurrentLinkedQueue<>();
    private S3Uploader uploader;
    private AtomicBoolean isCrawlDone;
    private FileSystemMigrationErrorReport errorReport;
    private FileSystemMigrationProgress progress;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        S3UploadConfig config = new S3UploadConfig("bucket-name", s3AsyncClient, tempDir);
        queue = new ConcurrentLinkedQueue<>();
        progress = new DefaultFilesystemMigrationProgress();
        errorReport = new DefaultFileSystemMigrationErrorReport();
        uploader = new S3Uploader(config, errorReport, progress);
        isCrawlDone = new AtomicBoolean(false);
    }

    @Test
    void uploadShouldConsumePathsWhileCrawlingIsRunning() throws IOException, InterruptedException, ExecutionException {
        PutObjectResponse putObjectResponse = (PutObjectResponse) PutObjectResponse.builder().sdkHttpResponse(sdkHttpResponse).build();
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(s3response.get()).thenReturn(putObjectResponse);
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(Path.class))).thenReturn(s3response);

        addFileToQueue("file1");

        final Future<?> submit = Executors.newFixedThreadPool(1).submit(() -> {
            uploader.upload(queue, isCrawlDone);
        });

        // verify consumption of the first path
        Thread.sleep(1000);
        assertFalse(submit.isDone());
        assertTrue(queue.isEmpty());

        // add new file when the crawler isn't finish
        addFileToQueue("file2");

        // finish crawling
        isCrawlDone.set(true);
        submit.get();

        // upload should finish and there shouldn't be more paths to process
        assertTrue(submit.isDone());
        assertTrue(queue.isEmpty());
        assertTrue(errorReport.getFailedFiles().isEmpty());
    }

    @Test
    void uploadShouldReportFileAsMigrated() throws IOException, ExecutionException, InterruptedException {
        PutObjectResponse putObjectResponse = (PutObjectResponse) PutObjectResponse.builder().sdkHttpResponse(sdkHttpResponse).build();
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(s3response.get()).thenReturn(putObjectResponse);
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(Path.class))).thenReturn(s3response);

        Path testPath = addFileToQueue("file1");
        isCrawlDone.set(true);

        final Future<?> submit = Executors.newFixedThreadPool(1).submit(() -> {
            uploader.upload(queue, isCrawlDone);
        });

        submit.get();
        assertEquals(1, progress.getCountOfMigratedFiles());
    }

    @Test
    void uploadNonExistentDirectoryShouldReturnFailedCollection() {
        final Path nonExistentFile = tempDir.resolve("non-existent");
        queue.add(nonExistentFile);
        isCrawlDone.set(true);

        uploader.upload(queue, isCrawlDone);

        assertEquals(errorReport.getFailedFiles().size(), 1);
    }

    @Test
    void shouldReportFileAsInFlightWhenUploadStarts() throws Exception {
        PutObjectResponse putObjectResponse = (PutObjectResponse) PutObjectResponse.builder().sdkHttpResponse(sdkHttpResponse).build();
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(s3response.get()).thenReturn(putObjectResponse);
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(Path.class))).thenReturn(s3response);

        addFileToQueue("file1");

        final Future<?> submit = Executors.newFixedThreadPool(1).submit(() -> {
            uploader.upload(queue, isCrawlDone);
        });

        Thread.sleep(100);

        assertEquals(1, progress.getNumberOfCommencedFileUploads());

        isCrawlDone.set(true);

        submit.get();
    }

    Path addFileToQueue(String fileName) throws IOException {
        final Path file = tempDir.resolve(fileName);
        Files.write(file, "".getBytes());
        queue.add(file);
        return file;
    }
}
