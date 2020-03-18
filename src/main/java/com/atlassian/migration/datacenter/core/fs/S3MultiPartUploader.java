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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Uploads file to S3 in multiple parts.
 * <p>
 * 1. Initialise the upload
 * 2. Split the file into same sized parts (except the last one) and upload them to S3
 * 3. Confirm the upload has finished with all the required parts
 * <p>
 * All files larger than 5GB (hard AWS limit) are required to be uploaded via this method.
 * <p>
 * https://docs.aws.amazon.com/AmazonS3/latest/dev/mpuoverview.html
 */
public class S3MultiPartUploader {
    private final static Logger logger = LoggerFactory.getLogger(S3MultiPartUploader.class);
    private final S3UploadConfig config;
    private final File file;
    private final String key;

    private int sizeToUpload = 100 * 1024 * 1024; // 100 MB
    private List<CompletedPart> completedParts = new ArrayList<>();
    private ByteBuffer buffer;
    private int uploadPartNumber = 1;

    public S3MultiPartUploader(S3UploadConfig config, File file, String key) {
        this.config = config;
        this.file = file;
        this.key = key;
    }

    public void upload() throws ExecutionException, InterruptedException {
        // lazily loaded to save memory
        buffer = ByteBuffer.allocate(getSizeToUpload());

        String uploadId = initiateUpload();

        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            int readBytes;
            while ((readBytes = bufferedInputStream.read(buffer.array())) > 0) {
                logger.trace("Read {} bytes from file {}", readBytes, file);

                String etag = uploadChunk(uploadId, uploadPartNumber, readBytes);
                logger.debug("Uploaded part {} with etag {}", uploadPartNumber, etag);

                uploadPartNumber = completePart(uploadPartNumber, etag);
            }
        } catch (IOException e) {
            logger.error("Cannot open file for the multi-part upload", e);
        }
        buffer.clear();

        logger.trace("Finished uploading parts, sending complete request.");
        try {
            completeUpload(key, uploadId).get();
            logger.debug("Finished multipart upload for {} with {} parts", key, completedParts.size());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Encountered error when uploading multipart file.", e);
        }
    }

    /**
     * Size of each chunk into which the file is split into
     *
     * @return size of the chunk
     */
    public int getSizeToUpload() {
        return sizeToUpload;
    }

    /**
     * Changes the default chunk size for multipart upload
     *
     * @param sizeToUpload chunk size
     */
    public void setSizeToUpload(int sizeToUpload) {
        this.sizeToUpload = sizeToUpload;
    }

    private String initiateUpload() throws InterruptedException, ExecutionException {
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .build();
        CreateMultipartUploadResponse response = config.getS3AsyncClient().createMultipartUpload(createMultipartUploadRequest).get();
        return response.uploadId();
    }

    private String uploadChunk(String uploadId, int uploadPartNumber, int readBytes) throws InterruptedException, ExecutionException {
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .uploadId(uploadId)
                .partNumber(uploadPartNumber)
                .build();

        AsyncRequestBody body;
        if (readBytes < buffer.limit()) {
            // We need to limit the buffer if the rest of the file is smaller than the allocated size.
            // If don't do this, the size of the sent part will be always equal to the buffer size.
            body = AsyncRequestBody.fromByteBuffer((ByteBuffer) buffer.limit(readBytes));
        } else {
            body = AsyncRequestBody.fromByteBuffer(buffer);
        }
        return config.getS3AsyncClient()
                .uploadPart(uploadPartRequest, body)
                .get()
                .eTag();
    }

    private int completePart(int uploadPartNumber, String etag) {
        CompletedPart part = CompletedPart.builder()
                .partNumber(uploadPartNumber)
                .eTag(etag)
                .build();
        completedParts.add(part);

        return uploadPartNumber + 1;
    }

    private CompletableFuture<CompleteMultipartUploadResponse> completeUpload(String key, String uploadId) {
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                        .bucket(config.getBucketName())
                        .key(key)
                        .uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload)
                        .build();
        return config.getS3AsyncClient().completeMultipartUpload(completeMultipartUploadRequest);
    }
}
