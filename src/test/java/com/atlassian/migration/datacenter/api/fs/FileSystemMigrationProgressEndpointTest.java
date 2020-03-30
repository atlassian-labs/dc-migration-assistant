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

package com.atlassian.migration.datacenter.api.fs;

import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.RUNNING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileSystemMigrationProgressEndpointTest {

    @Mock
    private FilesystemMigrationService fsMigrationService;

    @Mock
    private FileSystemMigrationReport report;

    private FileSystemMigrationEndpoint endpoint;

    @BeforeEach
    void setUp() {
        endpoint = new FileSystemMigrationEndpoint(fsMigrationService);
    }

    @Test
    void shouldReturnReportWhenMigrationExists() throws JsonProcessingException {
        when(fsMigrationService.getReport()).thenReturn(report);

        when(report.getStatus()).thenReturn(RUNNING);

        final String testReason = "test reason";
        final Path testFile = Paths.get("file");
        final FailedFileMigration failedFileMigration = new FailedFileMigration(testFile, testReason);
        final Set<FailedFileMigration> failedFiles = new HashSet<>();
        failedFiles.add(failedFileMigration);
        when(report.getFailedFiles()).thenReturn(failedFiles);

        when(report.getCountOfMigratedFiles()).thenReturn(1L);

        final Response response = endpoint.getFilesystemMigrationStatus();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);

        final String responseJson = (String) response.getEntity();
        ObjectReader reader = mapper.reader();
        JsonNode tree = reader.readTree(responseJson);

        final String responseStatus = tree.at("/status").asText();

        final String responseReason = tree.at("/failedFiles/0/reason").asText();
        final String responseFailedFile = tree.at("/failedFiles/0/filePath").asText();

        final Long responseSuccessFileCount = tree.at("/migratedFiles").asLong();

        assertEquals(RUNNING.name(), responseStatus);
        assertEquals(testReason, responseReason);
        assertEquals(testFile.toUri().toString(), responseFailedFile);
        assertEquals(1, responseSuccessFileCount);
    }

    @Test
    void shouldHandleVeryLargeReport() throws JsonProcessingException {
        when(fsMigrationService.getReport()).thenReturn(report);

        when(report.getStatus()).thenReturn(RUNNING);

        final Set<FailedFileMigration> failedFiles = new HashSet<>();

        final String testReason = "test reason";
        final Path testFile = Paths.get("file");
        for (int i = 0; i < 100; i++) {
            final FailedFileMigration failedFileMigration = new FailedFileMigration(testFile, testReason);
            failedFiles.add(failedFileMigration);
        }

        when(report.getFailedFiles()).thenReturn(failedFiles);

        when(report.getCountOfMigratedFiles()).thenReturn(1000000L);

        final Response response = endpoint.getFilesystemMigrationStatus();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);

        final String responseJson = (String) response.getEntity();
        ObjectReader reader = mapper.reader();
        JsonNode tree = reader.readTree(responseJson);

        final String responseStatus = tree.at("/status").asText();

        final String responseReason = tree.at("/failedFiles/99/reason").asText();
        final String responseFailedFile = tree.at("/failedFiles/99/filePath").asText();

        final Long responseSuccessFileCount = tree.at("/migratedFiles").asLong();

        assertEquals(RUNNING.name(), responseStatus);
        assertEquals(testReason, responseReason);
        assertEquals(testFile.toUri().toString(), responseFailedFile);
        assertEquals(1000000, responseSuccessFileCount);
    }

    @Test
    void shouldReturnBadRequestWhenNoReportExists() {
        when(fsMigrationService.getReport()).thenReturn(null);

        final Response response = endpoint.getFilesystemMigrationStatus();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertThat(response.getEntity().toString(), containsString("no file system migration exists"));
    }

    @Test
    void shouldNotRunFileMigrationWhenExistingMigrationIsInProgress() {
        FileSystemMigrationReport reportMock = Mockito.mock(FileSystemMigrationReport.class);
        when(reportMock.getStatus()).thenReturn(RUNNING);

        when(fsMigrationService.isRunning()).thenReturn(true);
        when(fsMigrationService.getReport()).thenReturn(reportMock);

        Response response = endpoint.runFileMigration();

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals(RUNNING, ((Map<String, String>) response.getEntity()).get("status"));
    }

    @Test
    void shouldRunFileMigrationWhenNoOtherMigrationIsNotInProgress() throws Exception {
        when(fsMigrationService.isRunning()).thenReturn(false);
        when(fsMigrationService.scheduleMigration()).thenReturn(true);
        Response response = endpoint.runFileMigration();

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        assertEquals(true, ((Map<String, String>) response.getEntity()).get("migrationScheduled"));
    }

    @Test
    void shouldNotRunFileMigrationWhenWhenUnableToScheduleMigration() throws Exception {
        when(fsMigrationService.isRunning()).thenReturn(false);
        when(fsMigrationService.scheduleMigration()).thenReturn(false);
        Response response = endpoint.runFileMigration();

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals(false, ((Map<String, String>) response.getEntity()).get("migrationScheduled"));
    }
}
