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

package com.atlassian.migration.datacenter.core.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class PostgresExtractor implements DatabaseExtractor
{
    private ApplicationConfiguration applicationConfiguration;

    private static String pddumpPaths[] = {"/usr/bin/pg_dump", "/usr/local/bin/pg_dump"};

    public PostgresExtractor(ApplicationConfiguration applicationConfiguration)
    {
        this.applicationConfiguration = applicationConfiguration;
    }

    private Optional<String> getPgdumpPath()
    {
        for (String path : pddumpPaths) {
            Path p = Paths.get(path);
            if (Files.isReadable(p) && Files.isExecutable(p)) {
                return Optional.of(path);
            }
        }
        return Optional.empty();
    }

    @Override
    public Process startDatabaseDump(Path target) throws DatabaseMigrationFailure
    {
        return startDatabaseDump(target, false);
    }

    /**
     * Invoke `pg_dump` against the database details store in the supplied ApplicationConfiguration. Some important notes:
     *
     * <ul>
     * <li>It is the responsibility of the caller to ensure that the filesystems the target resides on has sufficient space.</li>
     * <li>stdio & stderr are redirected to the stderr of the calling process.</li>
     * </ul>
     *
     * @param target - The directory to dump the compressed database export to.
     * @param parallel - Whether to use parallel dump strategy.
     * @return The underlying process object.
     * @throws DatabaseMigrationFailure on failure.
     */
    @Override
    public Process startDatabaseDump(Path target, Boolean parallel) throws DatabaseMigrationFailure
    {
        String pgdump = getPgdumpPath()
            .orElseThrow(() -> new DatabaseMigrationFailure("Failed to find appropriate pg_dump executable."));
        Integer numJobs = parallel ? 4 : 1;  // Common-case for now, could be tunable or num-CPUs.

        DatabaseConfiguration config = applicationConfiguration.getDatabaseConfiguration();

        ProcessBuilder builder = new ProcessBuilder(pgdump,
                                                    "--no-owner",
                                                    "--no-acl",
                                                    "--compress=9",
                                                    "--format=directory",
                                                    "--jobs", numJobs.toString(),
                                                    "--file", target.toString(),
                                                    "--dbname", config.getName(),
                                                    "--host", config.getHost(),
                                                    "--port", config.getPort().toString(),
                                                    "--username", config.getUsername())
            .inheritIO();
        builder.environment().put("PGPASSWORD", config.getPassword());

        try {
            return  builder.start();
        } catch (IOException e) {
            String command = String.join(" ", builder.command());
            throw new DatabaseMigrationFailure("Failed to start pg_dump process with commandline: " + command, e);
        }


    }

    /**
     * This is a blocking version of startDatabaseDump(); this may take some time, so should be called from a thread.
     *
     * @param to - The file to dump the compressed database export to.
     * @throws DatabaseMigrationFailure on failure, including a non-zero exit code.
     */
    @Override
    public void dumpDatabase(Path to) throws DatabaseMigrationFailure
    {
        Process proc = startDatabaseDump(to);

        int exit;
        try {
            exit = proc.waitFor();
        } catch (InterruptedException e) {
            throw new DatabaseMigrationFailure("The pg_dump process was interrupted. Check logs for more information.", e);
        }

        if (exit != 0) {
            throw new DatabaseMigrationFailure("pg_dump process exited with non-zero status: " + exit);
        }
    }
}
