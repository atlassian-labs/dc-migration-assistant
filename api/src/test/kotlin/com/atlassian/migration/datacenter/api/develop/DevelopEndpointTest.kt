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
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.env.Environment
import javax.ws.rs.core.Response

@ExtendWith(MockKExtension::class)
internal class DevelopEndpointTest {
    @MockK
    lateinit var migrationService: MigrationService

    @MockK
    lateinit var environment: Environment

    @InjectMockKs
    lateinit var endpoint: DevelopEndpoint

    private val objectMapper: ObjectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    @Throws(Exception::class)
    fun shouldSetStageWithCorrectTargetStageWhenProfileIsEnabled() {
        val migrationStage = objectMapper.readValue("\"FS_MIGRATION_COPY\"", MigrationStage::class.java)
        every { environment.activeProfiles } returns arrayOf(DevelopEndpoint.ALLOW_ANY_TRANSITION_PROFILE)
        every { migrationService.currentStage } returns migrationStage
        every { migrationService.transition(MigrationStage.FS_MIGRATION_COPY) } just Runs

        val response = endpoint.setMigrationStage(migrationStage)

        assertEquals(
            Response.Status.OK.statusCode,
            response.status
        )
        val entity = response.entity as Map<*, *>
        assertEquals(migrationStage.toString(), entity["targetStage"])
        verify { migrationService.transition(MigrationStage.FS_MIGRATION_COPY) }
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotCallMigrationServiceWhenProfileIsDisabled() {
        every { environment.activeProfiles } returns arrayOf()

        val response = endpoint.setMigrationStage(MigrationStage.FS_MIGRATION_COPY_WAIT)

        assertEquals(
            Response.Status.NOT_FOUND.statusCode,
            response.status
        )
        verify(exactly = 0) { migrationService.transition(any()) }
    }

    @Test
    fun shouldResetMigrations() {
        every { environment.activeProfiles } returns arrayOf(DevelopEndpoint.ALLOW_ANY_TRANSITION_PROFILE)
        every { migrationService.deleteMigrations() } just Runs

        val response = endpoint.resetMigrations()

        assertEquals(
            Response.Status.OK.statusCode,
            response.status
        )
        verify { migrationService.deleteMigrations() }
    }
}