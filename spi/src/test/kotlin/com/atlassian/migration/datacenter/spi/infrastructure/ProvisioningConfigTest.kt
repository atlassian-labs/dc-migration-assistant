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