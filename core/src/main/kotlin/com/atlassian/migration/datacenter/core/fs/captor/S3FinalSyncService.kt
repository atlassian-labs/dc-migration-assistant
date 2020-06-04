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
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.scheduler.config.JobId
import org.slf4j.LoggerFactory

class S3FinalSyncService(private val migrationRunner: MigrationRunner,
                         private val s3FinalSyncRunner: S3FinalSyncRunner,
                         private val migrationService: MigrationService,
                         private val sqsApi: SqsApi
) {
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
        migrationRunner.abortJobIfPresesnt(getScheduledJobId())

        logger.warn("Aborting running final file sync")

        migrationService.error("Aborted final file sync")
    }

    fun getFinalSyncStatus() : FinalFileSyncStatus {
        val currentContext = migrationService.currentContext
        val migrationQueueUrl = currentContext.migrationQueueUrl

        val itemsInQueue = sqsApi.getQueueLength(migrationQueueUrl)

        return FinalFileSyncStatus(0, itemsInQueue)
    }

    private fun getScheduledJobId(): JobId {
        return JobId.of(s3FinalSyncRunner.key + migrationService.currentMigration.id)
    }
}

class FinalFileSyncStatus(val uploadedFileCount: Int, val enqueuedFileCount: Int)