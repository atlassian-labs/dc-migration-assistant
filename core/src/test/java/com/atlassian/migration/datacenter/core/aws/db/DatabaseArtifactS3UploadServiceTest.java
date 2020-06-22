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

package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.fs.FileSystemMigrationReportManager;
import com.atlassian.migration.datacenter.core.fs.ReportType;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;
import java.util.function.Supplier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatabaseArtifactS3UploadServiceTest {

    @TempDir
    Path path;

    @Mock
    Supplier<S3AsyncClient> s3AsyncClientSupplier;

    @Mock
    DatabaseUploadStageTransitionCallback databaseUploadStageTransitionCallback;

    @Mock
    FileSystemMigrationReportManager reportManager;

    @InjectMocks
    DatabaseArtifactS3UploadService sut;

    @BeforeEach
    void setup() {
        when(reportManager.resetReport(ReportType.Database)).thenReturn(new DefaultFileSystemMigrationReport());
    }

    @Test
    void serviceShouldInstantiateS3ClientFromSupplier() throws Exception {
        sut.upload(path, "bucketName");
        verify(s3AsyncClientSupplier).get();
    }

    @Test
    void serviceShouldTransitionThroughExpectedStages() throws Exception {
        sut.upload(path, "bucketName");
        verify(databaseUploadStageTransitionCallback).assertInStartingStage();
        verify(databaseUploadStageTransitionCallback).transitionToServiceWaitStage();
        verify(databaseUploadStageTransitionCallback).transitionToServiceNextStage();
    }
}
