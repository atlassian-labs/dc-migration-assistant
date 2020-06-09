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

package com.atlassian.migration.datacenter.core.fs.captor

import com.atlassian.jira.config.util.JiraHome
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig
import com.atlassian.migration.datacenter.core.fs.S3Uploader
import com.atlassian.migration.datacenter.core.fs.jira.listener.JiraIssueAttachmentListener
import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport
import com.atlassian.migration.datacenter.core.util.MigrationJobRunner
import com.atlassian.scheduler.JobRunnerRequest
import com.atlassian.scheduler.JobRunnerResponse
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier

class S3FinalSyncRunner(
        private val attachmentSyncManager: AttachmentSyncManager,
        private val client: Supplier<S3AsyncClient>,
        private val home: JiraHome,
        private val migrationHelperDeploymentService: AWSMigrationHelperDeploymentService,
        private val queueWatcher: QueueWatcher,
        private val attachmentListener: JiraIssueAttachmentListener)
    : MigrationJobRunner {

    companion object {
        private val log = LoggerFactory.getLogger(com.atlassian.migration.datacenter.core.db.DatabaseMigrationJobRunner::class.java)
    }

    private val isRunning = AtomicBoolean(false)

    override fun getKey(): String {
        return S3FinalSyncRunner::class.java.name
    }

    override fun runJob(request: JobRunnerRequest): JobRunnerResponse? {
        if (!isRunning.compareAndSet(false, true)) {
            return JobRunnerResponse.aborted("Database migration job is already running")
        }

        log.info("Stopping attachment event listener. Attachments created from this point onwards will not be migrated.")
        attachmentListener.stop()

        val config = S3UploadConfig(migrationHelperDeploymentService.migrationS3BucketName, client.get(), home.home.toPath())
        val report = DefaultFileSystemMigrationReport()
        val uploader = S3Uploader(config, report)

        log.info("Starting final file sync migration job")
        val finalSyncUploader = S3FinalFileSync(attachmentSyncManager, uploader)
        finalSyncUploader.uploadCapturedFiles()

        if (report.failedFiles.isNotEmpty()) {
            log.error("Some files failed to upload during final sync")
            report.failedFiles.forEach {
                log.error("${it.filePath} - ${it.reason}")
            }
        }

        val queueDrainResult = queueWatcher.awaitQueueDrain()

        if (queueDrainResult) {
            log.debug("Processed all items from remote queue.")
        } else {
            log.error("Encountered error(s) while processing items from remote queue.")
        }

        log.info("Finished final file sync migration job")

        return JobRunnerResponse.success("Final file sync migration complete")
    }
}