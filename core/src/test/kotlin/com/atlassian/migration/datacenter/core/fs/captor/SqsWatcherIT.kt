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

import cloud.localstack.docker.LocalstackDockerExtension
import cloud.localstack.docker.annotation.LocalstackDockerProperties
import com.atlassian.migration.datacenter.core.aws.SqsApiImpl
import com.atlassian.migration.datacenter.core.aws.StubAwsCredentialsProvider
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.*
import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Tag("integration")
@ExtendWith(LocalstackDockerExtension::class)
@LocalstackDockerProperties(useSingleDockerContainer = true, services = ["sqs"], imageTag = "0.10.8")
class SqsWatcherIT {

    companion object {
        val sqsAsyncClient: SqsAsyncClient = SqsAsyncClient
                .builder()
                .endpointOverride(URI.create("http://localhost:4576"))
                .credentialsProvider(StubAwsCredentialsProvider())
                .region(Region.AP_SOUTHEAST_2)
                .build()
    }

    lateinit var migrationService: MigrationService
    lateinit var mockContext: MigrationContext
    lateinit var queueUrl: String
    lateinit var sqsWatcher: QueueWatcher

    @BeforeEach
    internal fun setUp() {
        val createQueueCf = sqsAsyncClient.createQueue(CreateQueueRequest.builder().queueName("sqsWatcherIntegrationTest").build());
        val createQueueResponse = createQueueCf.get()
        queueUrl = createQueueResponse.queueUrl()

        migrationService = mockkClass(MigrationService::class)
        mockContext = mockkClass(MigrationContext::class)
        sqsWatcher = SqsQueueWatcher(SqsApiImpl(Supplier { sqsAsyncClient }), migrationService, 1)
    }

    @AfterEach
    internal fun tearDown() {
        val deleteQueueCf = sqsAsyncClient.deleteQueue(DeleteQueueRequest.builder().queueUrl(queueUrl).build())
        deleteQueueCf.get()
    }

    @Test
    fun shouldGetCorrectQueueLengthWhenMessagesArePresentInTheQueue() {
        every { migrationService.currentStage } returns MigrationStage.FINAL_SYNC_WAIT
        every { migrationService.currentContext } returns mockContext
        every { mockContext.migrationQueueUrl } returns queueUrl
        every { migrationService.transition(MigrationStage.VALIDATE) } answers {}

        val sendMessageBatchCf = sqsAsyncClient.sendMessageBatch(SendMessageBatchRequest.builder().queueUrl(queueUrl).entries(
                SendMessageBatchRequestEntry.builder().id("1").messageBody("{\"path\":\"/foo/bar/1\"}").build(),
                SendMessageBatchRequestEntry.builder().id("2").messageBody("{\"path\":\"/foo/bar/2\"}").build(),
                SendMessageBatchRequestEntry.builder().id("3").messageBody("{\"path\":\"/foo/bar/3\"}").build()
        ).build())

        val sendMessageBatchResponse = sendMessageBatchCf.get()
        assertTrue(sendMessageBatchResponse.hasSuccessful())
        assertEquals(3, sendMessageBatchResponse.successful().size)

        purgeQueueAsync()

        val awaitQueueDrain = sqsWatcher.awaitQueueDrain()

        Assertions.assertTrue(awaitQueueDrain)
        verify { migrationService.transition(MigrationStage.VALIDATE) }
    }

    private fun purgeQueueAsync() {

        Executors.newSingleThreadScheduledExecutor().schedule({
            val purgeQueueResponse = sqsAsyncClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build()).get()
            assertTrue { purgeQueueResponse.sdkHttpResponse().isSuccessful }
        }, 20, TimeUnit.SECONDS)
    }
}