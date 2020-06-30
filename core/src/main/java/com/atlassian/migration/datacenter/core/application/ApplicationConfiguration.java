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

package com.atlassian.migration.datacenter.core.application;


import com.atlassian.migration.datacenter.spi.exceptions.ConfigurationReadException;

public interface ApplicationConfiguration {
    /**
     * @return key of the installed plugin
     */
    String getPluginKey();

    /**
     * @return version of the installed plugin
     */
    String getPluginVersion();

    /**
     * @return version of the host application
     */
    String getApplicationVersion();

    /**
     * Parses database configuration file and returns configuration that can be used to connect to the database
     *
     * @return application database configuration
     * @throws ConfigurationReadException when the configuration file cannot be parsed or read
     */
    DatabaseConfiguration getDatabaseConfiguration() throws ConfigurationReadException;
}
