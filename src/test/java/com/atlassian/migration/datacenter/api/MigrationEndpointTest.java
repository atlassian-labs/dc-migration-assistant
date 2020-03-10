package com.atlassian.migration.datacenter.api;

import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MigrationEndpointTest {

    @Mock
    private MigrationService mockMigrationService;

    @InjectMocks
    private MigrationEndpoint sut;

    @Test
    public void testOKAndMigrationStatusWhenMigrationExists() {
        when(mockMigrationService.getMigrationStage()).thenReturn(MigrationStage.AUTHENTICATION);

        Response response = sut.getMigrationStatus();

        assertThat(response.getEntity().toString(), containsString(MigrationStage.AUTHENTICATION.toString()));
    }

    @Test
    public void testNotFoundWhenMigrationDoesNotExist() {
        when(mockMigrationService.getMigrationStage()).thenReturn(MigrationStage.NOT_STARTED);

        Response response = sut.getMigrationStatus();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testNoContentWhenCreatingMigrationAndNoneExists() {
        when(mockMigrationService.startMigration()).thenReturn(true);

        Response response = sut.createMigration();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testBadRequestWhenCreatingMigrationAndOneExists() {
        when(mockMigrationService.startMigration()).thenReturn(false);

        Response response = sut.createMigration();

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

}
