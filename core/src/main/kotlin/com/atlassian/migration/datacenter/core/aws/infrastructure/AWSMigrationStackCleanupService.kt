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

package com.atlassian.migration.datacenter.core.aws.infrastructure

import com.atlassian.migration.datacenter.core.aws.CfnApi
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService

class AWSMigrationStackCleanupService(private val cfnApi: CfnApi, private val migrationService: MigrationService) : MigrationInfrastructureCleanupService {
    override fun scheduleMigrationInfrastructureCleanup(): Boolean {
        val context = migrationService.currentContext
        val migrationStack = context.helperStackDeploymentId

        return try {
            cfnApi.deleteStack(migrationStack)
            true
        } catch (e: InfrastructureDeploymentError) {
            false
        }
    }

    override fun getMigrationInfrastructureCleanupStatus(): InfrastructureCleanupStatus {
        TODO("not implemented yet")
    }
}