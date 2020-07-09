package com.atlassian.migration.datacenter.core.aws

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.event.api.EventPublisher
import com.atlassian.migration.datacenter.analytics.events.MigrationFailedEvent
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.min


open class AwsMigrationServiceWrapper(ao: ActiveObjects?, applicationConfiguration: ApplicationConfiguration?, eventPublisher: EventPublisher?) : AWSMigrationService(ao, applicationConfiguration, eventPublisher), MigrationService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(AwsMigrationServiceWrapper::class.java)
    }

    override fun error(message: String) {
        this.stageSpecificError(message);
    }

    override fun error(e: Throwable) {
        stageSpecificError(e.message!!)
        findFirstOrCreateMigration().stage.exception = e
    }

    private fun stageSpecificError(message: String) {
        logger.debug("Transitioning to stage specific error stage. Message - {}", message)
        val migration = findFirstOrCreateMigration()
        val preErrorTransitionStage = migration.stage
        val now = System.currentTimeMillis() / 1000L
        logger.debug("Stage before error transition - {}", preErrorTransitionStage)

        when {
            PROVISIONING_ERROR.validAncestorStages.contains(preErrorTransitionStage) -> {
                setCurrentStage(migration, PROVISIONING_ERROR)
            }
            FS_MIGRATION_ERROR.validAncestorStages.contains(preErrorTransitionStage) -> {
                setCurrentStage(migration, FS_MIGRATION_ERROR)
            }
            FINAL_SYNC_ERROR.validAncestorStages.contains(preErrorTransitionStage) -> {
                setCurrentStage(migration, FINAL_SYNC_ERROR)
            }

            else -> {
                setCurrentStage(migration, ERROR)
            }
        }

        val context: MigrationContext = migration.context
        // We must truncate the error message to 450 characters so that it fits in the varchar(450) column
        context.setErrorMessage(message.substring(0, min(450, message.length)))
        context.endEpoch = now
        context.save()

        this.eventPublisher.publish(MigrationFailedEvent(this.applicationConfiguration.pluginVersion,
                preErrorTransitionStage, now - context.startEpoch))
    }
}