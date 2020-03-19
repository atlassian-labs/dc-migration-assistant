package com.atlassian.migration.datacenter.api.develop;

import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/develop")
public class DevelopEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DevelopEndpoint.class);
    static final String ALLOW_ANY_TRANSITION_PROFILE = "allowAnyTransition";
    private MigrationService migrationService;
    private final Environment environment;

    public DevelopEndpoint(MigrationService migrationService, Environment environment) {
        this.migrationService = migrationService;
        this.environment = environment;
    }

    @PUT
    @Path("/migration/stage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setMigrationStage(MigrationStage targetStage) {
        boolean isProfileEnabled = Arrays.stream(this.environment.getActiveProfiles()).anyMatch(x -> x.equalsIgnoreCase(ALLOW_ANY_TRANSITION_PROFILE));
        if (!isProfileEnabled) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            migrationService.transition(targetStage);
            return Response
                    .ok(ImmutableMap.of("targetStage", migrationService.getCurrentStage().toString()))
                    .build();
        } catch (Exception e) {
            logger.warn("Cannot parse the migration stage", e);
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(ImmutableMap.of("error", String.format("Unable to transition migration to %s", targetStage)))
                    .build();
        }
    }
}
