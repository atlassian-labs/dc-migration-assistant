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

import com.atlassian.migration.datacenter.core.aws.cloud.AWSConfigurationService
import com.atlassian.migration.datacenter.spi.exceptions.InvalidCredentialsException
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.fasterxml.jackson.annotation.JsonAutoDetect
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("aws/configure")
class AWSConfigureEndpoint(private val awsConfigurationService: AWSConfigurationService) {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun storeAWSCredentials(configure: AWSConfigureWebObject?): Response {
        return try {
            configure?.let {
                awsConfigurationService.configureCloudProvider(
                        configure.accessKeyId,
                        configure.secretAccessKey,
                        configure.region
                )
            }
            Response.noContent().build()
        } catch (e: InvalidMigrationStageError) {
            Response
                    .status(Response.Status.CONFLICT)
                    .entity(mapOf("message" to e.message))
                    .build()
        } catch (e: InvalidCredentialsException) {
            Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("message" to e.message))
                    .build()
        }
    }

    @JsonAutoDetect
    class AWSConfigureWebObject {
        var accessKeyId: String? = null
        var secretAccessKey: String? = null
        var region: String? = null
    }
}