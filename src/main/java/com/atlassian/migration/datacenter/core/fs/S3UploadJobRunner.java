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
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * Scheduled job starting directory upload to S3 bucket. If there is already job running, it will abort execution.
 * The job expects 2 parameters being passed when the job is scheduled
 * <ul>
 * <li>s3bucket: target S3 bucket we are uploading the files into</li>
 * <li>directory: directory to upload</li>
 * </ul>
 */
public class S3UploadJobRunner implements JobRunner {
    public static String KEY = "com.atlassian.migration.datacenter.fs.S3UploadJobRunner";
    private static Logger log = LoggerFactory.getLogger(S3UploadJobRunner.class);
    private final FilesystemMigrationService fsMigrationService;

    public S3UploadJobRunner(FilesystemMigrationService fsMigrationService) {
        this.fsMigrationService = fsMigrationService;
    }

    @Nullable
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        if (fsMigrationService.isRunning()) {
            return JobRunnerResponse.aborted("S3 upload job is still running");
        }

        log.info("Starting S3 migration job");
        try {
            fsMigrationService.startMigration();
        } catch (InvalidMigrationStageError e) {
            log.error("Invalid migration transition - {}", e.getMessage());
            return JobRunnerResponse.failed(e);
        }

        final FileSystemMigrationReport report = fsMigrationService.getReport();
        log.info("Finished S3 migration job: {}", report);

        return JobRunnerResponse.success("S3 upload completed.");
    }

}
