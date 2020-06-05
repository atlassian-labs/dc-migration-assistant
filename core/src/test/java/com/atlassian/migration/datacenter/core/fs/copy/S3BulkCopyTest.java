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

package com.atlassian.migration.datacenter.core.fs.copy;

import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3BulkCopyTest {

    @Mock
    Supplier<S3AsyncClient> mockSupplier;

    @Mock
    AWSMigrationHelperDeploymentService helperDeploymentService;


    @Test
    void shouldThrowWhenSharedHomeDirectoryIsInvalid() {
        Path fakeHome = givenSharedHomeDoesNotExist();
        S3BulkCopy sut = new S3BulkCopy(mockSupplier, helperDeploymentService, fakeHome);
        sut.bindMigrationReport(new DefaultFileSystemMigrationReport());
        givenHelperDeploymentBucketExists();

        try {
            sut.copySharedHomeToS3();
            fail();
        } catch (FilesystemUploader.FileUploadException e) {
            assertEquals("Failed to migrate content. File not found: " + fakeHome.toString(), e.getMessage());
        }
    }

    @Test
    void shouldAbortRunningMigration() throws NoSuchFieldException {
        Path fakeHome = givenSharedHomeDoesNotExist();
        S3BulkCopy sut = new S3BulkCopy(mockSupplier, helperDeploymentService, fakeHome);
        final FilesystemUploader uploader = mock(FilesystemUploader.class);
        FieldSetter.setField(sut, sut.getClass().getDeclaredField("fsUploader"), uploader);

        sut.abortCopy();

        verify(uploader).abort();
    }

    private Path givenSharedHomeDoesNotExist() {
        Path nonexistentDir = Paths.get(UUID.randomUUID().toString());
        return nonexistentDir;
    }

    private void givenHelperDeploymentBucketExists() {
        when(helperDeploymentService.getMigrationS3BucketName()).thenReturn("my-bucket");
    }
}