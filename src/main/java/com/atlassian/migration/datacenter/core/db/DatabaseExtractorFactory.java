package com.atlassian.migration.datacenter.core.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration.DBType;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;
import org.springframework.stereotype.Component;

/**
 * Copyright Atlassian: 10/03/2020
 */
@Component
public class DatabaseExtractorFactory
{
    public static DatabaseExtractor getExtractor(ApplicationConfiguration config) throws DatabaseMigrationFailure
    {
        if (config.getDatabaseConfiguration().getType().equals(DBType.POSTGRESQL)) {
            return new PostgresExtractor(config);
        } else {
            throw new DatabaseMigrationFailure("Unsupported database type: " + config.getDatabaseConfiguration().getType());
        }
    }
}
