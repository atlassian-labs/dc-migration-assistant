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

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport;
import com.atlassian.migration.datacenter.core.util.UploadQueue;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryStreamCrawlerTest {
    @TempDir
    Path tempDir;

    private Crawler directoryStreamCrawler;
    private UploadQueue<Path> queue;
    private Set<Path> expectedPaths;
    private FileSystemMigrationReport report;

    @BeforeEach
    void createFiles() throws Exception {
        queue = new UploadQueue<>(10);
        expectedPaths = new HashSet<>();
        report = new DefaultFileSystemMigrationReport();
        directoryStreamCrawler = new DirectoryStreamCrawler(report);

        expectedPaths.add(Files.write(tempDir.resolve("newfile.txt"), "newfile content".getBytes()));
        final Path subdirectory = Files.createDirectory(tempDir.resolve("subdirectory"));
        expectedPaths.add(Files.write(subdirectory.resolve("subfile.txt"), "subfile content in the subdirectory".getBytes()));
    }

    @Test
    void shouldListAllSubdirectories() throws Exception {
        directoryStreamCrawler = new DirectoryStreamCrawler(report);
        directoryStreamCrawler.crawlDirectory(tempDir, queue);

        expectedPaths.forEach(path -> assertTrue(queue.contains(path), String.format("Expected %s is absent from crawler queue", path)));
    }

    @Test
    void incorrectStartDirectoryShouldReport() {
        assertThrows(IOException.class, () -> directoryStreamCrawler.crawlDirectory(Paths.get("nonexistent-directory-2010"), queue));
    }

    @Test
    void shouldReportFileAsFoundWhenCrawled() throws Exception {
        directoryStreamCrawler = new DirectoryStreamCrawler(report);
        directoryStreamCrawler.crawlDirectory(tempDir, queue);

        assertEquals(expectedPaths.size(), report.getNumberOfFilesFound());
    }

    @Test
    @Disabled("Simulating AccessDenied permission proved complicated in an unit test")
    void inaccessibleSubdirectoryIsReportedAsFailed() throws IOException {
        final Path directory = Files.createDirectory(tempDir.resolve("non-readable-subdir"),
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("-wx-wx-wx")));

        directoryStreamCrawler.crawlDirectory(tempDir, queue);

        assertEquals(report.getFailedFiles().size(), 1);
    }
}
