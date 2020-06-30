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

import com.atlassian.migration.datacenter.core.aws.SqsApi
import com.atlassian.migration.datacenter.core.util.MigrationRunner
import com.atlassian.migration.datacenter.spi.CancellableMigrationService
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.scheduler.config.JobId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean

class S3FinalSyncService(private val migrationRunner: MigrationRunner,
                         private val s3FinalSyncRunner: S3FinalSyncRunner,
                         private val migrationService: MigrationService,
                         private val sqsApi: SqsApi,
                         private val attachmentSyncManager: AttachmentSyncManager
) : CancellableMigrationService, DisposableBean {
    companion object {
        private val logger = LoggerFactory.getLogger(S3FinalSyncService::class.java)
    }

    fun scheduleSync(): Boolean {

        val jobId = getScheduledJobId()

        val result = migrationRunner.runMigration(jobId, s3FinalSyncRunner)

        if (!result) {
            logger.error("Unable to start s3 final sync migration job.")
        }
        return result
    }

    fun abortMigration() {
        // We always try to remove scheduled job if the system is in inconsistent state
        migrationRunner.abortJobIfPresent(getScheduledJobId())

        migrationService.transition(MigrationStage.FINAL_SYNC_ERROR)

        logger.warn("Aborting running final file sync")
    }

    fun getFinalSyncStatus() : FinalFileSyncStatus {
        val currentContext = migrationService.currentContext
        val migrationQueueUrl = currentContext.migrationQueueUrl
        val uploadedFileCount =  attachmentSyncManager.capturedAttachmentCountForCurrentMigration

        val itemsInQueue = sqsApi.getQueueLength(migrationQueueUrl)
        val itemsFailedToDownload = sqsApi.getQueueLength(currentContext.migrationDLQueueUrl)

        return FinalFileSyncStatus(uploadedFileCount, itemsInQueue, itemsFailedToDownload)
    }

    override fun unscheduleMigration(migrationId: Int): Boolean {
        val jobId = getScheduledJobIdForMigration(migrationId)
        return migrationRunner.abortJobIfPresent(jobId);
    }

    private fun getScheduledJobId(): JobId {
        return getScheduledJobIdForMigration(migrationService.currentMigration.id)
    }

    private fun getScheduledJobIdForMigration(migrationId: Int): JobId {
        return JobId.of(s3FinalSyncRunner.key + migrationId)
    }

    @Throws(Exception::class)
    override fun destroy() {
        val jobId = getScheduledJobId()
        this.migrationRunner.abortJobIfPresent(jobId)
    }
}

class FinalFileSyncStatus(val uploadedFileCount: Int, val enqueuedFileCount: Int, val failedFileCount: Int)