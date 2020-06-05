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

package com.atlassian.migration.datacenter.core.fs.captor

import com.atlassian.migration.datacenter.core.aws.SqsApi
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.MigrationStage.FINAL_SYNC_WAIT
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

//TODO: Convert to using co-routines and suspend functions?
class SqsQueueWatcher(private val sqsAPi: SqsApi,
                      private val migrationService: MigrationService,
                      private val schedulerPollFrequency: Long) : QueueWatcher {

    constructor(sqsAPi: SqsApi, migrationService: MigrationService) : this(sqsAPi, migrationService, 30)

    override fun awaitQueueDrain(): Boolean {
        awaitRunnableToComplete(::checkForStateToBeInFsSyncAwait).get()
        awaitRunnableToComplete(::checkForQueueToBeEmpty).get()
        migrationService.transition(MigrationStage.VALIDATE)
        return true
    }

    private fun checkForQueueToBeEmpty(future: @ParameterName(name = "future") CompletableFuture<Unit>): Runnable {
        return Runnable {
            val migrationQueueUrl = migrationService.currentContext.migrationQueueUrl
            val queueLength = sqsAPi.getQueueLength(migrationQueueUrl)
            if (queueLength == 0) {
                future.complete(Unit)
            }
        }
    }

    private fun checkForStateToBeInFsSyncAwait(future: CompletableFuture<Unit>): Runnable {
        return Runnable {
            val currentStage = migrationService.currentStage
            if (currentStage == FINAL_SYNC_WAIT) {
                future.complete(Unit)
            }
        }
    }

    private fun awaitRunnableToComplete(runnable: (future: CompletableFuture<Unit>) -> Runnable): CompletableFuture<Unit> {
        val completableFuture = CompletableFuture<Unit>()
        val executor = Executors.newSingleThreadScheduledExecutor()

        val scheduledFuture = executor.scheduleAtFixedRate(runnable(completableFuture), 0, schedulerPollFrequency, TimeUnit.SECONDS)

        completableFuture.whenComplete { _, _ ->
            run {
                scheduledFuture.cancel(true)
            }
        }
        return completableFuture
    }
}