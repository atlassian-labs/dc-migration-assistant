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

import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Tag("integration")
@ExtendWith({LocalstackDockerExtension.class, MockitoExtension.class})
@LocalstackDockerProperties(services = {"s3"}, imageTag = "0.10.8")
class S3MultiPartUploaderIT {
    private static final String LOCALSTACK_S3_ENDPOINT = "http://localhost:4572";
    private static final String TREBUCHET_LOCALSTACK_BUCKET = "trebuchet-localstack-bucket";
    @TempDir
    Path tempDir;
    @Mock
    private AwsCredentialsProvider mockCredentialsProvider;
    private S3UploadConfig config;

    @BeforeEach
    void setup() {
        S3AsyncClient localStackS3Client = S3AsyncClient.builder()
                .credentialsProvider(mockCredentialsProvider)
                .endpointOverride(URI.create(LOCALSTACK_S3_ENDPOINT))
                .region(Region.US_EAST_1)
                .build();
        config = new S3UploadConfig(TREBUCHET_LOCALSTACK_BUCKET, localStackS3Client, tempDir);

        when(mockCredentialsProvider.resolveCredentials()).thenReturn(new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return "fake-access-key";
            }

            @Override
            public String secretAccessKey() {
                return "fake-secret-key";
            }
        });
    }

    @Test
    void multiuploadShouldUploadFileToS3() throws Exception {
        final String filename = "file_to_upload.txt";
        // we use subfolder to verify we are writing the correct key
        final Path file = createFile("subfolder", filename);
        final String key = tempDir.relativize(file).toString();
        Files.write(file, "123456789".getBytes());

        S3MultiPartUploader uploader = new S3MultiPartUploader(config, file.toFile(), key);

        AmazonS3 s3Client = TestUtils.getClientS3();
        s3Client.createBucket(TREBUCHET_LOCALSTACK_BUCKET);

        uploader.upload();

        final List<S3ObjectSummary> objectSummaries = s3Client.listObjects(TREBUCHET_LOCALSTACK_BUCKET).getObjectSummaries();
        assertEquals(1, objectSummaries.size(), "Bucket should contain only one file");
        assertEquals(key, objectSummaries.get(0).getKey(), "Object key is different to the filename");
    }

    @Test
    void uploadFileInMultipleParts() throws Exception {
        final String filename = "file_to_upload.txt";
        // we use subfolder to verify we are writing the correct key
        final Path file = createFile("subfolder", filename);
        final String key = tempDir.relativize(file).toString();
        Files.write(file, "123456789".getBytes());

        S3MultiPartUploader uploader = new S3MultiPartUploader(config, file.toFile(), key);
        uploader.setSizeToUpload(1);

        AmazonS3 s3Client = TestUtils.getClientS3();
        s3Client.createBucket(TREBUCHET_LOCALSTACK_BUCKET);

        try {
            uploader.upload();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof S3Exception);
            e.getCause().getMessage().contains("Your proposed upload is smaller than the minimum allowed object size");
        }
    }

    Path createFile(String subfolder, String fileName) throws IOException {
        Files.createDirectory(tempDir.resolve(subfolder));
        final Path file = tempDir.resolve(subfolder).resolve(fileName);
        Files.write(file, "".getBytes());
        return file;
    }
}
