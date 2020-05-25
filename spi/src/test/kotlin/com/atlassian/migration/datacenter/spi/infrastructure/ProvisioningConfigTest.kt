package com.atlassian.migration.datacenter.spi.infrastructure

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ProvisioningConfigTest {
    @Test
    fun shouldGetListParametersAsCommaSeparatedValues() {
        val config = ProvisioningConfig("https://template.url", "stackName", object : HashMap<String, Any>() {
            init {
                put("password", "iamsupersecure.trustme")
                put("instanceCount", 2)
                put("multiAZ", false)
            }
        })
        val params = config.params!!
        Assertions.assertEquals("iamsupersecure.trustme", params["password"])
        Assertions.assertEquals("2", params["instanceCount"])
        Assertions.assertEquals("false", params["multiAZ"])
    }
}