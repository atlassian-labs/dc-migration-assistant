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

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.exceptions.ConfigurationReadException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class JiraConfiguration implements ApplicationConfiguration
{
    private final XmlMapper xmlMapper;
    private final JiraHome jiraHome;
    private Optional<DatabaseConfiguration> databaseConfiguration = Optional.empty();

    public JiraConfiguration(JiraHome jiraHome)
    {
        this.jiraHome = jiraHome;
        this.xmlMapper = new XmlMapper();
    }

    @Override
    public DatabaseConfiguration getDatabaseConfiguration() throws ConfigurationReadException {
        if (!databaseConfiguration.isPresent()) {
            databaseConfiguration = Optional.of(parseDatabaseConfigurationFromXmlFile());
        }
        return databaseConfiguration.get();
    }

    private DatabaseConfiguration parseDatabaseConfigurationFromXmlFile() throws ConfigurationReadException {
        Path databaseConfig = Paths.get(jiraHome.getLocalHomePath()).resolve("dbconfig.xml");

        try {
            DatabaseConfigurationXmlElement xmlElement = this.xmlMapper.readValue(databaseConfig.toFile(), DatabaseConfigurationXmlElement.class);
            return xmlElement.toDatabaseConfiguration();
        } catch (IOException e) {
            throw new ConfigurationReadException("Unable to parse database configuration XML file", e);
        }
    }
}
