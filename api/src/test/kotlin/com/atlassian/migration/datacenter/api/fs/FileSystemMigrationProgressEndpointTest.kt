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

import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService
import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Paths
import java.time.Duration
import java.util.HashSet
import javax.ws.rs.core.Response
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class FileSystemMigrationProgressEndpointTest {
    @MockK
    lateinit var fsMigrationService: FilesystemMigrationService

    @MockK
    lateinit var report: FileSystemMigrationReport

    @InjectMockKs
    lateinit var endpoint: FileSystemMigrationEndpoint

    @BeforeEach
    fun init() = MockKAnnotations.init(this)

    @Test
    fun shouldReturnReportWhenMigrationExists() {
        val testReason = "test reason"
        val testFile = Paths.get("file")
        val failedFileMigration = FailedFileMigration(testFile, testReason)
        val failedFilesCollection = hashSetOf<FailedFileMigration>()
        failedFilesCollection.add(failedFileMigration)
        every { fsMigrationService.report } returns mockk {
            every { status } returns FilesystemMigrationStatus.UPLOADING
            every { numberOfCommencedFileUploads } returns 1L
            every { numberOfFilesFound } returns 1L
            every { failedFiles } returns failedFilesCollection
            every { countOfUploadedFiles } returns 1L
            every { elapsedTime } returns Duration.ofMinutes(1)
            every { countOfDownloadFiles } returns 1L
            every { isCrawlingFinished } returns true
        }

        val response = endpoint.getFilesystemMigrationStatus()

        val mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        val responseJson = response.entity as String
        val reader = mapper.reader()
        val tree = reader.readTree(responseJson)
        val responseStatus = tree.at("/status").asText()
        val responseReason = tree.at("/failedFiles/0/reason").asText()
        val responseFailedFile = tree.at("/failedFiles/0/filePath").asText()
        val responseSuccessFileCount = tree.at("/uploadedFiles").asLong()
        val responseDownloadFileCount = tree.at("/downloadedFiles").asLong()
        val responseAllFilesFound = tree.at("/crawlingFinished").asBoolean()

        assertEquals(FilesystemMigrationStatus.UPLOADING.name, responseStatus)
        assertEquals(testReason, responseReason)
        assertEquals(testFile.toUri().toString(), responseFailedFile)
        assertEquals(1, responseSuccessFileCount)
        assertEquals(1, responseDownloadFileCount)
        assertTrue(responseAllFilesFound)
    }

    @Test
    fun shouldHandleVeryLargeReport() {
        every { fsMigrationService.report } returns report
        every { report.status } returns FilesystemMigrationStatus.UPLOADING
        every { report.elapsedTime } returns Duration.ofMinutes(1)
        every { report.numberOfFilesFound } returns 1000000L
        every { report.numberOfCommencedFileUploads } returns 1000000L
        every { report.countOfDownloadFiles } returns 1000000L
        every { report.isCrawlingFinished } returns true
        val failedFiles: MutableSet<FailedFileMigration?> = HashSet()
        val testReason = "test reason"
        val testFile = Paths.get("file")
        for (i in 0..99) {
            val failedFileMigration = FailedFileMigration(testFile, testReason)
            failedFiles.add(failedFileMigration)
        }
        every { report.failedFiles } returns failedFiles
        every { report.countOfUploadedFiles } returns 1000000L

        val response = endpoint.getFilesystemMigrationStatus()

        val mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        val responseJson = response.entity as String
        val reader = mapper.reader()
        val tree = reader.readTree(responseJson)
        val responseStatus = tree.at("/status").asText()
        val responseReason = tree.at("/failedFiles/99/reason").asText()
        val responseFailedFile = tree.at("/failedFiles/99/filePath").asText()
        val responseSuccessFileCount = tree.at("/uploadedFiles").asLong()
        val responseDownloadFileCount = tree.at("/downloadedFiles").asLong()
        val responseAllFilesFound = tree.at("/crawlingFinished").asBoolean()

        assertEquals(FilesystemMigrationStatus.UPLOADING.name, responseStatus)
        assertEquals(testReason, responseReason)
        assertEquals(testFile.toUri().toString(), responseFailedFile)
        assertEquals(1000000, responseSuccessFileCount)
        assertEquals(1000000, responseDownloadFileCount)
        assertTrue(responseAllFilesFound)
    }

    @Test
    fun shouldReturnBadRequestWhenNoReportExists() {
        every { fsMigrationService.report } returns null

        val response = endpoint.getFilesystemMigrationStatus()

        assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
        assertThat<String?>(
            response.entity.toString(),
            Matchers.containsString("no file system migration exists")
        )
    }

    @Test
    fun shouldNotRunFileMigrationWhenExistingMigrationIsInProgress() {
        val reportMock = mockk<FileSystemMigrationReport>()
        every { reportMock.status } returns FilesystemMigrationStatus.UPLOADING
        every { fsMigrationService.isRunning } returns true
        every { fsMigrationService.report } returns reportMock

        val response = endpoint.runFileMigration()

        assertEquals(Response.Status.CONFLICT.statusCode, response.status)
        assertEquals(FilesystemMigrationStatus.UPLOADING, (response.entity as MutableMap<*, *>)["status"])
    }

    @Test
    fun shouldRunFileMigrationWhenNoOtherMigrationIsNotInProgress() {
        every { fsMigrationService.isRunning } returns false
        every { fsMigrationService.scheduleMigration() } returns true

        val response = endpoint.runFileMigration()

        assertEquals(Response.Status.ACCEPTED.statusCode, response.status)
        assertEquals(true, (response.entity as MutableMap<*, *>)["migrationScheduled"])
    }

    @Test
    fun shouldNotRunFileMigrationWhenWhenUnableToScheduleMigration() {
        every { fsMigrationService.isRunning } returns false
        every { fsMigrationService.scheduleMigration() } returns false

        val response = endpoint.runFileMigration()

        assertEquals(Response.Status.CONFLICT.statusCode, response.status)
        assertEquals(false, (response.entity as MutableMap<*, *>)["migrationScheduled"])
    }
}