package com.atlassian.migration.datacenter.core.aws

import com.atlassian.migration.datacenter.core.exceptions.AwsQueueError

interface SqsApi {
    @Throws(AwsQueueError::class)
    fun getQueueLength(queueUrl: String) : Int
}