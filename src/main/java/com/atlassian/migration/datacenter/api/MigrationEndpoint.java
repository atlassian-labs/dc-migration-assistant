package com.atlassian.migration.datacenter.api;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.exceptions.MigrationAlreadyExistsException;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.google.common.collect.ImmutableMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST API Endpoint for managing in-product DC migrations.
 * Supports get and create.
 */
@Path("/migration")
public class MigrationEndpoint {

    private MigrationService migrationService;

    public MigrationEndpoint(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    /**
     * @return A response with the status of the current migration
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMigrationStatus() {
        if (migrationService.getCurrentStage() == MigrationStage.NOT_STARTED) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        } else {
            return Response
                    .ok(ImmutableMap.of("stage", migrationService.getCurrentStage().toString()))
                    .build();
        }
    }

    /**
     * Creates a new migration if none exists. Otherwise will respond with a 400 and an error message.
     *
     * @return no content if successful or 400 and error  message if a migration already exists.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMigration() {
        try {
            migrationService.createMigration();
            migrationService.transition(MigrationStage.NOT_STARTED, MigrationStage.AUTHENTICATION);
            return Response.noContent().build();
        } catch (MigrationAlreadyExistsException e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(ImmutableMap.of("error", "migration already exists"))
                    .build();
        } catch (InvalidMigrationStageError invalidMigrationStageError) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(ImmutableMap.of("error", "Unable to transition migration from initial state"))
                    .build();
        }
    }
}
