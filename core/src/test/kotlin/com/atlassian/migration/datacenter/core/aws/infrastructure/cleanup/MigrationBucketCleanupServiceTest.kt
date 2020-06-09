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

import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response
import software.amazon.awssdk.services.s3.model.S3Object
import java.util.concurrent.Executors
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class MigrationBucketCleanupServiceTest {

    @MockK
    lateinit var migrationService: MigrationService
    @MockK
    lateinit var context: MigrationContext
    @MockK
    lateinit var s3Client: S3Client
    private var clientSupplier = Supplier { s3Client }
    private lateinit var sut: MigrationBucketCleanupService

    private val bucketName = "bucket"

    @BeforeEach
    internal fun setUp() {
        sut = MigrationBucketCleanupService(migrationService, clientSupplier)
    }

    @Test
    fun shouldDeleteForEveryObjectInBucket() {
        val numObjects = 3
        givenBucketNameIsInMigrationContext()
        givenObjectsAreInBucket(numObjects)

        every {
            s3Client.deleteObject(any<Consumer<DeleteObjectRequest.Builder>>())
        } returns DeleteObjectResponse.builder().build()

        sut.startMigrationInfrastructureCleanup()

        verify(exactly = numObjects) {
            s3Client.deleteObject(any<Consumer<DeleteObjectRequest.Builder>>())
        }
    }

    @Test
    fun shouldReturnCleanupCompleteWhenBucketIsEmpty() {
        givenBucketNameIsInMigrationContext()
        givenObjectsAreInBucket(0)

        assertEquals(InfrastructureCleanupStatus.CLEANUP_COMPLETE, sut.getMigrationInfrastructureCleanupStatus())
    }

    @Test
    fun shouldReturnCleanupNotStartedWhenBucketIsNotEmptyAndCleanupNotInProgress() {
        givenBucketNameIsInMigrationContext()
        givenObjectsAreInBucket(3)

        assertEquals(InfrastructureCleanupStatus.CLEANUP_NOT_STARTED, sut.getMigrationInfrastructureCleanupStatus())
    }

    @Test
    fun shouldReturnCleanupInProgressWhileEmptyingBucket() {
        givenBucketNameIsInMigrationContext()
        givenObjectsAreInBucket(1000)

        every {
            s3Client.deleteObject(any<Consumer<DeleteObjectRequest.Builder>>())
        } returns DeleteObjectResponse.builder().build()

        val future = Executors.newSingleThreadExecutor().submit {
            sut.startMigrationInfrastructureCleanup()
        }

        Thread.sleep(10)

        assertEquals(InfrastructureCleanupStatus.CLEANUP_IN_PROGRESS, sut.getMigrationInfrastructureCleanupStatus())

        future.get()
    }


    private fun givenObjectsAreInBucket(numObjects: Int) {
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

    private fun givenBucketNameIsInMigrationContext() {
        every { migrationService.currentContext } returns context
        every { context.migrationBucketName } returns bucketName
    }
}