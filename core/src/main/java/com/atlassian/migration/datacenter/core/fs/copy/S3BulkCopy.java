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

import com.atlassian.migration.datacenter.core.fs.FileSystemMigrationReportManager;
import com.atlassian.migration.datacenter.core.fs.FileUploadException;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploaderFactory;
import com.atlassian.migration.datacenter.core.fs.ReportType;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.UPLOADING;

public class S3BulkCopy {

    private static final Logger logger = LoggerFactory.getLogger(S3BulkCopy.class);
    private static final String OVERRIDE_UPLOAD_DIRECTORY = System
            .getProperty("com.atlassian.migration.datacenter.fs.overrideJiraHome", "");

    private final Path home;
    private final FileSystemMigrationReportManager reportManager;
    private final FilesystemUploaderFactory filesystemUploaderFactory;

    private FilesystemUploader fsUploader;

    public S3BulkCopy(
        Path home,
        FilesystemUploaderFactory filesystemUploaderFactory,
        FileSystemMigrationReportManager reportManager)
    {
        this.home = home;
        this.reportManager = reportManager;
        this.filesystemUploaderFactory = filesystemUploaderFactory;
    }

    public void copySharedHomeToS3() throws FileUploadException
    {
        FileSystemMigrationReport report = reportManager.getCurrentReport(ReportType.Filesystem);

        if (report == null) {
            throw new FileUploadException("No files system migration report bound to bulk copy operation");
        }
        report.setStatus(UPLOADING);

        fsUploader = filesystemUploaderFactory.newUploader(report);

        logger.info("Commencing upload of shared home");

        fsUploader.uploadDirectory(getSharedHomeDir());

        logger.info("Upload of shared home complete.");
    }

    public void abortCopy() {
        if (fsUploader == null) {
            return;
        }

        logger.warn("Aborting running filesystem migration");
        fsUploader.abort();
    }


    private Path getSharedHomeDir() {
        if (!OVERRIDE_UPLOAD_DIRECTORY.equals("")) {
            return Paths.get(OVERRIDE_UPLOAD_DIRECTORY);
        }
        return home;
    }
}
