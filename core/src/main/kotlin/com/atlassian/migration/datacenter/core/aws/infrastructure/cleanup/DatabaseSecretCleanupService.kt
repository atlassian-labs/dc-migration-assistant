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
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import java.util.function.Supplier

class DatabaseSecretCleanupService(
        private val secretsManagerClient: () -> SecretsManagerClient,
        private val migrationService: MigrationService,
        private val targetDbCredentialsStorageService: TargetDbCredentialsStorageService
) : MigrationInfrastructureCleanupService {
    override fun startMigrationInfrastructureCleanup(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMigrationInfrastructureCleanupStatus(): InfrastructureCleanupStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}