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

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import com.atlassian.migration.datacenter.core.fs.Crawler;
import com.atlassian.migration.datacenter.core.fs.DirectoryStreamCrawler;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig;
import com.atlassian.migration.datacenter.core.fs.S3Uploader;
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.util.concurrent.Supplier;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseMigrationService {
    private final ApplicationConfiguration applicationConfiguration;
    private final Path tempDirectory;
    private S3AsyncClient s3AsyncClient;
    private Supplier<S3AsyncClient> s3AsyncClientSupplier;

    private Process extractorProcess;
    private AtomicReference<MigrationStatus> status = new AtomicReference<>();


    public DatabaseMigrationService(ApplicationConfiguration applicationConfiguration,
                                    Path tempDirectory,
                                    Supplier<S3AsyncClient> s3AsyncClientSupplier) {
        this.applicationConfiguration = applicationConfiguration;
        this.tempDirectory = tempDirectory;
        this.s3AsyncClientSupplier = s3AsyncClientSupplier;
        this.setStatus(MigrationStatus.NOT_STARTED);
    }

    @PostConstruct
    public void postConstruct() {
        this.s3AsyncClient = this.s3AsyncClientSupplier.get();
    }

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob. The status of the migration can be queried via getStatus().
     */
    public FileSystemMigrationErrorReport performMigration() throws DatabaseMigrationFailure {
        DatabaseExtractor extractor = DatabaseExtractorFactory.getExtractor(applicationConfiguration);
        Path target = tempDirectory.resolve("db.dump");

        extractorProcess = extractor.startDatabaseDump(target);
        setStatus(MigrationStatus.DUMP_IN_PROGRESS);
        try {
            extractorProcess.waitFor();
        } catch (Exception e) {
            String msg = "Error while waiting for DB extractor to finish";
            setStatus(MigrationStatus.error(msg, e));
            throw new DatabaseMigrationFailure(msg, e);
        }
        setStatus(MigrationStatus.DUMP_COMPLETE);


        FileSystemMigrationReport report = new DefaultFileSystemMigrationReport();

        String bucket = System.getProperty("S3_TARGET_BUCKET_NAME", "trebuchet-testing");
        S3UploadConfig config = new S3UploadConfig(bucket, this.s3AsyncClient, target.getParent());


        S3Uploader uploader = new S3Uploader(config, report);
        Crawler crawler = new DirectoryStreamCrawler(report);


        FilesystemUploader filesystemUploader = new FilesystemUploader(crawler, uploader);
        setStatus(MigrationStatus.UPLOAD_IN_PROGRESS);

        filesystemUploader.uploadDirectory(target);

        setStatus(MigrationStatus.UPLOAD_COMPLETE);

        setStatus(MigrationStatus.FINISHED);

        return report;
    }

    private void setStatus(MigrationStatus status) {
        this.status.set(status);
    }

    public MigrationStatus getStatus() {
        return status.get();
    }
}
