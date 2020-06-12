package com.atlassian.migration.datacenter.core.fs.captor

import com.atlassian.migration.datacenter.core.aws.SqsApi
import com.atlassian.migration.datacenter.core.util.MigrationRunner
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.verifyAll
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class S3FinalSyncServiceTest {

    @MockK lateinit var  migrationRunner : MigrationRunner
    @MockK lateinit var  jobRunner: S3FinalSyncRunner
    @MockK lateinit var  migrationService: MigrationService

    @MockK lateinit var  migrationContext: MigrationContext
    @MockK lateinit var  sqsApi: SqsApi
    @MockK lateinit var  attachmentSyncManager: AttachmentSyncManager

    @InjectMockKs
    lateinit var sut: S3FinalSyncService

    @BeforeEach
    fun init() = MockKAnnotations.init(this)

    @Test
    fun shouldGetFinalSyncStatus() {
        val queueUrl = "https://sqs/account/queues/foo"

        every { migrationService.currentContext } returns migrationContext
        every { migrationContext.migrationQueueUrl } returns queueUrl
        every { sqsApi.getQueueLength(queueUrl) } returns 42
        every { attachmentSyncManager.capturedAttachmentCountForCurrentMigration } returns 21

        val finalSyncStatus = sut.getFinalSyncStatus()

        assertEquals(42, finalSyncStatus.enqueuedFileCount)
        assertEquals(21, finalSyncStatus.uploadedFileCount)

        verify {
            sqsApi.getQueueLength(queueUrl)
        }
    }
}