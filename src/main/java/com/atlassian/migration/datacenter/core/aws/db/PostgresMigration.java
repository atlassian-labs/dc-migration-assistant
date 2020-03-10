package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Copyright Atlassian: 03/03/2020
 */
public class PostgresMigration implements DatabaseMigration
{
    private ApplicationConfiguration applicationConfiguration;

    private static String pddumpPaths[] = {"/usr/bin/pg_dump", "/usr/local/bin/pg_dump"};

    public PostgresMigration(ApplicationConfiguration applicationConfiguration)
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

    /**
     * Invoke `pg_dump` against the database details store in the supplied ApplicationConfiguration. Some important notes:
     *
     * <ul>
     * <li>It is the responsibility of the caller to ensure that the filesystems the target resides on has sufficient space.</li>
     * <li>stderr is redirected to the stderr of the calling process.</li>
     * </ul>
     *
     * @param to - The file to dump the compressed database export to.
     * @return The underlying process object.
     * @throws DatabaseMigrationFailure on failure.
     */
    @Override
    public Process startDatabaseDump(File to) throws DatabaseMigrationFailure
    {
        String pgdump = getPgdumpPath()
            .orElseThrow(() -> new DatabaseMigrationFailure("Failed to find appropriate pg_dump executable."));

        DatabaseConfiguration config = applicationConfiguration.getDatabaseConfiguration();

        ProcessBuilder builder = new ProcessBuilder(pgdump,
                                                    "--no-owner",
                                                    "--no-acl",
                                                    "--compress=9",
                                                    "--dbname", config.getName(),
                                                    "--host", config.getHost(),
                                                    "--port", config.getPort().toString(),
                                                    "--username", config.getUsername())
            .inheritIO()
            .redirectOutput(Redirect.to(to));
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
    public void dumpDatabase(File to) throws DatabaseMigrationFailure
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
