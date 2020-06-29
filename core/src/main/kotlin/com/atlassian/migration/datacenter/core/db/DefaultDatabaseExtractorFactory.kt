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
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration.DBType
import com.atlassian.migration.datacenter.spi.exceptions.ConfigurationReadException
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure
import org.slf4j.LoggerFactory

class DefaultDatabaseExtractorFactory(val config: ApplicationConfiguration) : DatabaseExtractorFactory {
    companion object {
        val log = LoggerFactory.getLogger(DefaultDatabaseExtractorFactory::class.java)
    }
    override val extractor: DatabaseExtractor by lazy {
        try {
            if (config.databaseConfiguration.type == DBType.POSTGRESQL) {
                PostgresExtractor(config)
            } else {
                UnSupportedDatabaseExtractor()
            }
        } catch (e: ConfigurationReadException) {
            log.error("error reading database configuration from application configuration", e)
            throw DatabaseMigrationFailure("Failed reading database configuration", e)
        }
    }
}