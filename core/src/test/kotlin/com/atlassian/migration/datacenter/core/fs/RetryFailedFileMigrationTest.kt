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

package com.atlassian.migration.datacenter.core.fs

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport
import com.atlassian.migration.datacenter.core.util.UploadQueue
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.FileSystemMigrationFailure
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService
import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class RetryFailedFileMigrationTest {

    @MockK
    lateinit var reportManager: FileSystemMigrationReportManager

    @MockK
    lateinit var uploaderFactory: UploaderFactory

    @MockK
    lateinit var uploader: Uploader

    @MockK
    lateinit var fsMigrationService: FilesystemMigrationService

    @MockK
    lateinit var migrationService: MigrationService

    @InjectMockKs
    lateinit var sut: RetryFailedFileMigration

    private lateinit var prevReport: FileSystemMigrationReport
    private lateinit var newReport: FileSystemMigrationReport

    private val failedPaths = listOf(Paths.get("foo"), Paths.get("bar"), Paths.get("baz"))
    private val foundFiles = 30
    private val uploadsCommenced = foundFiles
    private val uploadedFiles = foundFiles - failedPaths.size
    private val filesDownloaded = uploadedFiles

    @BeforeEach
    fun setup() {
        prevReport = DefaultFileSystemMigrationReport()
        newReport = DefaultFileSystemMigrationReport()
        every { reportManager.getCurrentReport(ReportType.Filesystem) } returnsMany listOf(prevReport, newReport)
        every { uploaderFactory.newUploader(prevReport) } returns uploader
        every { uploader.upload(any()) } just runs
        every { reportManager.resetReport(ReportType.Filesystem) } returns newReport
        every { fsMigrationService.abortMigration() } just runs
        every { migrationService.transition(any())} just runs

        setupPreviousReportData()
    }

    @Test
    fun shouldUploadFilesFailed() {
        sut.uploadFailedFiles()

        val queue = UploadQueue<Path>(3)
        failedPaths.forEach { queue.put(it) }

        verify { uploader.upload(queue) }
        verify { reportManager.resetReport(ReportType.Filesystem) }

        assertEquals(failedPaths.size.toLong(), newReport.getNumberOfFilesFound())
    }

    @Test
    fun shouldAbortRunningFileMigration() {
        sut.uploadFailedFiles()

        verify { fsMigrationService.abortMigration() }
    }

    @Test
    fun shouldTransitionThroughFSMigrationStages() {
        sut.uploadFailedFiles()

        verifySequence {
            migrationService.transition(MigrationStage.FS_MIGRATION_COPY)
            migrationService.transition(MigrationStage.FS_MIGRATION_COPY_WAIT)
            migrationService.transition(MigrationStage.OFFLINE_WARNING)
        }
    }

    @Test
    fun shouldThrowWhenCannotTransitionToStartStage() {
        every { migrationService.transition(MigrationStage.FS_MIGRATION_COPY) } throws InvalidMigrationStageError("bad transition")

        assertThrows<FileSystemMigrationFailure> { sut.uploadFailedFiles() }
    }

    @Test
    fun shouldThrowWhenUploadFails() {
        every { uploader.upload(any()) } throws FileUploadException("unable to upload")

        assertThrows<FileSystemMigrationFailure> { sut.uploadFailedFiles() }
    }

    private fun setupPreviousReportData() {
        failedPaths.forEach { prevReport.reportFileNotMigrated(FailedFileMigration(it, "bogus reason")) }
        for (i in 0 until foundFiles) {
            prevReport.reportFileFound()
        }
        for (i in 0 until uploadsCommenced) {
            prevReport.reportFileUploadCommenced()
        }
        for (i in 0 until uploadedFiles) {
            prevReport.reportFileUploaded()
        }
        prevReport.setNumberOfFilesDownloaded(filesDownloaded.toLong())
    }

}