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
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
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
}