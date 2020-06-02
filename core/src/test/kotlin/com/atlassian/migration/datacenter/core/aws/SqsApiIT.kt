package com.atlassian.migration.datacenter.core.aws

import cloud.localstack.docker.LocalstackDockerExtension
import cloud.localstack.docker.annotation.LocalstackDockerProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import java.net.URI
import java.util.function.Supplier
import kotlin.test.assertEquals

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
    fun shouldGetZeroQueueLengthWhenNoMessagesArePresentInTheQueue() {
        val queueLength = sqs.getQueueLength(queueUrl)
        assertEquals(0, queueLength)
    }
}