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

import com.atlassian.migration.datacenter.core.util.MigrationJobRunner
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.scheduler.JobRunnerRequest
import com.atlassian.scheduler.JobRunnerResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.NoSuchBucketException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier

class BucketCleanupJobRunner(private val migrationService: MigrationService, private val s3ClientSupplier: Supplier<S3Client>): MigrationJobRunner {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(MigrationBucketCleanupService::class.java)
        val KEY = BucketCleanupJobRunner::class.java.toString()
        val maxRetries = 3
    }

    val isRunning = AtomicBoolean(false)
    val failedToEmpty = AtomicBoolean(false)


    private fun doCleanup(): Boolean {

        val bucket = migrationService.currentContext.migrationBucketName

        if (bucket.isNullOrEmpty()) {
            logger.info("Bucket is not in context, no cleanup necessary")
            return true
        }

        val client = s3ClientSupplier.get()

        var response = client.listObjectsV2 { it.bucket(bucket) }
        var contents = response.contents()
        var continuation = response.continuationToken()
        var retryCount = 0

        logger.info("Deleting all objects in migration bucket")
        while (contents.size != 0) {
            val lastSize = contents.size
            contents.forEach { obj ->
                client.deleteObject { builder -> builder.bucket(bucket).key(obj.key()) }
            }
            response = client.listObjectsV2 { it.bucket(bucket).continuationToken(continuation) }
            contents = response.contents()
            continuation = response.continuationToken()

            if (lastSize == contents.size) {
                if (retryCount >= maxRetries) {
                    logger.error("Contents of bucket has not changed after 3 attempts at deleting everything, something must be wrong")
                    failedToEmpty.set(true)
                    break
                }
                retryCount++
            }
        }
        logger.info("All objects in bucket deleted")

        if (!failedToEmpty.get()) {
            logger.info("Deleting migration bucket")
            try {
                client.deleteBucket { it.bucket(bucket) }
            } catch (e: NoSuchBucketException) {
                logger.info("Bucket already doesn't exist...")
            }
        } else {
            logger.info("Bucket emptying failed. Not deleting migration bucket")
        }

        logger.info("Migration bucket cleanup complete")

        return true
    }


    override fun runJob(request: JobRunnerRequest): JobRunnerResponse? {
        logger.info("Got request to cleanup migration bucket")

        if (!isRunning.compareAndSet(false, true)) {
            return JobRunnerResponse.aborted("Migration bucket job is already running")
        }

        isRunning.set(true)
        val result = doCleanup()
        isRunning.set(false)

        return if (result)
            JobRunnerResponse.success("Bucket cleanup complete")
        else
            JobRunnerResponse.failed("Cleanup failed")
    }

    override fun getKey(): String {
        return KEY
    }

}