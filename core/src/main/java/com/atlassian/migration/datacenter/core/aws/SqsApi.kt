package com.atlassian.migration.datacenter.core.aws

import java.util.*

interface SqsApi {
    fun getQueueLength(queueName: String) : Optional<Int>
}