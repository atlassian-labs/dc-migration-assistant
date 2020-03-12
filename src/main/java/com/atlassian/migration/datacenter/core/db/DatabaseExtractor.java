package com.atlassian.migration.datacenter.core.db;

import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;

import java.io.File;
import java.nio.file.Path;

/**
 * Copyright Atlassian: 10/03/2020
 */
public interface DatabaseExtractor
{
    Process startDatabaseDump(Path target) throws DatabaseMigrationFailure;
    Process startDatabaseDump(Path target, Boolean parallel) throws DatabaseMigrationFailure;

    void dumpDatabase(Path to) throws DatabaseMigrationFailure;
}
