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

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.core.exceptions.MigrationAlreadyExistsException
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * REST API Endpoint for managing in-product DC migrations.
 * Supports get and create.
 */
@Path("/migration")
class MigrationEndpoint(private val migrationService: MigrationService) {
    /**
     * @return A response with the status of the current migration
     */
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    fun getMigrationStatus(): Response {
        return if (migrationService.currentStage == MigrationStage.NOT_STARTED) {
            Response
                .status(Response.Status.NOT_FOUND)
                .build()
        } else {
            Response
                .ok(mapOf("stage" to migrationService.currentStage.toString()))
                .build()
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
    fun createMigration(): Response {
        return try {
            migrationService.createMigration()
            migrationService.transition(MigrationStage.AUTHENTICATION)
            Response.noContent().build()
        } catch (e: MigrationAlreadyExistsException) {
            Response
                .status(Response.Status.CONFLICT)
                .entity(mapOf("error" to "migration already exists"))
                .build()
        } catch (invalidMigrationStageError: InvalidMigrationStageError) {
            Response
                .status(Response.Status.CONFLICT)
                .entity(mapOf("error" to "Unable to transition migration from initial state"))
                .build()
        }
    }
}