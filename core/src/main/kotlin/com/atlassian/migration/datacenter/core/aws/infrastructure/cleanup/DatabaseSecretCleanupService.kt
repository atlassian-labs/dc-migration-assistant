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
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException
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
            res.sdkHttpResponse().isSuccessful
        } catch (e: ResourceNotFoundException) {
            true
        } catch (e: SdkException) {
            false
        }
    }

    override fun getMigrationInfrastructureCleanupStatus(): InfrastructureCleanupStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}