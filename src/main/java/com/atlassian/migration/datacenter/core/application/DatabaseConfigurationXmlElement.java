/*
 * Copyright (c) 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and  limitations under the License.
 */

package com.atlassian.migration.datacenter.core.application;


import com.atlassian.migration.datacenter.core.exceptions.ConfigurationReadException;
import com.atlassian.migration.datacenter.core.exceptions.UnsupportedPasswordEncodingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Base64;
import java.util.stream.Stream;

@JacksonXmlRootElement(localName = "jira-database-config")
public class DatabaseConfigurationXmlElement {

    @JacksonXmlProperty(localName = "jdbc-datasource")
    private DbConfigXmlElement dbConfigXmlElement;

    public DatabaseConfiguration toDatabaseConfiguration() {
        String urlString = this.dbConfigXmlElement.getUrl();
        String userName = this.dbConfigXmlElement.getUserName();
        String password = this.dbConfigXmlElement.getPassword();

        validateRequiredValues(urlString, userName, password);

        URI absURI = URI.create(urlString);
        URI dbURI = URI.create(absURI.getSchemeSpecificPart());

        DatabaseConfiguration.DBType type = DatabaseConfiguration.DBType.valueOf(dbURI.getScheme().toUpperCase());

        String host = dbURI.getHost();
        Integer port = dbURI.getPort();
        if (port == -1)
            port = 5432;
        //TODO: handle connection param '?;
        String name = dbURI.getPath().substring(1); // Remove leading '/'

        return new DatabaseConfiguration(type, host, port, name, userName, password);
    }

    private void validateRequiredValues(String... values) throws ConfigurationReadException {

        boolean allValuesValid = Stream.of(values).allMatch(StringUtils::isNotBlank);
        if (!allValuesValid) {
            throw new ConfigurationReadException("Database configuration file has invalid values");
        }
    }
}


class DbConfigXmlElement {
    private static final String BASE64_CLASS = "com.atlassian.db.config.password.ciphers.base64.Base64Cipher";

    private String url;
    @JacksonXmlProperty(localName = "username")
    private String userName;
    @JacksonXmlProperty(localName = "atlassian-password-cipher-provider")
    private String cipher;
    private String password;

    public DbConfigXmlElement() {
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() throws UnsupportedPasswordEncodingException {
        if (cipher != null) {
            if (!cipher.equals(BASE64_CLASS)) {
                throw new UnsupportedPasswordEncodingException("Unsupported database password encryption in dbconfig.xml; see documentation for detail: " + cipher);
            }
            return new String(Base64.getDecoder().decode(password));
        }
        return password;
    }
}
