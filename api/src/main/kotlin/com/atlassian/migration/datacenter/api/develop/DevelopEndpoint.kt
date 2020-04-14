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
package com.atlassian.migration.datacenter.api.develop

import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/develop")
class DevelopEndpoint(private val migrationService: MigrationService, private val environment: Environment) {

    companion object {
        private val logger = LoggerFactory.getLogger(DevelopEndpoint::class.java)
        const val ALLOW_ANY_TRANSITION_PROFILE = "allowAnyTransition"
    }

    @PUT
    @Path("/migration/stage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun setMigrationStage(targetStage: MigrationStage): Response {
        return if (!isProfileEnabled()) {
            Response.status(Response.Status.NOT_FOUND).build()
        } else {
            try {
                migrationService.transition(targetStage)
                Response
                    .ok(mapOf("targetStage" to migrationService.currentStage.toString()))
                    .build()
            } catch (e: Exception) {
                logger.warn("Cannot parse the migration stage", e)
                Response
                    .status(Response.Status.CONFLICT)
                    .entity(mapOf("error" to "Unable to transition migration to $targetStage"))
                    .build()
            }
        }
    }

    @DELETE
    @Path("/migration/reset")
    @Produces(MediaType.APPLICATION_JSON)
    fun resetMigrations(): Response {
        return if (!isProfileEnabled()) {
            Response.status(Response.Status.NOT_FOUND).build()
        } else {
            migrationService.deleteMigrations()
            return Response.ok("Reset migration status").build()
        }
    }

    private fun isProfileEnabled(): Boolean {
        return environment.activeProfiles.any { it.equals(ALLOW_ANY_TRANSITION_PROFILE, ignoreCase = true) }
    }
}