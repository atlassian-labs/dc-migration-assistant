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
package com.atlassian.migration.datacenter.api

import com.atlassian.migration.datacenter.dto.Migration
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.exceptions.MigrationAlreadyExistsException
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import com.atlassian.plugins.rest.common.sal.websudo.WebSudoRequiredException
import com.atlassian.plugins.rest.common.sal.websudo.WebSudoResourceContext
import com.atlassian.plugins.rest.common.sal.websudo.WebSudoResourceFilterFactory
import com.sun.jersey.api.model.AbstractResource
import com.sun.jersey.api.model.AbstractResourceMethod
import com.sun.jersey.api.model.PathValue
import com.sun.jersey.spi.container.ContainerRequest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import javax.ws.rs.core.Response

@ExtendWith(MockKExtension::class)
class MigrationEndpointTest {
    @MockK
    lateinit var migrationService: MigrationService

    @MockK
    lateinit var migrationContext: MigrationContext

    @MockK
    lateinit var cleanupService: MigrationInfrastructureCleanupService

    @InjectMockKs
    lateinit var sut: MigrationEndpoint

    @BeforeEach
    fun init() = MockKAnnotations.init(this)

    @Test
    fun testOKAndMigrationStatusWhenMigrationExists() {
        every { migrationService.currentStage } returns MigrationStage.AUTHENTICATION

        val response = sut.getMigrationStatus()

        assertThat(response.entity.toString(), Matchers.containsString(MigrationStage.AUTHENTICATION.toString()))
    }

    @Test
    fun shouldBeErrorWhenStageIsError() {
        every { migrationService.currentStage } returns MigrationStage.ERROR

        val response = sut.getMigrationStatus()

        assertThat(response.entity.toString(), Matchers.containsString(MigrationStage.ERROR.toString()))
    }

    @Test
    fun testOKAndMigrationContextWhenMigrationExists() {
        val expectedServiceUrl = "i_am_a_service_url"

        every { migrationService.currentStage } returns MigrationStage.VALIDATE
        every { migrationService.currentContext } returns migrationContext
        every { migrationContext.serviceUrl } returns expectedServiceUrl
        every { migrationContext.getErrorMessage() } returns "foobar"

        val response = sut.getMigrationSummary()

        assertThat(response.status, Matchers.equalTo(Response.Status.OK.statusCode))
        val entity = response.entity as? Map<String, String>
        assertThat(entity!!["instanceUrl"], equalTo(expectedServiceUrl))
    }

    @Test
    fun testNotStartedWhenMigrationDoesNotExist() {
        every { migrationService.currentStage } returns MigrationStage.NOT_STARTED
        val response = sut.getMigrationStatus()
        assertEquals(Response.Status.OK.statusCode, response.status)
        assertThat(response.entity.toString(), Matchers.containsString(MigrationStage.NOT_STARTED.toString()))
    }

    @Test
    fun testNoContentWhenCreatingMigrationAndNoneExists() {
        val stubMigration = mockk<Migration>()
        every { migrationService.createMigration() } returns stubMigration
        every { migrationService.transition(MigrationStage.AUTHENTICATION) } just runs

        val response = sut.createMigration()

        assertEquals(Response.Status.NO_CONTENT.statusCode, response.status)
    }

    @Test
    @Throws(Exception::class)
    fun testBadRequestWhenCreatingMigrationAndOneExists() {
        val stubMigration = mockk<Migration>()
        every { migrationService.createMigration() } returns stubMigration
        every { migrationService.transition(MigrationStage.AUTHENTICATION) } throws InvalidMigrationStageError("")

        val response = sut.createMigration()

        assertEquals(Response.Status.CONFLICT.statusCode, response.status)
        val entity = response.entity as MutableMap<*, *>
        assertEquals("Unable to transition migration from initial state", entity["error"])
    }

    @Test
    fun testBadRequestWhenCreatingMigrationAndUnableToTransitionPastTheInitialStage() {
        every { (migrationService).createMigration() } throws MigrationAlreadyExistsException("")
        every { migrationService.transition(any()) } just Runs

        val response = sut.createMigration()

        assertEquals(Response.Status.CONFLICT.statusCode, response.status)
        val entity = response.entity as MutableMap<*, *>
        assertEquals("migration already exists", entity["error"])
        verify(exactly = 0) { migrationService.transition(any()) }
    }

    @Test
    fun shouldCleanUpInfrastructureAndResetMigration() {
        every { migrationService.deleteMigrations() } just Runs
        every { cleanupService.startMigrationInfrastructureCleanup() } returns true

        val response = sut.resetMigration()

        assertThat(response.status, equalTo(Response.Status.OK.statusCode))
        verify { migrationService.deleteMigrations() }
        verify { cleanupService.startMigrationInfrastructureCleanup() }
    }

    @Test
    fun shouldShowErrorMessageInMigration() {
        val expectedError = "i am an error message"

        every { migrationService.currentStage } returns MigrationStage.ERROR
        every { migrationService.currentContext } returns migrationContext
        every { migrationContext.serviceUrl } returns "foobar"
        every { migrationContext.getErrorMessage() } returns expectedError

        val response = sut.getMigrationSummary()

        assertThat(response.status, Matchers.equalTo(Response.Status.OK.statusCode))
        val entity = response.entity as? Map<String, String>
        assertThat(entity!!["error"], equalTo(expectedError))
    }

    @Test
    fun shouldBeOkWhenFinishCurrentMigrationIsCalledSuccessfully() {
        every { migrationService.finishCurrentMigration() } just Runs

        val response = sut.finishMigration()

        assertThat(response.status, equalTo(Response.Status.OK.statusCode))
    }

    @Test
    fun shouldBeBadRequestWhenFinishCurrentMigrationThrowsAnException() {
        val message = "bad stage"
        every { migrationService.finishCurrentMigration() } throws InvalidMigrationStageError(message)

        val response = sut.finishMigration()

        assertThat(response.status, equalTo(Response.Status.BAD_REQUEST.statusCode))
        assertThat((response.entity as Map<String, String>)["message"], equalTo(message))
    }

    @Test
    fun shouldRequireWebSudo() {
        val context = mockk<WebSudoResourceContext>()
        every { context.shouldEnforceWebSudoProtection() } returns true

        val clazz = MigrationEndpoint::class.java
        val abstractResource = AbstractResource(clazz, PathValue("/migration"))
        val method = AbstractResourceMethod(
            abstractResource,
            clazz.getMethod("getMigrationSummary"),
            clazz,
            clazz,
            "GET",
            arrayOf()
        )
        val filter = WebSudoResourceFilterFactory(context).create(method)
        val request = mockk<ContainerRequest>()

        assertThrows<WebSudoRequiredException> { filter[0].requestFilter.filter(request) }
    }
}