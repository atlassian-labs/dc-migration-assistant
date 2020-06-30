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

import com.atlassian.migration.datacenter.core.db.DatabaseMigrationJobRunner
import com.atlassian.migration.datacenter.core.util.MigrationRunner
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import com.atlassian.scheduler.config.JobId
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.NoSuchBucketException
import java.util.function.Supplier

class MigrationBucketCleanupService(private val migrationService: MigrationService,
                                    private val migrationRunner: MigrationRunner,
                                    private val s3ClientSupplier: Supplier<S3Client>)
    : MigrationInfrastructureCleanupService
{

    private val runner = BucketCleanupJobRunner(migrationService, s3ClientSupplier)

    override fun startMigrationInfrastructureCleanup(): Boolean {
        val jobId = JobId.of(BucketCleanupJobRunner.KEY + migrationService.currentMigration.id)

        val result: Boolean = migrationRunner.runMigration(jobId, runner)

        if (!result) {
            migrationService.error("Unable to start database migration job.")
        }
        return result
    }

    override fun getMigrationInfrastructureCleanupStatus(): InfrastructureCleanupStatus {
        return when {
            runner.isRunning.get() -> InfrastructureCleanupStatus.CLEANUP_IN_PROGRESS
            runner.failedToEmpty.get() -> InfrastructureCleanupStatus.CLEANUP_FAILED
            else -> {
                val bucket = migrationService.currentContext.migrationBucketName
                val client = s3ClientSupplier.get()
                return try {
                    client.headBucket {it.bucket(bucket)}
                    InfrastructureCleanupStatus.CLEANUP_NOT_STARTED
                } catch (e: NoSuchBucketException) {
                    InfrastructureCleanupStatus.CLEANUP_COMPLETE
                }
            }
        }
    }
}