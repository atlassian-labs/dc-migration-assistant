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
package com.atlassian.migration.datacenter.api.aws

import com.atlassian.migration.datacenter.api.aws.AWSConfigureEndpoint.AWSConfigureWebObject
import com.atlassian.migration.datacenter.core.aws.cloud.AWSConfigurationService
import com.atlassian.migration.datacenter.spi.exceptions.InvalidCredentialsException
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.ws.rs.core.Response

@ExtendWith(MockKExtension::class)
internal class AWSConfigureEndpointTest {
    @MockK(relaxUnitFun = true)
    lateinit var configurationService: AWSConfigurationService

    @InjectMockKs
    lateinit var sut: AWSConfigureEndpoint

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun shouldConfigureAWS() {
        val payload = AWSConfigureWebObject()
        val accessKeyId = "accessKeyId"
        val secretKey = "secretKey"
        val region = "us-east-1"
        payload.accessKeyId = accessKeyId
        payload.secretAccessKey = secretKey
        payload.region = region

        val response = sut.storeAWSCredentials(payload)

        verify { configurationService.configureCloudProvider(accessKeyId, secretKey, region) }
        confirmVerified(configurationService)
        assertEquals(Response.Status.NO_CONTENT.statusCode, response.status)
    }

    @Test
    fun shouldReturnFailedWhenInvalidMigrationStage() {
        val payload = AWSConfigureWebObject()
        payload.accessKeyId = "accessKeyId"
        payload.secretAccessKey = "secretKey"
        payload.region = "us-east-1"
        every { configurationService.configureCloudProvider(any(), any(), any()) } throws InvalidMigrationStageError("")

        val response = sut.storeAWSCredentials(payload)

        assertEquals(Response.Status.CONFLICT.statusCode, response.status)

    }

    @Test
    fun shouldReturnFailedWhenInvalidCredentialsAreSupplied() {
        val payload = AWSConfigureWebObject()
        payload.accessKeyId = "accessKeyId"
        payload.secretAccessKey = "secretKey"
        payload.region = "us-east-1"
        val errorMessage = "suppliedCredentialsAreInvalid"
        every { configurationService.configureCloudProvider(any(), any(), any()) } throws InvalidCredentialsException(errorMessage)

        val response = sut.storeAWSCredentials(payload)

        assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
        assertEquals(errorMessage, (response.entity as Map<*, *>)["message"])
    }
}