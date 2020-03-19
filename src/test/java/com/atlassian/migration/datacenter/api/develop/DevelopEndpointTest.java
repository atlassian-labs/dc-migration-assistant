package com.atlassian.migration.datacenter.api.develop;

import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import javax.ws.rs.core.Response;

import java.util.Map;

import static com.atlassian.migration.datacenter.api.develop.DevelopEndpoint.ALLOW_ANY_TRANSITION_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DevelopEndpointTest {
    @Mock
    MigrationService migrationService;

    @Mock
    Environment environment;

    DevelopEndpoint endpoint;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        endpoint = new DevelopEndpoint(migrationService, environment);
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void shouldSetStageWithCorrectTargetStageWhenProfileIsEnabled() throws Exception {
        final MigrationStage migrationStage = objectMapper.readValue("\"FS_MIGRATION_COPY\"", MigrationStage.class);

        when(environment.getActiveProfiles()).thenReturn(new String[]{ALLOW_ANY_TRANSITION_PROFILE});
        when(migrationService.getCurrentStage()).thenReturn(migrationStage);

        Response response = endpoint.setMigrationStage(migrationStage);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String,String> entity = (Map<String,String>)response.getEntity();
        assertEquals(migrationStage.toString(), entity.get("targetStage"));

        verify(migrationService).transition(MigrationStage.FS_MIGRATION_COPY);
    }

    @Test
    void shouldNotCallMigrationServiceWhenProfileIsDisabled() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[]{});

        Response response = endpoint.setMigrationStage(MigrationStage.FS_MIGRATION_COPY_WAIT);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        verifyNoInteractions(migrationService);
    }
}
