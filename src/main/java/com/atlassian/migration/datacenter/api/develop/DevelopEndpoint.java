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

package com.atlassian.migration.datacenter.api.develop;

import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/develop")
public class DevelopEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DevelopEndpoint.class);
    private MigrationService migrationService;

    public DevelopEndpoint(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @PUT
    @Path("/migration/stage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setMigrationStage(MigrationStage targetStage) {
        try {
            final MigrationStage currentStage = migrationService.getCurrentStage();
            migrationService.transition(currentStage, targetStage);
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
