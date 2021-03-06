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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectoryStreamCrawlerTest {
    @TempDir
    Path tempDir;

    private Crawler directoryStreamCrawler;
    private UploadQueue<Path> queue;
    private Set<Path> expectedPaths;
    private Set<Path> ignoredPaths;
    private FileSystemMigrationReport report;

    @BeforeEach
    void createFiles() throws Exception {
        queue = new UploadQueue<>(10);
        expectedPaths = new HashSet<>();
        ignoredPaths = new HashSet<>();
        report = new DefaultFileSystemMigrationReport();
        directoryStreamCrawler = new DirectoryStreamCrawler(report);

        final Path sub1 = Files.createDirectory(tempDir.resolve("subdirectory"));
        final Path sub2 = Files.createDirectory(tempDir.resolve("subdirectory/import"));
        expectedPaths.add(Files.write(tempDir.resolve("newfile.txt"), "newfile content".getBytes()));
        expectedPaths.add(Files.write(sub1.resolve("subfile.txt"), "subfile content in the subdirectory".getBytes()));
        expectedPaths.add(Files.write(sub2.resolve("subfile2.txt"), "subfile content in the subdirectory".getBytes()));

        final Path ignored1 = Files.createDirectory(tempDir.resolve("import"));
        final Path ignored2 = Files.createDirectories(tempDir.resolve("plugins/.osgi-plugins"));
        final Path ignored3 = Files.createDirectories(tempDir.resolve("export"));
        final Path ignored4 = Files.createDirectories(tempDir.resolve("log"));
        ignoredPaths.add(Files.write(tempDir.resolve("dbconfig.xml"), "subfile".getBytes()));
        ignoredPaths.add(Files.write(tempDir.resolve("keyFile"), "keyfile".getBytes()));
        ignoredPaths.add(Files.write(tempDir.resolve("saltFile"), "saltfile".getBytes()));
        ignoredPaths.add(Files.write(tempDir.resolve("cluster.properties"), "subfile".getBytes()));
        ignoredPaths.add(Files.write(ignored1.resolve("ignore1.txt"), "subfile".getBytes()));
        ignoredPaths.add(Files.write(ignored2.resolve("ignore2.txt"), "subfile".getBytes()));
        ignoredPaths.add(Files.write(ignored3.resolve("export-file.zip"), "subfile".getBytes()));
        ignoredPaths.add(Files.write(ignored4.resolve("atlassian-jira.log"), "subfile".getBytes()));
    }

    @Test
    void shouldListAllSubdirectories() throws Exception {
        directoryStreamCrawler = new DirectoryStreamCrawler(report);
        directoryStreamCrawler.crawlDirectory(tempDir, queue);

        expectedPaths.forEach(path -> assertTrue(queue.contains(path), String.format("Expected %s is absent from crawler queue", path)));
        ignoredPaths.forEach(path -> assertFalse(queue.contains(path), String.format("Expected %s should have been ignored", path)));
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
    void shouldReportAllFilesFoundWhenComplete() throws IOException {
        directoryStreamCrawler = new DirectoryStreamCrawler(report);
        directoryStreamCrawler.crawlDirectory(tempDir, queue);

        assertTrue(report.isCrawlingFinished());
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
