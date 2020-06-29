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
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure
import com.impossibl.postgres.jdbc.PGDriver
import net.swiftzer.semver.SemVer
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.SQLException
import java.util.*
import java.util.concurrent.TimeUnit

class PostgresExtractor(private val applicationConfiguration: ApplicationConfiguration) : DatabaseExtractor {
    companion object {
        private val log = LoggerFactory.getLogger(PostgresExtractor::class.java)
        private val pddumpPaths = arrayOf("/usr/bin/pg_dump", "/usr/local/bin/pg_dump")

        private val versionPattern = Regex("^pg_dump\\s+\\([^\\)]+\\)\\s+(\\d[\\d\\.]+)[\\s$]")

        @JvmStatic
        fun parsePgDumpVersion(text: String): SemVer? {
            val match = versionPattern.find(text) ?: return null
            return SemVer.parse(match.groupValues[1])
        }

    }

    private val pgdumpPath: String?
        get() {
            for (path in pddumpPaths) {
                val p = Paths.get(path)
                if (Files.isReadable(p) && Files.isExecutable(p)) {
                    return path
                }
            }
            return null
        }

    override val clientVersion: SemVer?
        get() {
            val pgdump = pgdumpPath ?: return null

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

    override val serverVersion: SemVer?
        get() {
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

    @Throws(DatabaseMigrationFailure::class)
    override fun startDatabaseDump(target: Path): Process {
        return startDatabaseDump(target, false)
    }

    /**
     * Invoke `pg_dump` against the database details store in the supplied ApplicationConfiguration. Some important notes:
     *
     *
     *  * It is the responsibility of the caller to ensure that the filesystems the target resides on has sufficient space.
     *  * stdio & stderr are redirected to the stderr of the calling process.
     *
     *
     * @param target   - The directory to dump the compressed database export to.
     * @param parallel - Whether to use parallel dump strategy.
     * @return The underlying process object.
     * @throws DatabaseMigrationFailure on failure.
     */
    @Throws(DatabaseMigrationFailure::class)
    override fun startDatabaseDump(target: Path, parallel: Boolean): Process {
        val numJobs = if (parallel) 4 else 1 // Common-case for now, could be tunable or num-CPUs.

        val pgdump = pgdumpPath ?: throw DatabaseMigrationFailure("Failed to find appropriate pg_dump executable.")
        val config = applicationConfiguration.databaseConfiguration

        val builder = ProcessBuilder(pgdump,
                "--no-owner",
                "--no-acl",
                "--compress=9",
                "--format=directory",
                "--jobs", numJobs.toString(),
                "--file", target.toString(),
                "--dbname", config.name,
                "--host", config.host,
                "--port", config.port.toString(),
                "--username", config.username)
                .inheritIO()
        builder.environment()["PGPASSWORD"] = config.password

        return try {
            if(Files.exists(target))  {
                log.debug("pg_dump archive [$target] already exists. Deleting now...")
                deleteDatabaseDump(target)
            }
            builder.start()
        } catch (e: IOException) {
            val command = java.lang.String.join(" ", builder.command())
            throw DatabaseMigrationFailure("Failed to start pg_dump process with commandline: $command", e)
        }
    }

    /**
     * This is a blocking version of startDatabaseDump(); this may take some time, so should be called from a thread.
     *
     * @param to - The file to dump the compressed database export to.
     * @throws DatabaseMigrationFailure on failure, including a non-zero exit code.
     */
    @Throws(DatabaseMigrationFailure::class)
    override fun dumpDatabase(to: Path) {
        val proc = startDatabaseDump(to)
        val exit: Int
        exit = try {
            proc!!.waitFor()
        } catch (e: InterruptedException) {
            throw DatabaseMigrationFailure("The pg_dump process was interrupted. Check logs for more information.", e)
        }
        if (exit != 0) {
            throw DatabaseMigrationFailure("pg_dump process exited with non-zero status: $exit")
        }
    }
    
    @Throws(IOException::class)
    fun deleteDatabaseDump(target: Path) {
        try {
            FileUtils.deleteDirectory(target.toFile())
            log.debug("pg_dump archive [$target] deleted.")
        } catch (io: IOException) {
            throw IOException("Unable to delete existing pg_dump archive: $target", io)
        }
    }
}
