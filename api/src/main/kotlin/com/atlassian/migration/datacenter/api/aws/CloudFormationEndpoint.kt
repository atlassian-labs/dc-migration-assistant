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

import com.atlassian.migration.datacenter.core.aws.CfnApi
import com.atlassian.migration.datacenter.core.aws.region.RegionService
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureDeploymentService
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException
import java.net.URLEncoder
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * REST API Endpoint for managing AWS provisioning.
 */
@Path("/aws/stack")
class CloudFormationEndpoint(
        private val applicationDeploymentService: ApplicationDeploymentService,
        private val migrationService: MigrationService,
        private val helperDeploymentService: MigrationInfrastructureDeploymentService,
        private val cfnApi: CfnApi,
        private val regionService: RegionService) {
    companion object {
        private val log = LoggerFactory.getLogger(CloudFormationEndpoint::class.java)
        private val mapper = ObjectMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
    }

    val PENDING_MIGRATION_INFR_STATUS = "PREPARING_MIGRATION_INFRASTRUCTURE_DEPLOYMENT"

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun provisionInfrastructure(provisioningConfig: ProvisioningConfig): Response {
        return try {
            val stackName = provisioningConfig.stackName
            when (provisioningConfig.deploymentMode) {
                ProvisioningConfig.DeploymentMode.WITH_NETWORK -> applicationDeploymentService.deployApplicationWithNetwork(stackName, provisioningConfig.params)
                ProvisioningConfig.DeploymentMode.STANDALONE -> applicationDeploymentService.deployApplication(stackName, provisioningConfig.params)
            }
            //Should be updated to URI location after get stack details Endpoint is built
            Response.status(Response.Status.ACCEPTED).entity(stackName).build()
        } catch (e: InvalidMigrationStageError) {
            log.error("Migration stage is not valid.", e)
            Response
                    .status(Response.Status.CONFLICT)
                    .entity(mapOf("error" to e.message))
                    .build()
        }
    }

    @GET
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun infrastructureStatus(): Response {
        return when (val currentMigrationStage = migrationService.currentStage) {
            MigrationStage.NOT_STARTED, MigrationStage.AUTHENTICATION, MigrationStage.PROVISION_APPLICATION -> Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "not currently deploying any infrastructure")).build()
            MigrationStage.PROVISION_APPLICATION_WAIT, MigrationStage.PROVISION_MIGRATION_STACK_WAIT -> handleAnyProvisioningInProgress(currentMigrationStage)
            MigrationStage.PROVISION_MIGRATION_STACK -> Response.ok(mapper.writeValueAsString(mapOf("status" to PENDING_MIGRATION_INFR_STATUS))).build()
            else -> handleProvisioningComplete()
        }
    }

    private fun handleProvisioningComplete(): Response {
        return try {
            val status = applicationDeploymentService.deploymentStatus
            Response.ok(mapper.writeValueAsString(mapOf("status" to status, "phase" to "complete"))).build()
        } catch (e: Exception) {
            Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to e.message)).build()
        }
    }

    private fun handleAnyProvisioningInProgress(currentMigrationStage: MigrationStage): Response {
        val phase = when (currentMigrationStage) {
            MigrationStage.PROVISION_APPLICATION_WAIT -> "app_infra"
            MigrationStage.PROVISION_MIGRATION_STACK_WAIT -> "migration_infra"
            else -> error("phase conditional doesn't match when cases")
        }
        return try {
            val deploymentStatus = when (currentMigrationStage) {
                MigrationStage.PROVISION_APPLICATION_WAIT -> applicationDeploymentService.deploymentStatus
                MigrationStage.PROVISION_MIGRATION_STACK_WAIT -> helperDeploymentService.deploymentStatus
                else -> error("phase conditional doesn't match when cases")
            }
            val entity = mutableMapOf(
                    "status" to deploymentStatus,
                    "phase" to phase
            )

            if (deploymentStatus == InfrastructureDeploymentState.CREATE_FAILED) {
                val deploymentId = when (currentMigrationStage) {
                    MigrationStage.PROVISION_APPLICATION_WAIT -> migrationService.currentContext.applicationDeploymentId
                    MigrationStage.PROVISION_MIGRATION_STACK_WAIT -> migrationService.currentContext.helperStackDeploymentId
                    else -> error("phase conditional doesn't match when cases")
                }

                val errorOptional = cfnApi.getStackErrorRootCause(deploymentId)
                entity["error"] = if (errorOptional.isEmpty) {
                    "unknown deployment error has occurred. Check the cloudformation console for details"
                } else {
                    errorOptional.get()
                }
                val stack = cfnApi.getStack(deploymentId).get()
                entity["stackUrl"] =
                        "https://console.aws.amazon.com/cloudformation/home?region=${regionService.region}#/stacks/stackinfo?filteringText=${URLEncoder.encode(deploymentId, "utf-8")}&filteringStatus=failed&viewNested=true&stackId=${URLEncoder.encode(stack.stackId(), "utf-8")}"
            }

            return Response.ok(mapper.writeValueAsString(entity)).build()
        } catch (e: StackInstanceNotFoundException) {
            Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(
                            mapper.writeValueAsString(mapOf("error" to "critical failure - infrastructure not found")))
                    .build()
        }
    }

}

