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
import net.swiftzer.semver.SemVer
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class PostgresClientTooling(private val applicationConfiguration: ApplicationConfiguration) : DatabaseClientTools {
    companion object {
        private val log = LoggerFactory.getLogger(PostgresClientTooling::class.java)
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
        val pgdump = getBinaryPath("pg_dump") ?: return null

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
    override fun getBinaryPath(binaryName: String): String? {
        for (path in resolveBinaryPath(binaryName)) {
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
        val psql = getBinaryPath("psql") ?: return null
        val config = applicationConfiguration.databaseConfiguration
        val url = "postgresql://${config.username}:${config.password}@${config.host}:${config.port}/${config.name}"

        try {
            /*
             * pg_dump availability is a hard requirements on this plugin. 
             * If pg_dump is available we can assume psql will also be available.
             * 
             * The pg_dump utility is obtained by installing a postgres
             * client library. These clients typically include psql.
             */
            val proc = ProcessBuilder(psql,
                    "-At",
                    url,
                    "-c", "SHOW server_version")
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            proc.waitFor(60, TimeUnit.SECONDS)

            val version = proc.inputStream.bufferedReader().readLine()

            return SemVer.parse(version)

        } catch (e: Exception) {
            log.error("Failed to get server version from command-line")
            return null
        }
    }

    private fun resolveBinaryPath(binaryName: String): Array<Path> {
        return try {
            val proc = ProcessBuilder("which",
                    binaryName)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            proc.waitFor(60, TimeUnit.SECONDS)

            arrayOf(Paths.get(proc.inputStream.bufferedReader().readLine()))

        } catch (e: Exception) {
            log.error("Failed to resolve path to $binaryName binary. Falling back to default locations '/usr/bin/$binaryName and '/usr/local/bin/$binaryName'", e)
            //Fallback to documented paths for binary if one could not be dynamically found
            getDefaultBinaryPaths(binaryName)
        }
    }

    private fun getDefaultBinaryPaths(binaryName: String): Array<Path> {
        return arrayOf(Paths.get("/usr/bin/$binaryName"), Paths.get("/usr/local/bin/$binaryName"))
    }
}