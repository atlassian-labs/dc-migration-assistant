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

package com.atlassian.migration.datacenter.api.application

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/application")
class ApplicationPropertiesEndpoint(private val configuration: ApplicationConfiguration) {
    private val mapper = ObjectMapper().registerKotlinModule()

    companion object {
        val log = LoggerFactory.getLogger(ApplicationPropertiesEndpoint::class.java)
    }

    @GET
    @Path("/properties")
    @Produces(MediaType.APPLICATION_JSON)
    fun getApplicationProperties(): Response {
        val props = ApplicationProperties(configuration.applicationVersion)
        log.trace("Application properties: {}", props)

        return Response.ok(mapper.writeValueAsString(props)).build()
    }

    data class ApplicationProperties(val version: String)
}