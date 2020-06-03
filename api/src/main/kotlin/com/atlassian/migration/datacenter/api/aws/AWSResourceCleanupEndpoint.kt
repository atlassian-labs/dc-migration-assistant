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

package com.atlassian.migration.datacenter.api.aws

import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import org.slf4j.LoggerFactory
import javax.ws.rs.DELETE
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

/**
 * REST API endpoint for cleaning up migration resources
 */
@Path("/aws/cleanup")
class AWSResourceCleanupEndpoint(val cleanupService: MigrationInfrastructureCleanupService) {

    companion object {
        private val logger = LoggerFactory.getLogger(AWSResourceCleanupEndpoint::class.java)
    }

    @DELETE
    @Produces(APPLICATION_JSON)
    fun cleanupMigrationInfrastructure(): Response {
        val builder = try {
            val started = cleanupService.scheduleMigrationInfrastructureCleanup()

            if (started) {
                Response.status(Response.Status.ACCEPTED)
            } else {
                Response.status(Response.Status.CONFLICT)
            }
        } catch (e: Exception) {
            logger.error("Unable to cleanup migration infrastructure", e)
            Response.serverError()
        }

        return builder.build()
    }
}