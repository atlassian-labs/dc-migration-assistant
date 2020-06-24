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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

const val queueUrl = "https://sqs/account/queues/foo"
const val dlQueUrl = "https://sqs/account/queus/deadletter"

internal class S3FinalSyncServiceTest {

    @MockK
    lateinit var migrationRunner: MigrationRunner
    @MockK
    lateinit var jobRunner: S3FinalSyncRunner
    @MockK
    lateinit var migrationService: MigrationService

    @MockK
    lateinit var migrationContext: MigrationContext
    @MockK
    lateinit var sqsApi: SqsApi
    @MockK
    lateinit var attachmentSyncManager: AttachmentSyncManager

    @InjectMockKs
    lateinit var sut: S3FinalSyncService

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        every { migrationService.currentContext } returns migrationContext
        every { migrationContext.migrationQueueUrl } returns queueUrl
        every { migrationContext.migrationDLQueueUrl } returns dlQueUrl
    }


    @Test
    fun shouldGetFinalSyncStatus() {
        givenElementsInMigrationQueueIs(42)
        givenElementsInDeadLetterQueueIs(0)
        givenFilesCapturedIs(21)

        val finalSyncStatus = sut.getFinalSyncStatus()

        assertEquals(42, finalSyncStatus.enqueuedFileCount)
        assertEquals(21, finalSyncStatus.uploadedFileCount)
        assertEquals(0, finalSyncStatus.failedFileCount)

        verify {
            sqsApi.getQueueLength(queueUrl)
        }
    }

    @Test
    fun shouldGetErrorsInFinalSync() {
        givenFilesCapturedIs(40)
        givenElementsInMigrationQueueIs(0)
        givenElementsInDeadLetterQueueIs(40)

        val finalSyncStatus = sut.getFinalSyncStatus()

        assertEquals(40, finalSyncStatus.uploadedFileCount)
        assertEquals(0, finalSyncStatus.enqueuedFileCount)
        assertEquals(40, finalSyncStatus.failedFileCount)
    }

    private fun givenFilesCapturedIs(numFiles: Int) {
        every { attachmentSyncManager.capturedAttachmentCountForCurrentMigration } returns numFiles
    }

    private fun givenElementsInMigrationQueueIs(numElements: Int) {
        every { sqsApi.getQueueLength(queueUrl) } returns numElements
    }

    private fun givenElementsInDeadLetterQueueIs(numElements: Int) {
        every { sqsApi.getQueueLength(dlQueUrl) } returns numElements
    }
}