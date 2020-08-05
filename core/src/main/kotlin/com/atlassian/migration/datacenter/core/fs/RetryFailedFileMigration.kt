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

import com.atlassian.migration.datacenter.core.util.UploadQueue
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.FileSystemMigrationFailure
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

class RetryFailedFileMigration(
        private val reportManager: FileSystemMigrationReportManager,
        private val uploaderFactory: UploaderFactory,
        private val fsMigrationService: FilesystemMigrationService,
        private val migrationService: MigrationService
) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(RetryFailedFileMigration::class.java)
    }

    @Throws(InvalidMigrationStageError::class)
    fun uploadFailedFiles() {
        try {
            log.debug("[FS Retry] Aborting current file system migration, if there is a migration in progress")
            fsMigrationService.abortMigration()
        } catch (e: InvalidMigrationStageError) {
            throw FileSystemMigrationFailure("[FS Retry] Error aborting fs migration", e)
        }

        try {
            log.debug("[FS Retry] Transitioning stage to File system start stage")
            migrationService.transition(MigrationStage.FS_MIGRATION_COPY)
        } catch (e: InvalidMigrationStageError) {
            throw FileSystemMigrationFailure("[FS Retry] Error performing retry state transition", e)
        }

        val report = reportManager.getCurrentReport(ReportType.Filesystem) ?: throw Error("No report")
        val newReport = reportManager.resetReport(ReportType.Filesystem)

        val uploadQueue = UploadQueue<Path>(report.failedFiles.size)

        report.failedFiles.forEach {
            uploadQueue.put(it.filePath)
            newReport.reportFileFound()
        }

        migrationService.transition(MigrationStage.FS_MIGRATION_COPY_WAIT)

        val uploader = uploaderFactory.newUploader(report)

        uploader.upload(uploadQueue)

        migrationService.transition(MigrationStage.OFFLINE_WARNING)
    }

}