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
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class PostgresExtractor(private val applicationConfiguration: ApplicationConfiguration, 
                        private val databaseClientTools: DatabaseClientTools) : DatabaseExtractor {
    private val log = LoggerFactory.getLogger(javaClass)

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

        log.info("Dump database to $target using $numJobs threads")

        val pgdump = databaseClientTools.getDatabaseDumpClientPath() ?: throw DatabaseMigrationFailure("Failed to find appropriate pg_dump executable.")
        val config = applicationConfiguration.databaseConfiguration

        val args = listOf(pgdump,
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
        val builder = ProcessBuilder(args)
                .inheritIO()
        builder.environment()["PGPASSWORD"] = config.password

        return try {
            if(Files.exists(target))  {
                log.debug("pg_dump archive [$target] already exists. Deleting now...")
                deleteDatabaseDump(target)
            }
            log.info("Calling pg_dump with: "+args.joinToString(" "))
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
            proc.waitFor()
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
