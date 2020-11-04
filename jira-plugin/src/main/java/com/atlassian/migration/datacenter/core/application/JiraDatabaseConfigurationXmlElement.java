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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

@JacksonXmlRootElement(localName = "jira-database-config")
public class JiraDatabaseConfigurationXmlElement {

    @JacksonXmlProperty(localName = "database-type")
    private String databaseType;
    @JacksonXmlProperty(localName = "jdbc-datasource")
    private JiraDbConfigXmlElement jiraDbConfigXmlElement;

    public DatabaseConfiguration toDatabaseConfiguration() {
        DatabaseConfiguration.DBType type = getDBType()
            .orElseThrow(() -> new ConfigurationReadException("Could not determine database type in dbconfig.xml"));

        if (type == DatabaseConfiguration.DBType.H2)
            return DatabaseConfiguration.h2();

        // Assumption: Non-H2 supported databases always have a URI (H2 does not).
        URI dbURI = getURI()
            .orElseThrow(() -> new ConfigurationReadException("No URI in dbconfig.xml"));

        String userName = jiraDbConfigXmlElement.getUserName();
        String password = jiraDbConfigXmlElement.getPassword();

        validateRequiredValues(dbURI.toString(), userName, password);

        String host = dbURI.getHost();
        Integer port = dbURI.getPort();
        if (port == -1)
            port = 5432;
        //TODO: handle connection param '?;
        String name = dbURI.getPath().substring(1); // Remove leading '/'

        return new DatabaseConfiguration(type, host, port, name, userName, password);
    }

    private void validateRequiredValues(String... values) throws ConfigurationReadException
    {

        boolean allValuesValid = Stream.of(values).allMatch(StringUtils::isNotBlank);
        if (!allValuesValid) {
            throw new ConfigurationReadException("Database configuration file has invalid or missing values");
        }
    }

    public Optional<URI> getURI() {
        try {
            String urlString = jiraDbConfigXmlElement.getUrl();
            if (urlString == null || urlString.equals(""))
                return Optional.empty();

            URI absURI = URI.create(urlString);
            URI dbURI = URI.create(absURI.getSchemeSpecificPart());
            return Optional.of(dbURI);

        } catch (Exception e) {}
        return Optional.empty();

    }

    public Optional<DatabaseConfiguration.DBType> getDBType() {
        if (databaseType != null && databaseType.equals("h2")) {
            return Optional.of(DatabaseConfiguration.DBType.H2);
        }

        // The <database-type> can vary a lot (esp. for mysql) but is fairly fixed for the URI:
        Optional<URI> uri = getURI();
        if (!uri.isPresent())
            return Optional.empty();

        try {
            String scheme = uri.get().getScheme();
            return Optional.of(DatabaseConfiguration.DBType.valueOf(scheme.toUpperCase()));
        } catch (IllegalArgumentException e) { }

        return Optional.empty();
    }

}

