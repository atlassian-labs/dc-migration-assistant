package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;

import java.io.File;

/**
 * Copyright Atlassian: 10/03/2020
 */
public interface DatabaseMigration
{
    Process startDatabaseDump(File to) throws DatabaseMigrationFailure;

    void dumpDatabase(File to) throws DatabaseMigrationFailure;
}
