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
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
import com.atlassian.plugin.PluginAccessor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.hsqldb.types.Charset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ConfluenceConfigurationTest {

    @MockK
    lateinit var systemInfoService: SystemInformationService

    @MockK
    lateinit var pluginAccessor: PluginAccessor

    @InjectMockKs
    lateinit var sut: ConfluenceConfiguration

    @MockK
    lateinit var confluenceInfo: ConfluenceInfo

    @TempDir
    lateinit var confHome: Path

    @Test
    fun shouldParseDbConfig() {
        val dbPass = "password"
        val dbUser = "postgres"
        val dbHost = "postgres"
        val dbName = "postgres"
        val dbPort = 5432
        // This is not a real confluence.cfg.xml it has all non-DB components stripped out
        val confluenceCfgPostgres = """
            <?xml version="1.0" encoding="UTF-8"?>
            <confluence-configuration>
                <properties>
                    <property name="hibernate.connection.password">${dbPass}</property>
                    <property name="hibernate.connection.url">jdbc:postgresql://${dbHost}:${dbPort}/${dbName}</property>
                    <property name="hibernate.connection.username">${dbUser}</property>
                    <property name="hibernate.dialect">net.sf.hibernate.dialect.PostgreSQLDialect</property>
                </properties>
            </confluence-configuration>

        """.trimIndent()

        val mockConfluenceCfgFile = confHome.resolve("confluence.cfg.xml").toFile()
        mockConfluenceCfgFile.writeText(confluenceCfgPostgres, java.nio.charset.Charset.forName("UTF-8"))

        every { systemInfoService.confluenceInfo } returns confluenceInfo
        every { confluenceInfo.home } returns confHome.toString()

        val dbConfiguration = sut.databaseConfiguration

        assertEquals(dbPass, dbConfiguration.password)
        assertEquals(dbUser, dbConfiguration.username)
        assertEquals(dbHost, dbConfiguration.host)
        assertEquals(dbPort, dbConfiguration.port)
        assertEquals(dbName, dbConfiguration.name)
        assertEquals(DatabaseConfiguration.DBType.POSTGRESQL, dbConfiguration.type)
    }

}