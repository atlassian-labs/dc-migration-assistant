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

package com.atlassian.migration.datacenter.api.db;

import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/migration/fs")
public class DatabaseMigrationEndpoint
{

    private final DatabaseMigrationService databaseMigrationService;
    private final MigrationService migrationService;

    private final ObjectMapper mapper;

    public DatabaseMigrationEndpoint(DatabaseMigrationService databaseMigrationService,
                                     MigrationService migrationService) {
        this.databaseMigrationService = databaseMigrationService;
        this.migrationService = migrationService;
        this.mapper = new ObjectMapper();
        this.mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start")
    public Response runMigration() {
        if (migrationService.getCurrentStage().isDBPhase()) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(ImmutableMap.of("status", migrationService.getCurrentStage()))
                    .build();
        }

        boolean started = databaseMigrationService.scheduleMigration();
        Response.ResponseBuilder builder = started ? Response.status(Response.Status.ACCEPTED) : Response.status(Response.Status.CONFLICT);

        return builder
            .entity(ImmutableMap.of("migrationScheduled", started))
            .build();
    }


    @GET
    @Produces(APPLICATION_JSON)
    @Path("/report")
    public Response getMigrationStatus() {
        try {
            return Response
                    .ok(mapper.writeValueAsString(migrationService.getCurrentStage()))
                    .build();
        } catch (JsonProcessingException e) {
            return Response
                    .serverError()
                    .entity(String.format("Unable to get file system status. Please contact support and show them this error: %s", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Produces(APPLICATION_JSON)
    @Path("/abort")
    public Response abortMigration() {
        try {
            databaseMigrationService.abortMigration();
            return Response
                    .ok(ImmutableMap.of("cancelled", true))
                    .build();
        } catch (InvalidMigrationStageError e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ImmutableMap.of("error", "filesystem migration is not in progress"))
                    .build();
        }
    }
}
