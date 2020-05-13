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

@ExtendWith(MockitoExtension.class)
public class DatabaseArtifactS3UploadServiceTest {

    @TempDir
    Path path;

    @Mock
    Supplier<S3AsyncClient> s3AsyncClientSupplier;

    @Mock
    DatabaseUploadStageTransitionCallback databaseUploadStageTransitionCallback;

    @InjectMocks
    DatabaseArtifactS3UploadService sut;

    @Test
    void serviceShouldInstantiateS3ClientFromSupplier() throws Exception {
        sut.upload(path, "bucketName", databaseUploadStageTransitionCallback);
        verify(s3AsyncClientSupplier).get();
    }

    @Test
    void serviceShouldTransitionThroughExpectedStages() throws Exception {
        sut.upload(path, "bucketName", databaseUploadStageTransitionCallback);
        verify(databaseUploadStageTransitionCallback).assertInStartingStage();
        verify(databaseUploadStageTransitionCallback).transitionToServiceWaitStage();
        verify(databaseUploadStageTransitionCallback).transitionToServiceNextStage();
    }
}
