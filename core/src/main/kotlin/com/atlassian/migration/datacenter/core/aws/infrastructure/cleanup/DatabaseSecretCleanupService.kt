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

import com.atlassian.migration.datacenter.core.aws.db.restore.TargetDbCredentialsStorageService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretRequest
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException
import java.time.Instant
import java.util.function.Supplier

class DatabaseSecretCleanupService(
        private val secretsManagerClient: Supplier<SecretsManagerClient>,
        private val targetDbCredentialsStorageService: TargetDbCredentialsStorageService
) : MigrationInfrastructureCleanupService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DatabaseSecretCleanupService::class.java)
    }

    override fun startMigrationInfrastructureCleanup(): Boolean {
        val client = secretsManagerClient.get()

        val secretName = targetDbCredentialsStorageService.secretName
        logger.info("deleting database secret: $secretName")

        return try {
            val res = client.deleteSecret(
                    DeleteSecretRequest
                            .builder()
                            .secretId(secretName)
                            .forceDeleteWithoutRecovery(true)
                            .build())
            val result = res.sdkHttpResponse().isSuccessful
            if (!result) {
                logger.error("unable to delete database secret")
            }
            result
        } catch (e: ResourceNotFoundException) {
            logger.info("database secret does not exist, no need to delete")
            true
        } catch (e: SdkException) {
            logger.error("error when deleting database secret", e)
            false
        }
    }

    override fun getMigrationInfrastructureCleanupStatus(): InfrastructureCleanupStatus {
        val client = secretsManagerClient.get()

        val secretName = targetDbCredentialsStorageService.secretName

        logger.info("getting status of database secret $secretName cleanup")

        return try {
            val res = client.describeSecret(DescribeSecretRequest.builder().secretId(secretName).build())
            when {
                res.deletedDate() <= Instant.now() -> InfrastructureCleanupStatus.CLEANUP_COMPLETE
                res.deletedDate() > Instant.now() -> InfrastructureCleanupStatus.CLEANUP_IN_PROGRESS
                else -> InfrastructureCleanupStatus.CLEANUP_NOT_STARTED
            }
        } catch (e: ResourceNotFoundException) {
            logger.info("secret does not exist, cleanup must have completed")
            InfrastructureCleanupStatus.CLEANUP_COMPLETE
        } catch (e: Exception) {
            // Assume cleanup is not started until we can get a successful result
            logger.error("error getting secret cleanup status", e)
            InfrastructureCleanupStatus.CLEANUP_NOT_STARTED
        }
    }
}