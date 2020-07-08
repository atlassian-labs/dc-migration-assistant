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

import com.atlassian.migration.datacenter.core.util.UploadQueue;
import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class DirectoryStreamCrawler implements Crawler {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryStreamCrawler.class);

    private static final String ignorePattern = "^(dbconfig\\.xml|cluster\\.properties|log|caches|import|export|plugins/.bundled_plugins|plugins/.osgi-plugins|keyFile|saltFile)";
    private static final Pattern defaultIgnoreList = Pattern.compile(ignorePattern);

    private FileSystemMigrationReport report;

    public DirectoryStreamCrawler(FileSystemMigrationReport report) {
        this.report = report;
    }

    @Override
    public void crawlDirectory(Path start, UploadQueue<Path> queue) throws IOException {
        try {
            final DirectoryStream<Path> paths;
            paths = Files.newDirectoryStream(start);
            listDirectories(start, queue, paths);
        } catch (NoSuchFileException e) {
            logger.error("Failed to find path " + start, e);
            report.reportFileNotMigrated(new FailedFileMigration(start, e.getMessage()));
            report.setStatus(FilesystemMigrationStatus.FAILED);
            throw e;

        } finally {
            try {
                logger.info("Crawled and added {} files for upload.", report.getNumberOfFilesFound());
                report.reportCrawlingFinished();
                queue.finish();
            } catch (InterruptedException e) {
                logger.error("Failed to finalise upload queue.", e);
            }
        }
    }

    private void listDirectories(Path base, UploadQueue<Path> queue, DirectoryStream<Path> paths) {
        int off = base.getNameCount();
        paths.forEach(p -> {
            // NOTE: This should be possible with the directoryStream regex glob,
            // but in practice doesn't play well with nested directory patterns.
            String subpath = p.subpath(off, p.getNameCount()).toString();
            if (defaultIgnoreList.matcher(subpath).matches())
                return;

            if (Files.isDirectory(p)) {
                logger.trace("Found directory while crawling home: {}", p);
                try (final DirectoryStream<Path> newPaths = Files.newDirectoryStream(p.toAbsolutePath())) {
                    listDirectories(base, queue, newPaths);
                } catch (Exception e) {
                    logger.error("Error when traversing directory {}, with exception {}", p, e);
                    report.reportFileNotMigrated(new FailedFileMigration(p, e.getMessage()));
                }
            } else {
                try {
                    logger.trace("queueing file: {}", p);
                    queue.put(p);
                } catch (InterruptedException e) {
                    logger.error("Error when queuing {}, with exception {}", p, e);
                    report.reportFileNotMigrated(new FailedFileMigration(p, e.getMessage()));
                }
                report.reportFileFound();
            }
        });
    }
}
