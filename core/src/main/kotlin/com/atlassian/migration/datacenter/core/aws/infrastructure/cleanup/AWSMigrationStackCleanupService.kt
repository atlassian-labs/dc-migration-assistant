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

import com.atlassian.migration.datacenter.core.aws.CfnApi
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException
import kotlin.math.log

class AWSMigrationStackCleanupService(private val cfnApi: CfnApi, private val migrationService: MigrationService) : MigrationInfrastructureCleanupService {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AWSMigrationStackCleanupService::class.java)
    }

    override fun startMigrationInfrastructureCleanup(): Boolean {
        val migrationStack = getMigrationStackName()

        logger.info("Got request to cleanup migration stack $migrationStack")

        return try {
            cfnApi.deleteStack(migrationStack)
            logger.info("Successfully sent request to delete migration stack")
            true
        } catch (e: InfrastructureDeploymentError) {
            logger.error("Error cleaning up migration stack", e)
            false
        }
    }

    override fun getMigrationInfrastructureCleanupStatus(): InfrastructureCleanupStatus {
        val migrationStack = getMigrationStackName()

        logger.info("Request for cleanup status of stack $migrationStack")

        return try {
            when (cfnApi.getStatus(migrationStack)) {
                InfrastructureDeploymentState.DELETE_COMPLETE -> InfrastructureCleanupStatus.CLEANUP_COMPLETE
                InfrastructureDeploymentState.DELETE_IN_PROGRESS -> InfrastructureCleanupStatus.CLEANUP_IN_PROGRESS
                InfrastructureDeploymentState.DELETE_FAILED -> InfrastructureCleanupStatus.CLEANUP_FAILED
                else -> InfrastructureCleanupStatus.CLEANUP_NOT_STARTED
            }
        } catch (e: StackInstanceNotFoundException) {
            InfrastructureCleanupStatus.CLEANUP_COMPLETE
        }
    }

    private fun getMigrationStackName(): String {
        val context = migrationService.currentContext
        return context.helperStackDeploymentId
    }
}