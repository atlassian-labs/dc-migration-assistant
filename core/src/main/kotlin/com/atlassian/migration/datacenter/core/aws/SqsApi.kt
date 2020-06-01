package com.atlassian.migration.datacenter.core.aws

interface SqsApi {
    fun getQueueLength(queueName: String) : Int?
}