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
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SqsQueueWatcherTest {

    @MockK
    lateinit var mockContext: MigrationContext

    @MockK
    lateinit var migrationService: MigrationService

    @MockK
    lateinit var sqsApi: SqsApi

    lateinit var queueWatcher: SqsQueueWatcher

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        queueWatcher = SqsQueueWatcher(sqsApi, migrationService, 1)
    }

    @Test
    fun shouldTransitionMigrationStateToValidate() {
        val migrationQueueUrl = "https://sqs/migrationQueue"

        every { migrationService.currentStage } returns MigrationStage.DB_MIGRATION_EXPORT_WAIT andThen MigrationStage.DB_MIGRATION_UPLOAD andThen MigrationStage.DATA_MIGRATION_IMPORT_WAIT andThen MigrationStage.FINAL_SYNC_WAIT andThen MigrationStage.DB_MIGRATION_EXPORT andThen MigrationStage.FINAL_SYNC_WAIT
        every { migrationService.transition(MigrationStage.VALIDATE) } answers {}
        every { migrationService.currentContext } returns mockContext
        every { mockContext.migrationQueueUrl } returns migrationQueueUrl
        every { sqsApi.getQueueLength(migrationQueueUrl) } returns 3 andThen 2 andThen 1 andThen 0

        val isQueueDrained = queueWatcher.awaitQueueDrain()

        Assertions.assertTrue(isQueueDrained, "Expected Queue to be drained, but wasn't")

        verify(exactly = 6) { migrationService.currentStage }
        verify(exactly = 4) { sqsApi.getQueueLength(migrationQueueUrl) }
        verify {
            migrationService.transition(MigrationStage.VALIDATE)
        }
    }
}