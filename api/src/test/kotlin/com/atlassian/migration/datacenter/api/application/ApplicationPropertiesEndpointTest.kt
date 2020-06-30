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

package com.atlassian.migration.datacenter.api.application

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationPropertiesEndpointTest {

    @MockK
    lateinit var configuration: ApplicationConfiguration

    @InjectMockKs
    lateinit var applicationPropertiesEndpoint: ApplicationPropertiesEndpoint

    @BeforeEach
    fun init() = MockKAnnotations.init(this)

    @Test
    fun `returns correct Jira version`() {
        val version = "8.8.3"
        every { configuration.applicationVersion } returns version

        val applicationProperties = applicationPropertiesEndpoint.getApplicationProperties()

        val expectedProps =
            ObjectMapper().writeValueAsString(ApplicationPropertiesEndpoint.ApplicationProperties(version))

        assertEquals(
            expectedProps,
            applicationProperties.entity
        )
    }
}