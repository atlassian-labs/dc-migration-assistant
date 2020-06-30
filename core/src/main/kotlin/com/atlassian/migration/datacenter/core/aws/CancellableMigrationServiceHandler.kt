package com.atlassian.migration.datacenter.core.aws

import com.atlassian.event.api.EventListener
import com.atlassian.event.api.EventPublisher
import com.atlassian.migration.datacenter.events.MigrationResetEvent
import com.atlassian.migration.datacenter.spi.CancellableMigrationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import java.util.function.Consumer


class CancellableMigrationServiceHandler(private val eventPublisher: EventPublisher,
                                         vararg services: CancellableMigrationService
) : InitializingBean, DisposableBean {
    private val cancellableServices: List<CancellableMigrationService> = listOf(*services)

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        eventPublisher.register(this)
    }

    @EventListener
    fun onMigrationResetEvent(event: MigrationResetEvent) {
        val migrationId = event.migrationId
        logger.info("Cancelling any scheduled jobs for migration {}", migrationId)
        cancellableServices.forEach(Consumer { x: CancellableMigrationService ->
            logger.debug("Cancelling {} scheduled job, if it has been scheduled for migration {}", x.javaClass.simpleName, migrationId)
            x.unscheduleMigration(migrationId)
        })
    }

    @Throws(Exception::class)
    override fun destroy() {
        eventPublisher.unregister(this)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CancellableMigrationServiceHandler::class.java)
    }
}