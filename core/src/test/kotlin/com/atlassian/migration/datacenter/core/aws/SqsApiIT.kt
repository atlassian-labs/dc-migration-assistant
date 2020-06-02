package com.atlassian.migration.datacenter.core.aws

import cloud.localstack.docker.LocalstackDockerExtension
import cloud.localstack.docker.annotation.LocalstackDockerProperties
import com.atlassian.migration.datacenter.core.exceptions.AwsQueueConnectionException
import com.atlassian.migration.datacenter.core.exceptions.AwsQueueError
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry
import java.net.URI
import java.util.function.Supplier
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Tag("integration")
@ExtendWith(LocalstackDockerExtension::class)
@LocalstackDockerProperties(useSingleDockerContainer = true, services = ["sqs"], imageTag = "0.10.8")
class SqsApiIT {

    companion object {

        val sqsAsyncClient = SqsAsyncClient
                .builder()
                .endpointOverride(URI.create("http://localhost:4576"))
                .credentialsProvider(StubAwsCredentialsProvider())
                .region(Region.AP_SOUTHEAST_2)
                .build()
    }

    lateinit var sqs: SqsApiImpl
    lateinit var queueUrl: String

    @BeforeEach
    internal fun setUp() {
        val createQueueCf = sqsAsyncClient.createQueue(CreateQueueRequest.builder().queueName("sqsApiIntegrationTest").build());
        val createQueueResponse = createQueueCf.get()
        queueUrl = createQueueResponse.queueUrl()

        sqs = SqsApiImpl(Supplier { sqsAsyncClient })
    }

    @Test
    fun shouldRaiseExceptionWhenQueueUrlIsIncorrect() {
        Assertions.assertThrows(AwsQueueConnectionException::class.java) { sqs.getQueueLength("$queueUrl/foo") }
    }

    @Test
    fun shouldGetZeroQueueLengthWhenNoMessagesArePresentInTheQueue() {
        val queueLength = sqs.getQueueLength(queueUrl)
        assertEquals(0, queueLength)
    }

    @AfterEach
    internal fun tearDown() {
        val deleteQueueCf = sqsAsyncClient.deleteQueue(DeleteQueueRequest.builder().queueUrl(queueUrl).build())
        deleteQueueCf.get()
    }

    @Test
    fun shouldGetCorrectQueueLengthWhenMessagesArePresentInTheQueue() {
        val sendMessageBatchCf = sqsAsyncClient.sendMessageBatch(SendMessageBatchRequest.builder().queueUrl(queueUrl).entries(
                SendMessageBatchRequestEntry.builder().id("1").messageBody("{\"path\":\"/foo/bar/1\"}").build(),
                SendMessageBatchRequestEntry.builder().id("2").messageBody("{\"path\":\"/foo/bar/2\"}").build(),
                SendMessageBatchRequestEntry.builder().id("3").messageBody("{\"path\":\"/foo/bar/3\"}").build()
        ).build())
        val sendMessageBatchResponse = sendMessageBatchCf.get()
        assertTrue(sendMessageBatchResponse.hasSuccessful())
        assertEquals(3, sendMessageBatchResponse.successful().size)

        val queueLength = sqs.getQueueLength(queueUrl)
        assertEquals(3, queueLength)
    }
}