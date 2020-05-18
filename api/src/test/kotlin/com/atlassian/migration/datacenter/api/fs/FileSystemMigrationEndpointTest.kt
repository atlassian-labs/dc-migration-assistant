package com.atlassian.migration.datacenter.api.fs

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