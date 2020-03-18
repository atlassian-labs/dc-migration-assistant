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
            migrationService.transition(MigrationStage.AUTHENTICATION);
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
