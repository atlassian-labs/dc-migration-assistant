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

import com.atlassian.migration.datacenter.core.exceptions.AwsQueueApiUnsuccessfulResponse
import com.atlassian.migration.datacenter.core.exceptions.AwsQueueBadRequestError
import com.atlassian.migration.datacenter.core.exceptions.AwsQueueConnectionException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse
import software.amazon.awssdk.services.sqs.model.QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES
import software.amazon.awssdk.services.sqs.model.QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.function.Supplier

@ExtendWith(MockKExtension::class)
internal class SqsApiImplTest {

    @MockK
    lateinit var sqsAsyncClient: SqsAsyncClient

    lateinit var sqs: SqsApiImpl

    @BeforeEach
    internal fun setUp() {
        sqs = SqsApiImpl(Supplier { sqsAsyncClient })
    }

    @Test
    fun shouldGetApproximateLengthOfMessagesInAQueue() {
        val queueUrl = "https://sqs/foo"
        every {
            sqsAsyncClient.getQueueAttributes(
                    GetQueueAttributesRequest.builder()
                            .queueUrl(queueUrl)
                            .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)
                            .build())
        } returns completedFuture(
                GetQueueAttributesResponse
                        .builder()
                        .attributes(
                                mapOf(APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE to "2",
                                        APPROXIMATE_NUMBER_OF_MESSAGES to "40")
                        )
                        .build()
        )
        val queueLength = sqs.getQueueLength(queueUrl)
        assertEquals(42, queueLength)
    }

    @Test
    fun shouldGetApproximateLengthOfMessagesInAQueueWhenNoMessagesAreEnqueuedButMessagesAreInFlight() {
        val queueUrl = "https://sqs/foo"
        every {
            sqsAsyncClient.getQueueAttributes(
                    GetQueueAttributesRequest.builder()
                            .queueUrl(queueUrl)
                            .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)
                            .build())
        } returns completedFuture(
                GetQueueAttributesResponse
                        .builder()
                        .attributes(
                                mapOf(APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE to "42",
                                        APPROXIMATE_NUMBER_OF_MESSAGES to "0")
                        )
                        .build()
        )
        val queueLength = sqs.getQueueLength(queueUrl)
        assertEquals(42, queueLength)
    }

    @Test
    fun shouldGetApproximateLengthOfMessagesInAQueueWhenNoMessagesAreInFlightButMessagesAreEnqueued() {
        val queueUrl = "https://sqs/foo"
        every {
            sqsAsyncClient.getQueueAttributes(
                    GetQueueAttributesRequest.builder()
                            .queueUrl(queueUrl)
                            .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)
                            .build())
        } returns completedFuture(
                GetQueueAttributesResponse
                        .builder()
                        .attributes(
                                mapOf(APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE to "0",
                                        APPROXIMATE_NUMBER_OF_MESSAGES to "42")
                        )
                        .build()
        )
        val queueLength = sqs.getQueueLength(queueUrl)
        assertEquals(42, queueLength)
    }

    @Test
    fun shouldRaiseExceptionWhenQueueAttributesCannotBeRetrieved() {
        val queueUrl = "https://sqs/bar"

        every {
            sqsAsyncClient.getQueueAttributes(
                    GetQueueAttributesRequest.builder()
                            .queueUrl(queueUrl)
                            .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)
                            .build())
        } returns completedFuture(GetQueueAttributesResponse.builder().build())


        Assertions.assertThrows(AwsQueueApiUnsuccessfulResponse::class.java) { sqs.getQueueLength(queueUrl) }
    }

    @Test
    fun shouldRaiseExceptionWhenQueueUrlIsEmpty() {
        Assertions.assertThrows(AwsQueueBadRequestError::class.java) { sqs.getQueueLength("") }
    }

    @Test
    fun shouldRaiseExceptionWhenQueueSQSApiCompletesExceptionally() {
        val queueUrl = "https://sqs/bar"
        val errorCompletableFuture = CompletableFuture<GetQueueAttributesResponse>()
        errorCompletableFuture.cancel(true)

        every {
            sqsAsyncClient.getQueueAttributes(
                    GetQueueAttributesRequest.builder()
                            .queueUrl(queueUrl)
                            .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)
                            .build())
        } returns errorCompletableFuture

        Assertions.assertThrows(AwsQueueConnectionException::class.java) { sqs.getQueueLength(queueUrl) }
    }
}