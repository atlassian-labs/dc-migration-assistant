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

package com.atlassian.migration.datacenter.api.fs

import com.atlassian.migration.datacenter.core.fs.FileSystemMigrationReportManager
import com.atlassian.migration.datacenter.core.fs.captor.AttachmentSyncManager
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.ws.rs.core.Response

@ExtendWith(MockKExtension::class)
internal class FileSystemMigrationEndpointTest {
    @MockK
    lateinit var fsMigrationService: FilesystemMigrationService

    @MockK
    lateinit var migrationService: MigrationService

    @MockK
    lateinit var attachmentSyncManager: AttachmentSyncManager

    @MockK
    lateinit var reportManager: FileSystemMigrationReportManager

    @InjectMockKs
    lateinit var endpoint: FileSystemMigrationEndpoint

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun abortRunningMigrationShouldBeSuccessful() {
        every { fsMigrationService.abortMigration() } just runs

        val response = endpoint.abortFilesystemMigration()

        assertEquals(response.status, Response.Status.OK.statusCode)
    }

    @Test
    @Throws(Exception::class)
    fun throwConflictIfMigrationIsNotRunning() {
        every { fsMigrationService.abortMigration() } throws InvalidMigrationStageError("running")

        val response = endpoint.abortFilesystemMigration()

        assertEquals(response.status, Response.Status.CONFLICT.statusCode)
    }

    @Test
    fun shouldBeAbleToRetryAMigration() {
        every { fsMigrationService.abortMigration() } just runs
        every { migrationService.transition(MigrationStage.FS_MIGRATION_COPY) } just runs
        every { fsMigrationService.scheduleMigration() } returns true

        val response = endpoint.retryFileSystemMigration()

        assertEquals(response.status, Response.Status.ACCEPTED.statusCode)
    }

    @Test
    fun shouldRetryMigrationEvenWhenAbortMigrationThrowsAnException() {
        every { fsMigrationService.abortMigration() } throws InvalidMigrationStageError("bad state error")
        every { migrationService.transition(MigrationStage.FS_MIGRATION_COPY) } just runs
        every { fsMigrationService.scheduleMigration() } returns true

        val response = endpoint.retryFileSystemMigration()

        assertEquals(response.status, Response.Status.ACCEPTED.statusCode)

        verify { migrationService.transition(MigrationStage.FS_MIGRATION_COPY) }
        verify { fsMigrationService.scheduleMigration() }
    }

    @Test
    fun shouldNotRetryMigrationWhenMigrationCannotBeTransitionedToStartStage() {
        every { fsMigrationService.abortMigration() } just runs
        every { migrationService.transition(MigrationStage.FS_MIGRATION_COPY) } throws InvalidMigrationStageError("bad transition")

        val response = endpoint.retryFileSystemMigration()

        assertEquals(response.status, Response.Status.BAD_REQUEST.statusCode)

        verify(exactly = 0) { fsMigrationService.scheduleMigration() }
    }


    @Test
    fun shouldNotRetryMigrationWhenFsMigrationCannotBeScheduled() {
        every { fsMigrationService.abortMigration() } just runs
        every { migrationService.transition(MigrationStage.FS_MIGRATION_COPY) } just runs
        every { fsMigrationService.scheduleMigration() } returns false

        val response = endpoint.retryFileSystemMigration()

        assertEquals(response.status, Response.Status.CONFLICT.statusCode)
    }

}