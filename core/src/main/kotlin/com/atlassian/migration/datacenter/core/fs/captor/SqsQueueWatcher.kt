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

import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.util.function.Supplier

class SqsQueueWatcher(private val sqsAsyncClientSupplier: Supplier<SqsAsyncClient>,
                      private val migrationService: MigrationService) : QueueWatcher {

    override fun awaitQueueDrain(): Boolean {
        //TODO: Wait for
        // 1. DB Status to move to Final_Sync_Wait
        // 2. Remote SQS queue to be empty
        //Then transition service to validate
        migrationService.transition(MigrationStage.VALIDATE)
        return true
    }
}