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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assume.assumeThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3MultiPartUploaderTest {
    @TempDir
    Path tempDir;

    @Mock
    S3AsyncClient client;
    @Captor
    ArgumentCaptor<AsyncRequestBody> valueCaptor;
    private String content = "123";

    @Test
    void shouldExecuteInitiationAndUploadAndCompletion() throws Exception {
        File file = createFile();

        final String bucket = "bucket";
        final String key = "key";
        S3UploadConfig config = new S3UploadConfig(bucket, client, tempDir);
        final S3MultiPartUploader uploader = new S3MultiPartUploader(config, file, key);

        final CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder().key(key).bucket(bucket).build();
        when(client.createMultipartUpload(createRequest))
                .thenReturn(CompletableFuture.completedFuture(CreateMultipartUploadResponse.builder().build()));

        when(client.uploadPart(any(UploadPartRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.completedFuture(UploadPartResponse.builder().build()));

        final CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest
                .builder()
                .bucket(bucket)
                .multipartUpload(
                        CompletedMultipartUpload.builder().parts(CompletedPart.builder().partNumber(1).build()).build())
                .key(key)
                .build();
        when(client.completeMultipartUpload(completeRequest))
                .thenReturn(CompletableFuture.completedFuture(CompleteMultipartUploadResponse.builder().build()));

        uploader.upload();

        verify(client).createMultipartUpload(createRequest);
        verify(client).uploadPart(any(UploadPartRequest.class), any(AsyncRequestBody.class));
        verify(client).completeMultipartUpload(completeRequest);
    }

    @Test
    void lastChunkShouldHaveExactRemainingSize() throws Exception {
        File file = createFile();

        S3UploadConfig config = new S3UploadConfig("bucket", client, tempDir);
        final S3MultiPartUploader uploader = new S3MultiPartUploader(config, file, "file");
        int chunkSize = 2;
        uploader.setSizeToUpload(chunkSize); // use 2 bytes chunks
        assumeThat(content.length() % chunkSize, Matchers.greaterThan(0));

        when(client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(CreateMultipartUploadResponse.builder().build()));

        when(client.uploadPart(any(UploadPartRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.completedFuture(UploadPartResponse.builder().build()));

        when(client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(CompleteMultipartUploadResponse.builder().build()));

        uploader.upload();

        verify(client, times(2)).uploadPart(any(UploadPartRequest.class), valueCaptor.capture());

        final List<AsyncRequestBody> allValues = valueCaptor.getAllValues();
        assertEquals(2, allValues.get(0).contentLength().get());
        assertEquals(1, allValues.get(1).contentLength().get());
    }

    private File createFile() throws Exception {
        final String filename = "file_to_upload.txt";
        final Path file = tempDir.resolve(filename);
        Files.write(file, content.getBytes());

        return file.toFile();
    }

}