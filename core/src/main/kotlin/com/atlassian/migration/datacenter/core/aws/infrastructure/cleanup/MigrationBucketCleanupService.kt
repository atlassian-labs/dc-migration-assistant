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

import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.s3.S3Client
import java.util.function.Supplier

class MigrationBucketCleanupService(private val migrationService: MigrationService, private val s3ClientSupplier: Supplier<S3Client>) : MigrationInfrastructureCleanupService {

    private var failedToEmpty = false

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MigrationBucketCleanupService::class.java)
    }

    override fun startMigrationInfrastructureCleanup(): Boolean {
        val bucket = migrationService.currentContext.migrationBucketName

        if (bucket.isNullOrEmpty()) {
            return true
        }

        val client = s3ClientSupplier.get()

        var response = client.listObjectsV2 { it.bucket(bucket) }
        var contents = response.contents()
        var continuation = response.continuationToken()
        var retryCount = 0

        while (contents.size != 0) {
            val lastSize = contents.size
            contents.forEach { obj ->
                client.deleteObject { builder -> builder.bucket(bucket).key(obj.key()) }
            }
            response = client.listObjectsV2 { it.bucket(bucket).continuationToken(continuation) }
            contents = response.contents()
            continuation = response.continuationToken()

            if (lastSize == contents.size) {
                if (retryCount >= 3) {
                    logger.error("contents of bucket has not changed after 3 attempts at deleting everything, something must be wrong")
                    failedToEmpty = true
                    break
                }
                retryCount++
            }
        }

        return true
    }

    override fun getMigrationInfrastructureCleanupStatus(): InfrastructureCleanupStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}