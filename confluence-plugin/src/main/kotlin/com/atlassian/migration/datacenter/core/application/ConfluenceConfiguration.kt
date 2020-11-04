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

package com.atlassian.migration.datacenter.core.application

import com.atlassian.confluence.status.service.SystemInformationService
import com.atlassian.plugin.PluginAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import java.nio.file.Paths

class ConfluenceConfiguration(private val systemInformationService: SystemInformationService, pluginAccessor: PluginAccessor) : CommonApplicationConfiguration(pluginAccessor) {
    private val pluginKey = "com.atlassian.migration.datacenter.confluence-plugin"
    private val confluenceConfigFile = "confluence.cfg.xml"
    private val mapper: ObjectMapper = XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun getPluginKey(): String {
        return pluginKey
    }

    override fun getApplicationVersion(): String {
        return systemInformationService.confluenceInfo.version
    }


    override fun getDatabaseConfiguration(): DatabaseConfiguration {
        val dialectPropertyName = "hibernate.dialect"
        val urlPropertyName = "hibernate.connection.url"
        val usernamePropertyName = "hibernate.connection.username"
        val passwordPropertyName = "hibernate.connection.password"

        val homePath = Paths.get(systemInformationService.confluenceInfo.home).resolve(confluenceConfigFile)

        val xmlElement = mapper.readValue(homePath.toFile(), ConfluenceCfgXmlElement::class.java)

        val properties = xmlElement.properties.map { prop -> prop.name to prop.value }.toMap()

        val dbType = dialectClassToDbType(properties[dialectPropertyName])
        return if (dbType == DatabaseConfiguration.DBType.H2) {
            DatabaseConfiguration.h2()
        } else {
            val jdbcRegex = Regex("^jdbc:.*?://(.*?):([0-9]+)/(.+)\$")

            val result = jdbcRegex.find(properties[urlPropertyName]!!) ?: error("Could not parse JDBC URL")

            val groups = result.groupValues

            val host = groups[1]
            val port = Integer.parseInt(groups[2])
            val name = groups[3]

            DatabaseConfiguration(
                    dbType,
                    host,
                    port,
                    name,
                    properties[usernamePropertyName],
                    properties[passwordPropertyName])
        }
    }

    private fun dialectClassToDbType(dialectClassName: String?): DatabaseConfiguration.DBType {
        return when (dialectClassName) {
            "net.sf.hibernate.dialect.PostgreSQLDialect" -> DatabaseConfiguration.DBType.POSTGRESQL
            "org.hibernate.dialect.H2Dialect" -> DatabaseConfiguration.DBType.H2
            else -> error("Unsupported database type")
        }
    }

}