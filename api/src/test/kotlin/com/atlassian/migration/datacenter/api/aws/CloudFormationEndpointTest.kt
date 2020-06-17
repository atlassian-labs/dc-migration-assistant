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
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureDeploymentService
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.cloudformation.model.Stack
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException
import java.util.*
import javax.ws.rs.core.Response
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.get

@ExtendWith(MockKExtension::class)
internal class CloudFormationEndpointTest {
    @MockK(relaxUnitFun = true)
    lateinit var deploymentService: ApplicationDeploymentService

    @MockK(relaxUnitFun = true)
    lateinit var helperDeploymentService: MigrationInfrastructureDeploymentService

    @MockK(relaxUnitFun = true)
    lateinit var migrationSerivce: MigrationService

    @MockK
    lateinit var context: MigrationContext

    @MockK
    lateinit var cfnApi: CfnApi

    @MockK
    lateinit var regionService: RegionService

    @InjectMockKs
    lateinit var endpoint: CloudFormationEndpoint

    @BeforeEach
    fun init() = MockKAnnotations.init(this)

    @Test
    fun shouldDeployStandaloneStackWhenConfigSaysSo() {
        val stackName = "stack-name"
        val provisioningConfig = ProvisioningConfig(stackName, HashMap(), ProvisioningConfig.DeploymentMode.STANDALONE)

        val response = endpoint.provisionInfrastructure(provisioningConfig)

        assertEquals(Response.Status.ACCEPTED.statusCode, response.status)
        assertEquals(provisioningConfig.stackName, response.entity)
        verify { deploymentService.deployApplication(stackName, HashMap()) }
    }

    @Test
    fun shouldDeployWithVirtualNetworkWhenConfigSaysSo() {
        val stackName = "stack-name"
        val provisioningConfig = ProvisioningConfig(stackName, HashMap(), ProvisioningConfig.DeploymentMode.WITH_NETWORK)

        val response = endpoint.provisionInfrastructure(provisioningConfig)

        assertEquals(Response.Status.ACCEPTED.statusCode, response.status)
        assertEquals(provisioningConfig.stackName, response.entity)
        verify { deploymentService.deployApplicationWithNetwork(stackName, HashMap()) }
    }

    @Test
    fun shouldBeConflictWhenCurrentMigrationStageIsNotValid() {
        val provisioningConfig = ProvisioningConfig("stack-name", HashMap(), ProvisioningConfig.DeploymentMode.STANDALONE)
        val errorMessage = "migration status is FUBAR"
        every {
            deploymentService.deployApplication(
                    provisioningConfig.stackName,
                    provisioningConfig.params
            )
        } throws InvalidMigrationStageError(errorMessage)

        val response = endpoint.provisionInfrastructure(provisioningConfig)

        assertEquals(Response.Status.CONFLICT.statusCode, response.status)
        assertEquals(errorMessage, (response.entity as Map<*, *>)["error"])
    }

    @Test
    fun shouldGetCurrentProvisioningStatusForGivenStackId() {
        val expectedState = InfrastructureDeploymentState.CREATE_IN_PROGRESS
        every { deploymentService.deploymentStatus } returns expectedState
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_APPLICATION_WAIT

        val response = endpoint.infrastructureStatus()

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertThat(response.entity as String, containsString("CREATE_IN_PROGRESS"))
    }

    @Test
    fun shouldGetHandleErrorWhenStatusCannotBeRetrieved() {
        val expectedErrorMessage = "critical failure - infrastructure not found"
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_APPLICATION_WAIT
        every { deploymentService.deploymentStatus } throws StackInstanceNotFoundException.builder()
                .message(expectedErrorMessage).build()

        val response = endpoint.infrastructureStatus()

        assertEquals(Response.Status.NOT_FOUND.statusCode, response.status)
        assertEquals(expectedErrorMessage, readResponseIntoMap(response)["error"])
    }

    @Test
    fun shouldReturnMigrationStackDeploymentStatusWhenItIsBeingDeployed() {
        val expectedState = InfrastructureDeploymentState.CREATE_IN_PROGRESS
        every { deploymentService.deploymentStatus } returns InfrastructureDeploymentState.CREATE_COMPLETE
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_MIGRATION_STACK_WAIT
        every { helperDeploymentService.deploymentStatus } returns expectedState

        val response = endpoint.infrastructureStatus()

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertThat(response.entity as String, containsString("CREATE_IN_PROGRESS"))
        assertThat(response.entity as String, not(containsString("CREATE_COMPLETE")))
    }

    @Test
    fun shouldReturnNotFoundWhenNoInfraIsBeingDeployed() {
        val expectedErrorMessage = "not currently deploying any infrastructure"
        every { migrationSerivce.currentStage } returns MigrationStage.AUTHENTICATION

        val response = endpoint.infrastructureStatus()

        assertEquals(Response.Status.NOT_FOUND.statusCode, response.status)
        assertEquals(expectedErrorMessage, (response.entity as Map<*, *>)["error"])
    }

    @Test
    fun shouldReturnCompleteAfterProvisioningPhase() {
        every { deploymentService.deploymentStatus } returns InfrastructureDeploymentState.CREATE_COMPLETE
        every { migrationSerivce.currentStage } returns MigrationStage.FS_MIGRATION_COPY

        val response = endpoint.infrastructureStatus()

        assertThat(response.entity as String, containsString("CREATE_COMPLETE"))
    }

