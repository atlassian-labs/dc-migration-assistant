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

package com.atlassian.migration.datacenter.core.aws.infrastructure.cleanup

import com.atlassian.migration.datacenter.core.util.MigrationRunner
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import com.atlassian.scheduler.JobRunnerRequest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.concurrent.thread

@ExtendWith(MockKExtension::class)
internal class BucketCleanupJobRunnerTest {

    @MockK
    lateinit var migrationService: MigrationService
    @MockK
    lateinit var context: MigrationContext
    @MockK
    lateinit var s3Client: S3Client
    @MockK
    lateinit var jobRunnerRequest: JobRunnerRequest

    private var clientSupplier = Supplier { s3Client }
    private lateinit var sut: BucketCleanupJobRunner

    private val bucketName = "bucket"

    @BeforeEach
    internal fun setUp() {
        sut = BucketCleanupJobRunner(migrationService, clientSupplier)
    }

    @Test
    fun shouldDeleteBucketIfItExists() {
        givenBucketNameIsInMigrationContext()
        givenObjectsAreInBucket(0)
        andBucketWillBeDeleted()

        sut.runJob(jobRunnerRequest)

        verify(exactly = 1) {
            s3Client.deleteBucket(any<Consumer<DeleteBucketRequest.Builder>>())
        }
    }


    @Test
    fun shouldDeleteForEveryObjectInBucket() {
        val numObjects = 3
        givenBucketNameIsInMigrationContext()
        givenObjectsAreInBucket(numObjects)
        andObjectsWillBeDeleted()
        andBucketWillBeDeleted()

        sut.runJob(jobRunnerRequest)

        verify(exactly = numObjects) {
            s3Client.deleteObject(any<Consumer<DeleteObjectRequest.Builder>>())
        }
    }

    private fun givenObjectsAreInBucket(numObjects: Int) {
        every { s3Client.headBucket(any<Consumer<HeadBucketRequest.Builder>>()) } returns HeadBucketResponse.builder().build()
        val objects = mutableListOf<S3Object>()
        for (i in 1..numObjects) {
            objects.add(S3Object.builder().build())
        }
        every {
            s3Client.listObjectsV2(any() as Consumer<ListObjectsV2Request.Builder>)
        } returnsMany
                listOf(ListObjectsV2Response.builder()
                        .contents(objects)
                        .build(),
                        ListObjectsV2Response.builder().build())
    }


    private fun andObjectsWillBeDeleted() {
        every {
            s3Client.deleteObject(any<Consumer<DeleteObjectRequest.Builder>>())
        } returns DeleteObjectResponse.builder().build()
    }

    private fun andBucketWillBeDeleted() {
        every {
            s3Client.deleteBucket(any<Consumer<DeleteBucketRequest.Builder>>())
        } returns DeleteBucketResponse.builder().build()
    }

    private fun givenBucketNameIsInMigrationContext() {
        every { migrationService.currentContext } returns context
        every { context.migrationBucketName } returns bucketName
    }


}