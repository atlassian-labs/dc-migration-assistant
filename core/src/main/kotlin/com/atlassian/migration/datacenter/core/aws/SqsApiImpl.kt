package com.atlassian.migration.datacenter.core.aws

import com.atlassian.migration.datacenter.core.exceptions.AwsQueueApiUnsuccessfulResponse
import com.atlassian.migration.datacenter.core.exceptions.AwsQueueBadRequestError
import com.atlassian.migration.datacenter.core.exceptions.AwsQueueConnectionException
import com.atlassian.migration.datacenter.core.exceptions.AwsQueueError
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES
import software.amazon.awssdk.services.sqs.model.QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE
import java.util.function.Supplier

class SqsApiImpl(private val sqsClientSupplier: Supplier<SqsAsyncClient>) : SqsApi {

    companion object {
        private val logger = LoggerFactory.getLogger(SqsApiImpl::class.java)
    }

    @Throws(AwsQueueError::class)
    override fun getQueueLength(queueUrl: String): Int {
        when {
            queueUrl.isNullOrBlank() -> {
                throw AwsQueueBadRequestError("Expected Queue URL to be specified")
            }
            else -> {
                val request = GetQueueAttributesRequest
                        .builder()
                        .queueUrl(queueUrl)
                        .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)
                        .build()

                try {

                    val response = sqsClientSupplier.get().getQueueAttributes(request).get()

                    if (response.hasAttributes()) {
                        val attributes = response.attributes()
                        val messageCountInQueue = attributes[APPROXIMATE_NUMBER_OF_MESSAGES]
                        val messageCountInFlight = attributes[APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE]

                        return (messageCountInQueue?.toIntOrNull() ?: 0) + (messageCountInFlight?.toIntOrNull() ?: 0)
                    }
                } catch (ex: Exception) {
                    val errorMessage = "Error while trying to query SQS API"
                    logger.error(errorMessage, ex)
                    throw AwsQueueConnectionException(errorMessage, ex)
                }
                throw AwsQueueApiUnsuccessfulResponse("Unable to retrieve queue attributes from SQS")
            }
        }
    }
}
