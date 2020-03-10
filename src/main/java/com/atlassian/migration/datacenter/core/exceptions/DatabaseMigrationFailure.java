package com.atlassian.migration.datacenter.core.exceptions;

/**
 * Copyright Atlassian: 03/03/2020
 */
public class DatabaseMigrationFailure extends RuntimeException
{
    public DatabaseMigrationFailure(String message)
    {
        super(message);
    }

    public DatabaseMigrationFailure(String message, Throwable cause)
    {
        super(message, cause);
    }
}
