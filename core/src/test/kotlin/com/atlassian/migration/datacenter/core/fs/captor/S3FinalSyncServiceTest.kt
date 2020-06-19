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