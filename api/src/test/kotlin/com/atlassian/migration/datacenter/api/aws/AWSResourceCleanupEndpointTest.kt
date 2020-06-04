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
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception
import javax.ws.rs.core.Response.Status.ACCEPTED
import javax.ws.rs.core.Response.Status.CONFLICT
import javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class AWSResourceCleanupEndpointTest {

    @MockK
    lateinit var cleanupService: MigrationInfrastructureCleanupService

    @InjectMockKs
    lateinit var sut: AWSResourceCleanupEndpoint

    @Test
    fun shouldReturnAcceptedWhenCleanupStarts() {
        every { cleanupService.scheduleMigrationInfrastructureCleanup() } returns true

        val resp = sut.cleanupMigrationInfrastructure()

        assertEquals(ACCEPTED.statusCode, resp.status)
    }

    @Test
    fun shouldReturnConflictWhenCleanupDoesntStart() {
        every { cleanupService.scheduleMigrationInfrastructureCleanup() } returns false

        val resp = sut.cleanupMigrationInfrastructure()

        assertEquals(CONFLICT.statusCode, resp.status)
    }

    @Test
    fun shouldReturnServerErrorWhenCleanupErrors() {
        every { cleanupService.scheduleMigrationInfrastructureCleanup() } throws Exception()

        val resp = sut.cleanupMigrationInfrastructure()

        assertEquals(INTERNAL_SERVER_ERROR.statusCode, resp.status)
    }


}