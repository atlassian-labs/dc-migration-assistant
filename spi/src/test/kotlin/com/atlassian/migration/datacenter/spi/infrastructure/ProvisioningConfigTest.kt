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

package com.atlassian.migration.datacenter.spi.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ProvisioningConfigTest {
    @Test
    fun shouldGetListParametersAsCommaSeparatedValues() {
        val config = ProvisioningConfig("stackName", object : HashMap<String, Any>() {
            init {
                put("password", "iamsupersecure.trustme")
                put("instanceCount", 2)
                put("multiAZ", false)
            }
        }, ProvisioningConfig.DeploymentMode.WITH_NETWORK)
        val params = config.params
        assertEquals("iamsupersecure.trustme", params["password"])
        assertEquals("2", params["instanceCount"])
        assertEquals("false", params["multiAZ"])
    }

    @Test
    fun shouldBeDeserialisable() {
        val jiraProductJSONKey = "JiraProduct"
        val jiraProductJSONValue = "Software"
        val stackName = "treb-20"
        val instanceCountKey = "instanceCount"
        val instanceCountValue = "2"
        val json = """
            {
                "stackName": "$stackName",
                "params": {
                    "$jiraProductJSONKey": "$jiraProductJSONValue",
                    "$instanceCountKey": "$instanceCountValue"
                },
                "deploymentMode": "WITH_NETWORK"
            }
        """.trimMargin()

        val mapper = ObjectMapper()
        val config = mapper.readValue(json, ProvisioningConfig::class.java)

        assertEquals(jiraProductJSONValue, config.params[jiraProductJSONKey])
        assertEquals(instanceCountValue, config.params[instanceCountKey])
        assertEquals(stackName, config.stackName)
        assertEquals(ProvisioningConfig.DeploymentMode.WITH_NETWORK, config.deploymentMode)
    }
}