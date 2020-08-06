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
package com.atlassian.migration.datacenter.core.aws.db.restore

import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest
import java.util.*
import java.util.function.Supplier

/**
 * Service for managing credentials for the target database in the migrated environment.
 * The migration should store the target database password with this service. It will be stored securely.
 * The database username is managed as it is constant in Quick Start environments.
 */
class TargetDbCredentialsStorageService(private val clientFactory: Supplier<SecretsManagerClient>, private val migrationService: MigrationService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Stores the given database password in AWS [Secrets Manager](https://aws.amazon.com/secrets-manager/)
     * to be used later to restore the database. Will be stored under the key:
     * com.atlassian.migration.db.target.[migration_deployment_id].applicationPassword
     *
     * @param password the database password
     * @throws NullPointerException if the password is null
     * @see MigrationContext.getApplicationDeploymentId
     */
    fun storeCredentials(password: String) {
        Objects.requireNonNull(password)
        val name = secretName
        logger.info("Storing secret $name")
        val client = clientFactory.get()
        val request = CreateSecretRequest.builder() /*
                The secret is named like this because it makes it easier for the migration stack to download
                the secret. This allows the migration stack to use "atl-${AWS::StackName}-app-rds-password" as the
                secret name. The migration stack name is deterministic given the application stack name
                (see AWSMigrationHelperDeploymentService#deployMigrationInfrastructure) so we use it here even though
                it hasn't been deployed yet.
                */
                .name(name)
                .secretString(password)
                .description("Password for the application user in you new AWS deployment")
                .build()
        val response = client.createSecret(request)
        val httpResponse = response.sdkHttpResponse()
        if (!httpResponse.isSuccessful) {
            val errorMessage = "Unable to store target database password with AWS secrets manager"
            if (httpResponse.statusText().isPresent) {
                throw DatabaseMigrationFailure(errorMessage + ": " + httpResponse.statusText().get())
            }
            throw DatabaseMigrationFailure(errorMessage)
        }
    }

    val secretName: String
        get() {
            val context = migrationService.currentContext
            return String.format("atl-%s-migration-app-rds-password", context.applicationDeploymentId)
        }
}