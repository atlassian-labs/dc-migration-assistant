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
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.DONE;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.FAILED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.NOT_STARTED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.RUNNING;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultFileSystemMigrationReportTest {

    private DefaultFileSystemMigrationReport sut;

    @Spy
    FileSystemMigrationProgress progress;

    @Spy
    FileSystemMigrationErrorReport errors;

    @BeforeEach
    void setUp() {
        sut = new DefaultFileSystemMigrationReport(errors, progress);
    }

    @Test
    void testStatusInitiallyNotStarted() {
        assertEquals(NOT_STARTED, sut.getStatus());
    }

    @Test
    void testSetsStatus() {
        sut.setStatus(FAILED);

        assertEquals(FAILED, sut.getStatus());
    }

    @Test
    void shouldDelegatesToWrappedErrorReport() {
        final Path testFile = Paths.get("file");
        final String testReason = "test";
        final FailedFileMigration failedFileMigration = new FailedFileMigration(testFile, testReason);
        sut.reportFileNotMigrated(failedFileMigration);

        verify(errors).reportFileNotMigrated(failedFileMigration);

        sut.getFailedFiles();
        verify(errors).getFailedFiles();
    }

    @Test
    void shouldDelegateToWrappedProgress() {
        final Path path = Paths.get("file");
        sut.reportFileMigrated();

        verify(progress).reportFileMigrated();

        sut.getCountOfMigratedFiles();

        verify(progress).getCountOfMigratedFiles();
    }

    @Test
    void shouldGiveDurationBetweenStartedAndGetElapsedTime() {
        Clock testClock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        sut.setClock(testClock);

        sut.setStatus(RUNNING);

        assertEquals(Duration.ZERO, sut.getElapsedTime());

        sut.setClock(Clock.offset(testClock, Duration.ofDays(1).plusSeconds(5)));

        assertEquals(1L, sut.getElapsedTime().toDays());
    }

    @Test
    void shouldNotRestartTimerWhenTransitioningFromRunningToRunning() {
        Clock testClock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        sut.setClock(testClock);

        sut.setStatus(RUNNING);

        sut.setClock(Clock.offset(testClock, Duration.ofSeconds(10)));
        assertEquals(10L, sut.getElapsedTime().getSeconds());

        sut.setStatus(RUNNING);
        assertEquals(10L, sut.getElapsedTime().getSeconds());

        sut.setClock(Clock.offset(testClock, Duration.ofSeconds(20)));
        assertEquals(20L, sut.getElapsedTime().getSeconds());
    }

    @ParameterizedTest
    @EnumSource(value = FilesystemMigrationStatus.class, names = {"DONE", "FAILED"})
    void shouldNotIncrementElapsedTimeAfterMigrationEnds(FilesystemMigrationStatus status) {
        Clock testClock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        sut.setClock(testClock);

        sut.setStatus(RUNNING);

        sut.setClock(Clock.offset(testClock, Duration.ofSeconds(10)));
        assertEquals(10L, sut.getElapsedTime().getSeconds());

        sut.setStatus(status);
        sut.setClock(Clock.offset(testClock, Duration.ofSeconds(20)));
        assertEquals(10L, sut.getElapsedTime().getSeconds());
    }

    @Test
    void testToString() {
        final long successfullyMigrated = 888L;
        final int failedFiles = 666;
        when(progress.getCountOfMigratedFiles()).thenReturn(successfullyMigrated);

        final Set errorList = mock(Set.class);
        when(errorList.size()).thenReturn(failedFiles);
        when(errors.getFailedFiles()).thenReturn(errorList);

        sut.setStatus(DONE);

        final String text = sut.toString();
        Assert.assertThat(text, containsString("DONE"));
        Assert.assertThat(text, containsString(String.valueOf(successfullyMigrated)));
        Assert.assertThat(text, containsString(String.valueOf(failedFiles)));
    }
}
