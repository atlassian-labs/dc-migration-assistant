package com.atlassian.migration.datacenter.api.fs;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class FileSystemMigrationEndpointTest {

    @Mock
    FilesystemMigrationService fsMigrationService;

    FileSystemMigrationEndpoint endpoint;

    @BeforeEach
    void setUp() {
        endpoint = new FileSystemMigrationEndpoint(fsMigrationService);
    }

    @Test
    void abortRunningMigrationShouldBeSuccessful() {
        final Response response = endpoint.abortFilesystemMigration();
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    void throwConflictIfMigrationIsNotRunning() throws Exception {
        doThrow(new InvalidMigrationStageError("running")).when(fsMigrationService).abortMigration();

        final Response response = endpoint.abortFilesystemMigration();

        assertEquals(response.getStatus(), Response.Status.CONFLICT.getStatusCode());
    }
}