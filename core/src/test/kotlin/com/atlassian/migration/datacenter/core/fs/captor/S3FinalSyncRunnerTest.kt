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
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService
import com.atlassian.migration.datacenter.core.fs.FileSystemMigrationReportManager
import com.atlassian.migration.datacenter.core.fs.ReportType
import com.atlassian.migration.datacenter.core.fs.jira.listener.JiraIssueAttachmentListener
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport
import com.atlassian.scheduler.JobRunnerRequest
import com.atlassian.scheduler.status.RunOutcome
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.nio.file.Path
import java.util.function.Supplier
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class S3FinalSyncRunnerTest {

    @MockK
    lateinit var attachmentSyncManager: AttachmentSyncManager

    @MockK
    lateinit var home: Path

    @MockK
    lateinit var migrationHelperDeploymentService: AWSMigrationHelperDeploymentService

    @MockK
    lateinit var client: Supplier<S3AsyncClient>

    @MockK
    lateinit var queueWatcher: QueueWatcher

    @MockK
    lateinit var jiraIssueAttachmentListener: JiraIssueAttachmentListener

    @MockK
    lateinit var fileSystemMigrationReportManager: FileSystemMigrationReportManager

    @MockK
    lateinit var sqsApi: SqsApi

    @MockK
    lateinit var jobRunnerRequest: JobRunnerRequest

    @InjectMockKs
    lateinit var sut: S3FinalSyncRunner

    @Test
    fun subsequentRunShouldBeExecuted() {
        val report = mockk<FileSystemMigrationReport>()

        every { migrationHelperDeploymentService.deadLetterQueueResource } returns "dlq"
        every { sqsApi.emptyQueue("dlq") } just Runs
        every { jiraIssueAttachmentListener.stop() } just Runs
        every { migrationHelperDeploymentService.migrationS3BucketName } returns "migration-bucket"
        every { client.get() } returns S3AsyncClient.create()
        every { fileSystemMigrationReportManager.resetReport(ReportType.Final) } returns report
        every { attachmentSyncManager.capturedAttachments } returns emptySet()
        every { report.failedFiles } returns emptySet()
        every { queueWatcher.awaitQueueDrain() } returns true

        val result = sut.runJob(jobRunnerRequest)
        assertEquals(result?.runOutcome, RunOutcome.SUCCESS)
        verify { jiraIssueAttachmentListener.stop() }

        val subsequentRun = sut.runJob(jobRunnerRequest)
        assertEquals(subsequentRun?.runOutcome, RunOutcome.SUCCESS)
    }
}