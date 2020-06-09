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

import cloud.localstack.TestUtils
import cloud.localstack.docker.LocalstackDockerExtension
import cloud.localstack.docker.annotation.LocalstackDockerProperties
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.util.AwsCredentialsProviderShim
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.io.File
import java.net.URI
import java.util.function.Supplier
import kotlin.test.assertEquals

@ExtendWith(LocalstackDockerExtension::class, MockKExtension::class)
@LocalstackDockerProperties(services = ["s3"], imageTag = "0.10.8")
class MigrationBucketCleanupServiceIT {

    @MockK
    lateinit var context: MigrationContext
    @MockK
    lateinit var migrationService: MigrationService

    private lateinit var client: S3Client
    private lateinit var sut: MigrationBucketCleanupService

    private val bucket = "tes-bucket"
    private val keyPrefix = "object-"
    private val numObjects = 4

    @BeforeEach
    internal fun setUp() {
        client = S3Client.builder()
                .endpointOverride(URI.create("http://localhost:4572"))
                .region(Region.of(TestUtils.DEFAULT_REGION))
                .credentialsProvider(AwsCredentialsProviderShim(TestUtils.getCredentialsProvider()))
                .build()
        sut = MigrationBucketCleanupService(migrationService, Supplier { client })
    }

    @Test
    fun shouldClearAllObjectsFromBucket() {
        client.createBucket { it.bucket(bucket) }
        val dummyFile = File.createTempFile("test", "test")
        for (i in 0 until numObjects) {
            client.putObject({ it.key("$keyPrefix$i").bucket(bucket) }, dummyFile.toPath())
        }

        val buckets = client.listBuckets()
        assertEquals(1, buckets.buckets().size)
        assertEquals(bucket, buckets.buckets()[0].name())
        val objects = client.listObjects { it.bucket(bucket) }
        assertEquals(numObjects, objects.contents().size)
        for (i in 0 until numObjects) {
            assertEquals("$keyPrefix$i", objects.contents()[i].key())
        }

        every { migrationService.currentContext } returns context
        every { context.migrationBucketName } returns bucket

        sut.startMigrationInfrastructureCleanup()

        val objectsAfter = client.listObjects { it.bucket(bucket) }
        assertEquals(0, objectsAfter.contents().size)

        dummyFile.delete()
    }
}