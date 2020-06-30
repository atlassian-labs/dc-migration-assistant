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
import net.swiftzer.semver.SemVer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import kotlin.test.assertEquals

@Testcontainers
@ExtendWith(MockitoExtension::class)
internal class PostgresClientToolingIT {
    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:9.6.18").apply {
            withDatabaseName("jira")
            withCopyFileToContainer(MountableFile.forClasspathResource("db/jira.sql"), "/docker-entrypoint-initdb.d/jira.sql")
        }
    }

    @Mock(lenient = true)
    var configuration: ApplicationConfiguration? = null
    
    private lateinit var databaseClientTooling: PostgresClientTooling;

    @BeforeEach
    fun setUp() {
        Mockito.`when`(configuration!!.databaseConfiguration)
                .thenReturn(DatabaseConfiguration(DatabaseConfiguration.DBType.POSTGRESQL,
                        postgres.containerIpAddress,
                        postgres.getMappedPort(5432),
                        postgres.databaseName,
                        postgres.username,
                        postgres.password))

        databaseClientTooling = PostgresClientTooling(configuration!!)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun itShouldObtainThePostgresServerVersion() {
        assertEquals(SemVer(9, 6, 18), databaseClientTooling.getDatabaseServerVersion())
    }
}