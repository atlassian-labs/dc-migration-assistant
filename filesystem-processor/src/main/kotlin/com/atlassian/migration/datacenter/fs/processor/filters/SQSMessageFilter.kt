package com.atlassian.migration.datacenter.fs.processor.filters

import org.springframework.integration.annotation.Filter
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.messaging.Message

@MessageEndpoint
class SQSMessageFilter {

    @Filter(inputChannel = "inboundChannel", outputChannel = "filteredChannel", discardChannel = "loggingChannel")
    fun filter(message: Message<String>): Boolean {
        val body = message.payload
        val testEvent = body.contains("TestEvent", true)
        return !testEvent
    }

}

