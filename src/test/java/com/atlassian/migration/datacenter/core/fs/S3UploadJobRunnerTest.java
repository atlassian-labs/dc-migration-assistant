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

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.status.RunOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3UploadJobRunnerTest {

    @Mock
    FilesystemMigrationService filesystemMigrationService;

    @Mock
    JobRunnerRequest jobRunnerRequest;

    @InjectMocks
    S3UploadJobRunner s3UploadJobRunner;

    @Test
    void shouldNotRunJobWhenJobIsAlreadyRunning() {
        when(filesystemMigrationService.isRunning()).thenReturn(true);
        JobRunnerResponse jobRunnerResponse = s3UploadJobRunner.runJob(jobRunnerRequest);
        Assertions.assertEquals(RunOutcome.ABORTED, jobRunnerResponse.getRunOutcome());
    }


    @Test
    void shouldNotRunJobWhenMigrationStageIsInvalid() throws Exception {
        when(filesystemMigrationService.isRunning()).thenReturn(false);
        Mockito.doThrow(InvalidMigrationStageError.class).when(filesystemMigrationService).startMigration();

        JobRunnerResponse jobRunnerResponse = s3UploadJobRunner.runJob(jobRunnerRequest);
        Assertions.assertEquals(RunOutcome.FAILED, jobRunnerResponse.getRunOutcome());
    }

    @Test
    void shouldCompleteJobRunSuccessfully() throws Exception {
        DefaultFileSystemMigrationReport report = new DefaultFileSystemMigrationReport(null, null);
        report.setStatus(FilesystemMigrationStatus.DONE);

        when(filesystemMigrationService.isRunning()).thenReturn(false);

        when(filesystemMigrationService.getReport()).thenReturn(report);
        Mockito.doNothing().when(filesystemMigrationService).startMigration();

        JobRunnerResponse jobRunnerResponse = s3UploadJobRunner.runJob(jobRunnerRequest);
        Assertions.assertEquals(RunOutcome.SUCCESS, jobRunnerResponse.getRunOutcome());
    }
}