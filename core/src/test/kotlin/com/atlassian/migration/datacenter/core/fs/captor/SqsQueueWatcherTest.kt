package com.atlassian.migration.datacenter.core.fs.captor

import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.util.function.Supplier

internal class SqsQueueWatcherTest {

    @MockK
    lateinit var migrationService: MigrationService

    @MockK
    lateinit var sqsAsyncClient: SqsAsyncClient

    lateinit var queueWatcher: SqsQueueWatcher

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        queueWatcher = SqsQueueWatcher(Supplier { sqsAsyncClient }, migrationService)
    }

    @Test
    fun shouldTransitionMigrationStateToValidate() {
        every { migrationService.transition(MigrationStage.VALIDATE) } answers {}

        queueWatcher.awaitQueueDrain()

        verify {
            migrationService.transition(MigrationStage.VALIDATE)
        }
    }
}