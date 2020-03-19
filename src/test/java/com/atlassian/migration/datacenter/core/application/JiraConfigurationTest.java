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
import com.atlassian.migration.datacenter.core.exceptions.UnsupportedPasswordEncodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraConfigurationTest
{
    @Mock JiraHome jiraHome;
    @TempDir Path tempDir;

    JiraConfiguration jiraConfiguration;

    @BeforeEach
    void setUp()
    {
        jiraConfiguration = new JiraConfiguration(jiraHome);
        when(jiraHome.getLocalHomePath()).thenReturn(tempDir.toString());
    }

    @Test
    void shouldRaiseFileNotFoundExceptionWhenDatabaseFileIsNotFound() {
        Exception e = assertThrows(ConfigurationReadException.class, () -> {
            jiraConfiguration.getDatabaseConfiguration();
        });
        assertEquals(FileNotFoundException.class, e.getCause().getClass());
    }

    @Test
    void shouldRaiseConfigurationExceptionWhenDatabaseFileIsNotValid() throws IOException {
        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, "not-xml".getBytes());

        assertThrows(ConfigurationReadException.class, () -> {
            jiraConfiguration.getDatabaseConfiguration();
        });
    }

    @Test
    void shouldRaiseAnExceptionWhenDbconfigFileIsMissingElements() throws IOException {
        String xml = "<jira-database-config><jdbc-datasource><username>jdbc_user</username><password>password</password></jdbc-datasource></jira-database-config>";
        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, xml.getBytes());

        assertThrows(ConfigurationReadException.class, () -> {
            jiraConfiguration.getDatabaseConfiguration();
        });
    }

    @Test
    void shouldBeValidWhenConfigurationFileIsComplete() throws Exception {
        String url = "jdbc:postgresql://dbhost:9876/dbname";
        String xml = "<jira-database-config><jdbc-datasource><url>" + url + "</url><username>jdbc_user</username><password>password</password></jdbc-datasource></jira-database-config>";
        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, xml.getBytes());

        DatabaseConfiguration config = jiraConfiguration.getDatabaseConfiguration();
        assertEquals("jdbc_user", config.getUsername());
        assertEquals("password", config.getPassword());
        assertEquals("dbhost", config.getHost());
        assertEquals("dbname", config.getName());
        assertEquals(9876, config.getPort());
        assertEquals(DatabaseConfiguration.DBType.POSTGRESQL, config.getType());
    }

    @Test
    void shouldBeValidWhenConfigurationDoesNotContainValueForPort() throws Exception {
        String url = "jdbc:postgresql://dbhost/dbname";
        String xml = "<jira-database-config><jdbc-datasource><url>" + url + "</url><username>jdbc_user</username><password>password</password></jdbc-datasource></jira-database-config>";
        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, xml.getBytes());

        DatabaseConfiguration config = jiraConfiguration.getDatabaseConfiguration();
        assertEquals("jdbc_user", config.getUsername());
        assertEquals("password", config.getPassword());
        assertEquals("dbhost", config.getHost());
        assertEquals("dbname", config.getName());
        assertEquals(5432, config.getPort());
    }

    @Test
    void shouldParseDatabaseConfigWithValidCipher() throws Exception {
        String url = "jdbc:postgresql://dbhost:9876/dbname";
        String xml = "<jira-database-config><jdbc-datasource>" +
                "<url>" + url + "</url>" +
                "<username>jdbc_user</username>" +
                "<atlassian-password-cipher-provider>com.atlassian.db.config.password.ciphers.base64.Base64Cipher</atlassian-password-cipher-provider>" +
                "<password>cGFzc3dvcmQ=</password>" +
                "</jdbc-datasource></jira-database-config>";

        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, xml.getBytes());

        DatabaseConfiguration databaseConfiguration = jiraConfiguration.getDatabaseConfiguration();
        assertNotNull(databaseConfiguration);

        assertEquals("jdbc_user", databaseConfiguration.getUsername());
        assertEquals("password", databaseConfiguration.getPassword());
        assertEquals("dbhost", databaseConfiguration.getHost());
        assertEquals("dbname", databaseConfiguration.getName());
    }

    @Test
    void shouldNotParseDatabaseConfigWithInvalidCipher() throws Exception {
        String url = "jdbc:postgresql://dbhost:9876/dbname";
        String xml = "<jira-database-config><jdbc-datasource>" +
                "<url>" + url + "</url>" +
                "<username>jdbc_user</username>" +
                "<atlassian-password-cipher-provider>com.atlassian.db.config.password.ciphers.algorithm.AlgorithmCipher</atlassian-password-cipher-provider>" +
                "<password>cGFzc3dvcmQ=</password>" +
                "</jdbc-datasource></jira-database-config>";

        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, xml.getBytes());

        assertThrows(UnsupportedPasswordEncodingException.class, () -> {
            jiraConfiguration.getDatabaseConfiguration();
        });
    }
}