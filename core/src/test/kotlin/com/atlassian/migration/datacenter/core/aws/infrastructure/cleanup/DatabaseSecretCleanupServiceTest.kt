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

import com.atlassian.core.util.DateUtils
import com.atlassian.migration.datacenter.core.aws.db.restore.TargetDbCredentialsStorageService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.jfree.data.time.Second
import org.joda.time.Seconds
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretResponse
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretRequest
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretResponse
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import java.util.function.Supplier
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class DatabaseSecretCleanupServiceTest {

    @MockK
    lateinit var secretsManagerClient: SecretsManagerClient
    private val clientSupplier = Supplier { secretsManagerClient }
    @MockK
    lateinit var deleteResponse: DeleteSecretResponse
    @MockK
    lateinit var sdkResponse: SdkHttpResponse

    @MockK
    lateinit var dbCredentialsStorageService: TargetDbCredentialsStorageService

    lateinit var sut: DatabaseSecretCleanupService

    private val mySecret = "secret"

    @BeforeEach
    internal fun setUp() {
        sut = DatabaseSecretCleanupService(clientSupplier, dbCredentialsStorageService)
        every { dbCredentialsStorageService.secretName } returns mySecret
    }

    @Test
    fun shouldDeleteSecretWhenItExists() {
        givenDeleteRequestCompletesWithStatus(true)

        assertTrue(sut.startMigrationInfrastructureCleanup())
    }

    @Test
    fun shouldReturnFalseWhenDeleteRequestFails() {
        givenDeleteRequestCompletesWithStatus(false)

        assertFalse(sut.startMigrationInfrastructureCleanup())
    }

    @Test
    fun shouldReturnTrueWhenSecretDoesNotExist() {
        every {
            secretsManagerClient.deleteSecret(
                    DeleteSecretRequest
                            .builder()
                            .secretId(mySecret)
                            .forceDeleteWithoutRecovery(true).build())
        } throws ResourceNotFoundException.builder().build()

        assertTrue(sut.startMigrationInfrastructureCleanup())
    }

    @Test
    fun shouldReturnFalseWhenDeleteFailsWithError() {
        every {
            secretsManagerClient.deleteSecret(
                    DeleteSecretRequest
                            .builder()
                            .secretId(mySecret)
                            .forceDeleteWithoutRecovery(true).build())
        } throws SdkException.builder().build()

        assertFalse(sut.startMigrationInfrastructureCleanup())
    }

    @Test
    fun shouldReturnCompleteWhenDeleteDateIsInThePast() {
        every {
            secretsManagerClient.describeSecret(
                    DescribeSecretRequest
                            .builder()
                            .secretId(mySecret)
                            .build()
            )
        } returns DescribeSecretResponse.builder().deletedDate(Instant.now().minusSeconds(20L )).build()

        assertEquals(InfrastructureCleanupStatus.CLEANUP_COMPLETE, sut.getMigrationInfrastructureCleanupStatus())
    }

    @Test
    fun shouldReturnInProgressWhenDeletionDateIsInTheFuture() {
        every {
            secretsManagerClient.describeSecret(
                    DescribeSecretRequest
                            .builder()
                            .secretId(mySecret)
                            .build()
            )
        } returns DescribeSecretResponse.builder().deletedDate(Instant.now().plusSeconds(20L )).build()

        assertEquals(InfrastructureCleanupStatus.CLEANUP_IN_PROGRESS, sut.getMigrationInfrastructureCleanupStatus())
    }

    @Test
    fun shouldReturnNotStartedWhenNoDeletionDate() {
        every {
            secretsManagerClient.describeSecret(
                    DescribeSecretRequest
                            .builder()
                            .secretId(mySecret)
                            .build()
            )
        } returns DescribeSecretResponse.builder().deletedDate(null).build()

        assertEquals(InfrastructureCleanupStatus.CLEANUP_NOT_STARTED, sut.getMigrationInfrastructureCleanupStatus())
    }

    @Test
    fun shouldReturnCompleteWhenSecretDoesntExist() {
        every {
            secretsManagerClient.describeSecret(
                    DescribeSecretRequest
                            .builder()
                            .secretId(mySecret)
                            .build()
            )
        } throws ResourceNotFoundException.builder().build()

        assertEquals(InfrastructureCleanupStatus.CLEANUP_COMPLETE, sut.getMigrationInfrastructureCleanupStatus())
    }

    private fun givenDeleteRequestCompletesWithStatus(status: Boolean) {
        every {
            secretsManagerClient.deleteSecret(
                    DeleteSecretRequest
                            .builder()
                            .secretId(mySecret)
                            .forceDeleteWithoutRecovery(true).build())
        } returns deleteResponse
        every { deleteResponse.sdkHttpResponse() } returns sdkResponse
        every { sdkResponse.isSuccessful } returns status
    }
}