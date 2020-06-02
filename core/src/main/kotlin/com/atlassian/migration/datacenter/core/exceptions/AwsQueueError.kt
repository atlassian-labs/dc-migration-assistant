package com.atlassian.migration.datacenter.core.exceptions


open class AwsQueueError : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)

}

class AwsQueueConnectionException(message: String, cause: Throwable) : AwsQueueError(message, cause)
class AwsQueueApiUnsuccessfulResponse(message: String) : AwsQueueError(message)