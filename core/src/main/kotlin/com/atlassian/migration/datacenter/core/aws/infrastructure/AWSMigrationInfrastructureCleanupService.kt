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

import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AWSMigrationInfrastructureCleanupService : MigrationInfrastructureCleanupService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(AWSMigrationInfrastructureCleanupService::class.java)
    }

    override fun getMigrationInfrastructureCleanupStatus(): InfrastructureCleanupStatus {
        logger.info("querying state of AWS resource cleanup")

        return InfrastructureCleanupStatus.CLEANUP_COMPLETE
    }

    override fun scheduleMigrationInfrastructureCleanup(): Boolean {
        logger.info("cleaning up AWS migration infrastructure")
        return true
    }
}