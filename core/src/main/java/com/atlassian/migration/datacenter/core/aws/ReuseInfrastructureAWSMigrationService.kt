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

package com.atlassian.migration.datacenter.core.aws

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.event.api.EventPublisher
import com.atlassian.migration.datacenter.analytics.events.MigrationCreatedEvent
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration
import com.atlassian.migration.datacenter.dto.Migration
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ReuseInfrastructureAWSMigrationService
(
        ao: ActiveObjects,
        applicationConfiguration: ApplicationConfiguration,
        eventPublisher: EventPublisher
) : AwsMigrationServiceWrapper
(
        ao,
        applicationConfiguration,
        eventPublisher)
{

    companion object {
        val log: Logger = LoggerFactory.getLogger(ReuseInfrastructureAWSMigrationService::class.java)
    }

    /**
     * Restarts the migration from the FS copy stage, retaining all deployment parameters
     */
    override fun resetMigration() {
        log.info("resetting migration and retaining infrastructure")

        val context = currentContext

        super.resetMigration()

        val migration = super.createMigration()

        migration.stage = MigrationStage.FS_MIGRATION_COPY
        migration.save()

        val newContext = migration.context

        newContext.applicationDeploymentId = context.applicationDeploymentId
        newContext.helperStackDeploymentId = context.helperStackDeploymentId
        newContext.serviceUrl = context.serviceUrl

        newContext.deploymentMode = context.deploymentMode
        newContext.deploymentState = InfrastructureDeploymentState.CREATE_COMPLETE

        newContext.migrationDLQueueUrl = context.migrationDLQueueUrl
        newContext.migrationBucketName = context.migrationBucketName
        newContext.migrationQueueUrl = context.migrationQueueUrl
        newContext.migrationStackAsgIdentifier = context.migrationStackAsgIdentifier
        newContext.fsRestoreSsmDocument = context.fsRestoreSsmDocument
        newContext.fsRestoreStatusSsmDocument = context.fsRestoreStatusSsmDocument
        newContext.rdsRestoreSsmDocument = context.rdsRestoreSsmDocument

        newContext.save()

        eventPublisher.publish(MigrationCreatedEvent(applicationConfiguration.pluginVersion))
    }

    override fun finishCurrentMigration() {
        resetMigration()
    }

}