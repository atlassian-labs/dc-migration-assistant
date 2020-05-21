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
package com.atlassian.migration.datacenter.core.db

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration
import com.atlassian.migration.datacenter.util.PSQLContainerHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.apache.commons.io.IOUtils
import org.testcontainers.utility.MountableFile
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import java.util.zip.GZIPInputStream

@Testcontainers
@ExtendWith(MockitoExtension::class)
internal class PostgresExtractorIT {

    companion object {
        @Container
        var postgres = PSQLContainerHelper("postgres:9.6")
                .withDatabaseName("jira")
                .withCopyFileToContainer(MountableFile.forClasspathResource("db/jira.sql"), "/docker-entrypoint-initdb.d/jira.sql")
    }

    @Mock(lenient = true)
    var configuration: ApplicationConfiguration? = null

    var tempDir: Path? = null

    @BeforeEach
    fun setUp() {
        tempDir = createTempDir().toPath()
        Mockito.`when`(configuration!!.databaseConfiguration)
                .thenReturn(DatabaseConfiguration(DatabaseConfiguration.DBType.POSTGRESQL,
                        postgres.containerIpAddress,
                        postgres.getMappedPort(5432),
                        postgres.databaseName,
                        postgres.username,
                        postgres.password))
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    @Throws(SQLException::class)
    fun testPsqlDataImported() {
        val props = Properties()
        props["user"] = postgres.username
        props["password"] = postgres.password
        val conn = DriverManager.getConnection(postgres.jdbcUrl, props)
        val s = conn.createStatement()
        val r = s.executeQuery("SELECT id, summary FROM jiraissue WHERE issuenum = 1;")
        Assertions.assertTrue(r.next())
        val summary = r.getString(2)
        Assertions.assertTrue(summary.startsWith("As an Agile team, I'd like to learn about Scrum"))
        Assertions.assertFalse(r.next())
        r.close()
        s.close()
        conn.close()
    }

    @Test
    @Throws(IOException::class)
    fun testDatabaseDump() {
        val migration = PostgresExtractor(configuration!!)
        val target = tempDir!!.resolve("database.dump")
        migration.dumpDatabase(target)
        Assertions.assertTrue(target.toFile().exists())
        Assertions.assertTrue(target.toFile().isDirectory)
        var found = false
        for (p in Files.newDirectoryStream(target, "*.gz")) {
            val stream: InputStream = GZIPInputStream(FileInputStream(p.toFile()))
            for (line in IOUtils.readLines(stream, "UTF-8")) {
                if (line.contains("As an Agile team, I'd like to learn about Scrum")) {
                    found = true
                    break
                }
            }
        }
        Assertions.assertTrue(found)
    }

}