    @Test
    fun shouldReturnIntermediatePhaseWhileBetweenDeployments() {
        val expectedStatus = "PREPARING_MIGRATION_INFRASTRUCTURE_DEPLOYMENT"
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_MIGRATION_STACK

        val response = endpoint.infrastructureStatus()

        assertEquals(Response.Status.OK.statusCode, response.status)
        val responseData = readResponseIntoMap(response)

        assertThat(responseData["status"]!!, equalTo(expectedStatus))
    }

    @Test
    fun shouldReturnApplicationAsPhaseWhenApplicationStackIsBeingDeployed() {
        val expectedPhase = "app_infra"
        every { deploymentService.deploymentStatus } returns InfrastructureDeploymentState.CREATE_IN_PROGRESS
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_APPLICATION_WAIT

        val response = endpoint.infrastructureStatus()

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertThat(response.entity as String, containsString(expectedPhase))
    }

    @Test
    fun shouldReturnMigrationAsPhaseWhenMigrationStackIsBeingDeployed() {
        val expectedPhase = "migration_infra"
        every { helperDeploymentService.deploymentStatus } returns InfrastructureDeploymentState.CREATE_IN_PROGRESS
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_MIGRATION_STACK_WAIT

        val response = endpoint.infrastructureStatus()

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertThat(response.entity as String, containsString(expectedPhase))
    }

    @Test
    fun shouldReturnReasonWhenDeploymentFails() {
        val expectedStatus = InfrastructureDeploymentState.CREATE_FAILED
        every { helperDeploymentService.deploymentStatus } returns expectedStatus
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_MIGRATION_STACK_WAIT

        every { migrationSerivce.currentContext } returns context
        val helperDeploymentId = "helper-deployment"
        every { context.helperStackDeploymentId } returns helperDeploymentId

        val testRegion = "us-east-1"
        every { regionService.region } returns testRegion
        every { cfnApi.getStack(helperDeploymentId) } returns Optional.of(Stack.builder().stackId("arn:aws:cloudformation:$testRegion:887764444972:stack/$helperDeploymentId/cf721e30-aab0-11ea-8ca3-122e54527a47").build())

        val permissionError = "you dont have permission"
        every { cfnApi.getStackErrorRootCause(helperDeploymentId) } returns Optional.of(permissionError)

        val response = endpoint.infrastructureStatus()

        assertEquals(Response.Status.OK.statusCode, response.status)

        val responseData = readResponseIntoMap(response)

        assertEquals(responseData["status"], "CREATE_FAILED")
        assertEquals(responseData["error"], permissionError)
    }

    @Test
    fun shouldBeOKGivenCurrentStageIsErrorWhenResetEndpointIsInvoked() {
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISIONING_ERROR
        val response = endpoint.resetProvisioningStage()
        assertEquals(Response.Status.OK.statusCode, response.status)
        verify { migrationSerivce.transition(MigrationStage.PROVISION_APPLICATION) }
    }

    @Test
    fun shouldBeBadRequestGivenCurrentStageIsNotErrorWhenResetEndpointIsInvoked() {
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_MIGRATION_STACK_WAIT
        val response = endpoint.resetProvisioningStage()
        assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
        assertEquals("Expected state to be ${MigrationStage.ERROR} but was ${MigrationStage.PROVISION_MIGRATION_STACK_WAIT}", (response.entity as Map<String, String>)["message"])
    }

    private fun responseEntityToMap(response: String): HashMap<String, String> {
        return ObjectMapper().readValue(response, object : TypeReference<HashMap<String, String>>() {})
    }

    @Test
    fun failedStatusShouldIncludeDashboardURL() {
        every { deploymentService.deploymentStatus } returns InfrastructureDeploymentState.CREATE_FAILED
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_APPLICATION_WAIT
        every { migrationSerivce.currentContext } returns context
        val deploymentId = "tcat-per-jira-37c49438"
        every { context.applicationDeploymentId } returns deploymentId
        every { cfnApi.getStackErrorRootCause(deploymentId) } returns Optional.of("error")
        val testRegion = "us-east-1"
        every { regionService.region } returns testRegion
        every { cfnApi.getStack(deploymentId) } returns Optional.of(Stack.builder().stackId("arn:aws:cloudformation:$testRegion:887764444972:stack/$deploymentId/cf721e30-aab0-11ea-8ca3-122e54527a47").build())

        val response = endpoint.infrastructureStatus()
        val responseData = readResponseIntoMap(response)

        val urlEncodedColon = "%3A"
        val urlEncodedSlash = "%2F"
        assertEquals(
                "https://console.aws.amazon.com/cloudformation/home?region=$testRegion#/stacks/stackinfo?filteringText=$deploymentId&filteringStatus=failed&viewNested=true&stackId=arn${urlEncodedColon}aws${urlEncodedColon}cloudformation$urlEncodedColon$testRegion${urlEncodedColon}887764444972${urlEncodedColon}stack$urlEncodedSlash$deploymentId${urlEncodedSlash}cf721e30-aab0-11ea-8ca3-122e54527a47",
                responseData["stackUrl"]
        )
    }

    @Test
    fun successfulStatusShouldNotIncludeURLOrError() {
        every { deploymentService.deploymentStatus } returns InfrastructureDeploymentState.CREATE_IN_PROGRESS
        every { migrationSerivce.currentStage } returns MigrationStage.PROVISION_APPLICATION_WAIT

        val response = endpoint.infrastructureStatus()
        val responseData = readResponseIntoMap(response)

        assertFalse(responseData.containsKey("stackUrl"))
        assertFalse(responseData.containsKey("error"))
    }

    private fun readResponseIntoMap(response: Response): HashMap<String, String> {
        val typeRef: TypeReference<HashMap<String, String>> = object : TypeReference<HashMap<String, String>>() {}
        return ObjectMapper().readValue(response.entity as String, typeRef)
    }
}
