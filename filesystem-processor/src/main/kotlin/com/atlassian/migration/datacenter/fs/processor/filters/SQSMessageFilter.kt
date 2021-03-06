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

