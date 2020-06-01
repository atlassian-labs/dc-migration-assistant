package com.atlassian.migration.datacenter.core.aws

import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName
import software.amazon.awssdk.services.sqs.model.QueueAttributeName.*
import java.util.function.Supplier

class SqsApiImpl(private val sqsClientSupplier: Supplier<SqsAsyncClient>) : SqsApi {

    companion object {
        private val logger = LoggerFactory.getLogger(SqsApiImpl::class.java)
    }

    override fun getQueueLength(queueName: String): Int? {
        val sqsAsyncClient = this.sqsClientSupplier.get()
        val request = GetQueueAttributesRequest
                .builder()
                .queueUrl(queueName)
                .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)
                .build()

        try {
            val response = sqsAsyncClient.getQueueAttributes(request).get()

            if (response.hasAttributes()) {
                val attributes = response.attributes()
                val messageCountInQueue = attributes[APPROXIMATE_NUMBER_OF_MESSAGES]
                val messageCountInFlight = attributes[APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE]

                return (messageCountInQueue?.toIntOrNull() ?: 0) + (messageCountInFlight?.toIntOrNull() ?: 0)
            }
        } catch (ex: Exception) {
            logger.error("Error while trying to query SQS API", ex)
            return null
        }
        return null
    }
}