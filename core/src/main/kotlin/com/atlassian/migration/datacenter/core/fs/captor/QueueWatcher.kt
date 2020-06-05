package com.atlassian.migration.datacenter.core.fs.captor

interface QueueWatcher {
    fun awaitQueueDrain() : Boolean
}