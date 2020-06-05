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

package com.atlassian.migration.datacenter.core.aws.infrastructure.cleanup

import com.atlassian.migration.datacenter.core.aws.CfnApi
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class AWSMigrationStackCleanupServiceTest {

    @MockK
    lateinit var cfnApi: CfnApi
    @MockK
    lateinit var migrationService: MigrationService
    @MockK
    lateinit var context: MigrationContext
    @InjectMockKs
    lateinit var sut: AWSMigrationStackCleanupService

    val migrationStackName = "migrationstack"

    @Test
    fun shouldReturnTrueWhenStackIsDeleted() {
        givenMigrationStackNameIsInContext()
        every { cfnApi.deleteStack(migrationStackName) } answers {}

        assertTrue(sut.startMigrationInfrastructureCleanup())
    }

    @Test
    fun shouldReturnFalseWhenStackCantBeDeleted() {
        givenMigrationStackNameIsInContext()
        every { cfnApi.deleteStack(migrationStackName) } throws InfrastructureDeploymentError("error")

        assertFalse(sut.startMigrationInfrastructureCleanup())
    }

    @Test
    fun shouldReportDeletedWhenDeletionIsComplete() {
        givenMigrationStackNameIsInContext()
        every { cfnApi.getStatus(migrationStackName) } returns InfrastructureDeploymentStatus(InfrastructureDeploymentState.DELETE_COMPLETE, "")

        assertEquals(InfrastructureCleanupStatus.CLEANUP_COMPLETE, sut.getMigrationInfrastructureCleanupStatus())
    }

    @Test
    fun shouldReportDeletedWhenStackDoesntExist() {
        givenMigrationStackNameIsInContext()
        every { cfnApi.getStatus(migrationStackName) } throws StackInstanceNotFoundException.builder().build()

        assertEquals(InfrastructureCleanupStatus.CLEANUP_COMPLETE, sut.getMigrationInfrastructureCleanupStatus())
    }

    @Test
    fun shouldReportDeleteInProgressWhenStackIsBeingDeleted() {
        givenMigrationStackNameIsInContext()
        every { cfnApi.getStatus(migrationStackName) } returns InfrastructureDeploymentStatus(InfrastructureDeploymentState.DELETE_IN_PROGRESS, "")

        assertEquals(InfrastructureCleanupStatus.CLEANUP_IN_PROGRESS, sut.getMigrationInfrastructureCleanupStatus())
    }

    @Test
    fun shouldReportDeleteFailedWhenDeleteFails() {
        givenMigrationStackNameIsInContext()
        every { cfnApi.getStatus(migrationStackName) } returns InfrastructureDeploymentStatus(InfrastructureDeploymentState.DELETE_FAILED, "")

        assertEquals(InfrastructureCleanupStatus.CLEANUP_FAILED, sut.getMigrationInfrastructureCleanupStatus())
    }

    @ParameterizedTest
    @EnumSource(value = InfrastructureDeploymentState::class, names = ["CREATE_COMPLETE", "CREATE_IN_PROGRESS", "CREATE_FAILED"])
    fun shouldReportNotStartedWhenStackIsNotBeingDeletedAndExists(state: InfrastructureDeploymentState) {
        givenMigrationStackNameIsInContext()
        every { cfnApi.getStatus(migrationStackName) } returns InfrastructureDeploymentStatus(state, "")

        assertEquals(InfrastructureCleanupStatus.CLEANUP_NOT_STARTED, sut.getMigrationInfrastructureCleanupStatus())
    }

    private fun givenMigrationStackNameIsInContext() {
        every { migrationService.currentContext } returns context
        every { context.helperStackDeploymentId } returns migrationStackName
    }
}