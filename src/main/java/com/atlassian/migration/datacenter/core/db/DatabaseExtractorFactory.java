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
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration.DBType;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;

public class DatabaseExtractorFactory {
    public static DatabaseExtractor getExtractor(ApplicationConfiguration config) throws DatabaseMigrationFailure {
        if (config.getDatabaseConfiguration().getType().equals(DBType.POSTGRESQL)) {
            return new PostgresExtractor(config);
        }

        //Profile scoped perhaps?
        if (config.getDatabaseConfiguration().getType().equals(DBType.H2)) {
            return new UnSupportedDatabaseExtractor();
        }

        throw new DatabaseMigrationFailure("Unsupported database type: " + config.getDatabaseConfiguration().getType());
    }
}
