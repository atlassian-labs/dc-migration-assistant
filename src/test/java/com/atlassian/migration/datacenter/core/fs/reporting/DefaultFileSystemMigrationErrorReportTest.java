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

package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultFileSystemMigrationErrorReportTest {

    private DefaultFileSystemMigrationErrorReport sut;

    @BeforeEach
    void setUp() {
        sut = new DefaultFileSystemMigrationErrorReport();
    }

    @Test
    void shouldBeInitialisedWithNoErrors() {
        assertTrue(sut.getFailedFiles().isEmpty(), "expected failed files to be empty on fresh report");
    }

    @Test
    void shouldAddReportedErrorsToFailedFiles() {
        final Path testFile = Paths.get("file");
        final String testReason = "it broke";
        sut.reportFileNotMigrated(new FailedFileMigration(testFile, testReason));

        assertEquals(1, sut.getFailedFiles().size());

        assertTrue(sut.getFailedFiles().contains(new FailedFileMigration(testFile, testReason)));
    }
}
