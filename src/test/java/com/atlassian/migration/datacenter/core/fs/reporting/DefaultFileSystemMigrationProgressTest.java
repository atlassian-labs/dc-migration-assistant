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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultFileSystemMigrationProgressTest {

    private DefaultFilesystemMigrationProgress sut;

    @BeforeEach
    void setUp() {
        sut = new DefaultFilesystemMigrationProgress();
    }

    @Test
    void shouldBeInitialisedWithNoCompleteFiles() {
        assertEquals(0, sut.getCountOfMigratedFiles());
    }

    @Test
    void shouldAddMigratedFileToMigratedFiles() {
        sut.reportFileMigrated();

        assertEquals(1, sut.getCountOfMigratedFiles());
    }

    @Test
    void shouldCountMigratingFile() {
        sut.reportFileFound();

        assertEquals(1, sut.getNumberOfFilesFound());
    }

    @Test
    void shouldCountCommencedFileUploads() {
        sut.reportFileUploadCommenced();

        assertEquals(1, sut.getNumberOfCommencedFileUploads());
    }

    @Test
    void shouldHandleLargeNumberOfMigratedFiles() {
        int numFilesToMigrate = 1000000;
        IntStream.range(0, numFilesToMigrate).forEach(i -> sut.reportFileMigrated());

        assertEquals(numFilesToMigrate, sut.getCountOfMigratedFiles());
    }
}
