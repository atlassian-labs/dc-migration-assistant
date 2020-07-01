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
import com.impossibl.postgres.jdbc.PGDriver
import net.swiftzer.semver.SemVer
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.SQLException
import java.util.*
import java.util.concurrent.TimeUnit

class PostgresClientTooling(private val applicationConfiguration: ApplicationConfiguration) : DatabaseClientTools {
    companion object {
        private val log = LoggerFactory.getLogger(PostgresClientTooling::class.java)
        private val defaultPgDumpPaths = arrayOf(Paths.get("/usr/bin/pg_dump"), Paths.get("/usr/local/bin/pg_dump"))
        private val versionPattern = Regex("^pg_dump\\s+\\([^\\)]+\\)\\s+(\\d[\\d\\.]+)[\\s$]")
        
        @JvmStatic
        fun parsePgDumpVersion(text: String): SemVer? {
            val match = versionPattern.find(text) ?: return null
            return SemVer.parse(match.groupValues[1])
        }
    }

    /**
     * Get the the pg_dump version
     *
     * @return semantic version of the dump utility
     */
    override fun getDatabaseDumpClientVersion(): SemVer? {
        val pgdump = getDatabaseDumpClientPath() ?: return null

        try {
            val proc = ProcessBuilder(pgdump,
                    "--version")
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            proc.waitFor(60, TimeUnit.SECONDS)

            val message = proc.inputStream.bufferedReader().readText()

            return parsePgDumpVersion(message)

        } catch (e: Exception) {
            log.error("Failed to get pg_dump version from command-line")
            return null
        }
    }

    /**
     * Get the path to the executable pg_dump binary
     *
     * @return the path to the dump utility
     */
    override fun getDatabaseDumpClientPath(): String? {
        for (path in resolvePgDumpPath()) {
            if (Files.isReadable(path) && Files.isExecutable(path)) {
                return path.toString()
            }
        }
        return null
    }

    /**
     * Get the semantic version of the postgres server
     *
     * @return the semantic version of postgres in use
     */
    override fun getDatabaseServerVersion(): SemVer? {
        // NOTE: We use the NG Postgres driver here as the official
        // one doesn't play well with OSGI.
        val config = applicationConfiguration.databaseConfiguration
        val url = "jdbc:pgsql://${config.host}:${config.port}/${config.name}"
        val props = Properties().apply {
            setProperty("user", config.username)
            setProperty("password", config.password)
        }

        val conn = try {
            PGDriver().connect(url, props)
        } catch (e: SQLException) {
            log.error("Exception opening DB connection for version", e)
            null
        } ?: return null

        val meta = conn.metaData
        conn.close()

        return SemVer.parse(meta.databaseProductVersion)
    }

    private fun resolvePgDumpPath(): Array<Path> {
        return try {
            val proc = ProcessBuilder("which", 
                    "pg_dump")
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            proc.waitFor(60, TimeUnit.SECONDS)

            arrayOf(Paths.get(proc.inputStream.bufferedReader().readLine()))
            
        } catch (e: Exception) {
            log.error("Failed to resolve path to pg_dump binary. Falling back to default locations '/usr/bin/pg_dump and '/usr/local/bin/pg_dump'", e)
            //Fallback to documented paths for pg_dump if one could not be dynamically found
            defaultPgDumpPaths
        }
    }
}
