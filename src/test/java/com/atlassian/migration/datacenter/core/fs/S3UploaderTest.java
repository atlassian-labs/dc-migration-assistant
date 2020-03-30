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

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import com.atlassian.migration.datacenter.core.util.UploadQueue;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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


    private UploadQueue<Path> queue;
    private S3Uploader uploader;
    private FileSystemMigrationReport report;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        S3UploadConfig config = new S3UploadConfig("bucket-name", s3AsyncClient, tempDir);
        queue = new UploadQueue<>(20);
        report = new DefaultFileSystemMigrationReport();
        uploader = new S3Uploader(config, report);
    }

    @Test
    void uploadShouldConsumePathsWhileCrawlingIsRunning() throws IOException, InterruptedException, ExecutionException {
        PutObjectResponse putObjectResponse = (PutObjectResponse) PutObjectResponse.builder().sdkHttpResponse(sdkHttpResponse).build();
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(s3response.get()).thenReturn(putObjectResponse);
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(Path.class))).thenReturn(s3response);

        addFileToQueue("file1");

        final Future<?> submit = Executors.newFixedThreadPool(1).submit(() -> {
            try {
                uploader.upload(queue);
            } catch (FilesystemUploader.FileUploadException e) {
                throw new RuntimeException(e);
            }
        });

        // verify consumption of the first path
        Thread.sleep(1000);
        assertFalse(submit.isDone());
        assertTrue(queue.isEmpty());

        // add new file when the crawler isn't finish
        addFileToQueue("file2");

        // finish crawling
        queue.finish();
        submit.get();

        // upload should finish and there shouldn't be more paths to process
        assertTrue(submit.isDone());
        assertTrue(queue.isEmpty());
        assertTrue(report.getFailedFiles().isEmpty());
    }

    @Test
    void uploadShouldReportFileAsMigrated() throws IOException, ExecutionException, InterruptedException {
        PutObjectResponse putObjectResponse = (PutObjectResponse) PutObjectResponse.builder().sdkHttpResponse(sdkHttpResponse).build();
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(s3response.get()).thenReturn(putObjectResponse);
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(Path.class))).thenReturn(s3response);

        Path testPath = addFileToQueue("file1");
        queue.finish();

        final Future<?> submit = Executors.newFixedThreadPool(1).submit(() -> {
            try {
                uploader.upload(queue);
            } catch (FilesystemUploader.FileUploadException e) {
                throw new RuntimeException(e);
            }
        });

        submit.get();
        assertEquals(1, report.getCountOfMigratedFiles());
    }

    @Test
    void uploadNonExistentDirectoryShouldReturnFailedCollection() throws InterruptedException, FilesystemUploader.FileUploadException {
        final Path nonExistentFile = tempDir.resolve("non-existent");
        queue.put(nonExistentFile);
        queue.finish();

        uploader.upload(queue);

        assertEquals(report.getFailedFiles().size(), 1);
    }

    @Test
    void shouldReportFileAsInFlightWhenUploadStarts() throws Exception {
        PutObjectResponse putObjectResponse = (PutObjectResponse) PutObjectResponse.builder().sdkHttpResponse(sdkHttpResponse).build();
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(s3response.get()).thenReturn(putObjectResponse);
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(Path.class))).thenReturn(s3response);

        addFileToQueue("file1");

        final Future<?> submit = Executors.newFixedThreadPool(1).submit(() -> {
            try {
                uploader.upload(queue);
            } catch (FilesystemUploader.FileUploadException e) {
                throw new RuntimeException(e);
            }
        });

        Thread.sleep(100);

        assertEquals(1, report.getNumberOfCommencedFileUploads());

        queue.finish();

        submit.get();
    }

    Path addFileToQueue(String fileName) throws IOException, InterruptedException {
        final Path file = tempDir.resolve(fileName);
        Files.write(file, "".getBytes());
        queue.put(file);
        return file;
    }

}
