package com.atlassian.migration.datacenter.core.fs.captor

import com.atlassian.migration.datacenter.core.aws.SqsApi
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SqsQueueWatcherTest {

    @MockK
    lateinit var mockContext: MigrationContext

    @MockK
    lateinit var migrationService: MigrationService

    @MockK
    lateinit var sqsApi: SqsApi

    lateinit var queueWatcher: SqsQueueWatcher

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        queueWatcher = SqsQueueWatcher(sqsApi, migrationService, 1)
    }

    @Test
    fun shouldTransitionMigrationStateToValidate() {
        val migrationQueueUrl = "https://sqs/migrationQueue"

        every { migrationService.currentStage } returns MigrationStage.DB_MIGRATION_EXPORT_WAIT andThen MigrationStage.DB_MIGRATION_UPLOAD andThen MigrationStage.DATA_MIGRATION_IMPORT_WAIT andThen MigrationStage.FINAL_SYNC_WAIT
        every { migrationService.transition(MigrationStage.VALIDATE) } answers {}
        every { migrationService.currentContext } returns mockContext
        every { mockContext.migrationQueueUrl } returns migrationQueueUrl
        every { sqsApi.getQueueLength(migrationQueueUrl) } returns 3 andThen 2 andThen 1 andThen 0

        val isQueueDrained = queueWatcher.awaitQueueDrain()

        Assertions.assertTrue(isQueueDrained, "Expected Queue to be drained, but wasn't")

        verify(exactly = 4) { migrationService.currentStage }
        verify(exactly = 4) { sqsApi.getQueueLength(migrationQueueUrl) }
        verify {
            migrationService.transition(MigrationStage.VALIDATE)
        }
    }
}