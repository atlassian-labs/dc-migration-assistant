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

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.fs.Crawler;
import com.atlassian.migration.datacenter.core.fs.DirectoryStreamCrawler;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig;
import com.atlassian.migration.datacenter.core.fs.S3Uploader;
import com.atlassian.migration.datacenter.core.fs.Uploader;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.UPLOADING;

public class S3BulkCopy {

    private static final Logger logger = LoggerFactory.getLogger(S3BulkCopy.class);
    private static final String OVERRIDE_UPLOAD_DIRECTORY = System
            .getProperty("com.atlassian.migration.datacenter.fs.overrideJiraHome", "");

    private FileSystemMigrationReport report;
    private final Supplier<S3AsyncClient> clientSupplier;
    private final AWSMigrationHelperDeploymentService migrationHelperDeploymentService;
    private final JiraHome jiraHome;
    private FilesystemUploader fsUploader;

    public S3BulkCopy(
            Supplier<S3AsyncClient> clientSupplier,
            AWSMigrationHelperDeploymentService migrationHelperDeploymentService,
            JiraHome jiraHome) {
        this.clientSupplier = clientSupplier;
        this.migrationHelperDeploymentService = migrationHelperDeploymentService;
        this.jiraHome = jiraHome;
    }

    public void copySharedHomeToS3() throws FilesystemUploader.FileUploadException {
        if (report == null) {
            throw new FilesystemUploader.FileUploadException("No files system migration report bound to bulk copy operation");
        }
        logger.trace("Beginning FS upload. Uploading shared home dir {} to S3 bucket {}", getSharedHomeDir(),
                getS3Bucket());
        report.setStatus(UPLOADING);

        Crawler homeCrawler = new DirectoryStreamCrawler(report);

        S3UploadConfig s3UploadConfig = new S3UploadConfig(getS3Bucket(), clientSupplier.get(), getSharedHomeDir());
        Uploader s3Uploader = new S3Uploader(s3UploadConfig, report);

        fsUploader = new FilesystemUploader(homeCrawler, s3Uploader);

        logger.info("commencing upload of shared home");

        fsUploader.uploadDirectory(getSharedHomeDir());

        logger.info("upload of shared home complete.");
    }

    public void abortCopy() {
        if (fsUploader == null) {
            return;
        }

        logger.warn("Aborting running filesystem migration");
        fsUploader.abort();
    }

    public void bindMigrationReport(FileSystemMigrationReport report) {
        this.report = report;
    }

    private String getS3Bucket() {
        return migrationHelperDeploymentService.getMigrationS3BucketName();
    }

    private Path getSharedHomeDir() {
        if (!OVERRIDE_UPLOAD_DIRECTORY.equals("")) {
            return Paths.get(OVERRIDE_UPLOAD_DIRECTORY);
        }
        return jiraHome.getHome().toPath();
    }
}
