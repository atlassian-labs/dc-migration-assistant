package com.atlassian.migration.datacenter.core.application;

import com.atlassian.jira.config.util.JiraHome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.xpath.XPathExpressionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Copyright Atlassian: 04/03/2020
 */
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
    void noDbConfigFile()
    {
        Exception e = assertThrows(ApplicationConfiguration.ConfigurationReadException.class, () -> {
            jiraConfiguration.getDatabaseConfiguration();
        });
        assertEquals(FileNotFoundException.class, e.getCause().getClass());
    }

    @Test
    void dbconfigBadFile() throws IOException
    {
        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, "not-xml".getBytes());

        Exception e = assertThrows(ApplicationConfiguration.ConfigurationReadException.class, () -> {
            jiraConfiguration.getDatabaseConfiguration();
        });
        assertEquals(XPathExpressionException.class, e.getCause().getClass());
    }

    @Test
    void dbconfigMissingElements() throws IOException
    {
        String xml = "<jira-database-config><jdbc-datasource><username>jdbc_user</username><password>password</password></jdbc-datasource></jira-database-config>";
        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, xml.getBytes());

        Exception e = assertThrows(ApplicationConfiguration.ConfigurationReadException.class, () -> {
            jiraConfiguration.getDatabaseConfiguration();
        });
    }

    @Test
    void dbconfigValid() throws Exception
    {
        String url = "jdbc:postgresql://dbhost:9876/dbname";
        String xml = "<jira-database-config><jdbc-datasource><url>"+url+"</url><username>jdbc_user</username><password>password</password></jdbc-datasource></jira-database-config>";
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
    void dbconfigValidNoPort() throws Exception
    {
        String url = "jdbc:postgresql://dbhost/dbname";
        String xml = "<jira-database-config><jdbc-datasource><url>"+url+"</url><username>jdbc_user</username><password>password</password></jdbc-datasource></jira-database-config>";
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
    void dbconfigValidBase64() throws Exception
    {
        String url = "jdbc:postgresql://dbhost:9876/dbname";
        String xml = "<jira-database-config><jdbc-datasource>"+
                     "<url>"+url+"</url>"+
                     "<username>jdbc_user</username>"+
                     "<atlassian-password-cipher-provider>com.atlassian.db.config.password.ciphers.base64.Base64Cipher</atlassian-password-cipher-provider>"+
                     "<password>cGFzc3dvcmQ=</password>"+
                     "</jdbc-datasource></jira-database-config>";

        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, xml.getBytes());

        DatabaseConfiguration config = jiraConfiguration.getDatabaseConfiguration();
        assertEquals("jdbc_user", config.getUsername());
        assertEquals("password", config.getPassword());
        assertEquals("dbhost", config.getHost());
        assertEquals("dbname", config.getName());
    }

    @Test
    void dbconfigUnsupportedDecoder() throws Exception
    {
        String url = "jdbc:postgresql://dbhost:9876/dbname";
        String xml = "<jira-database-config><jdbc-datasource>"+
                     "<url>"+url+"</url>"+
                     "<username>jdbc_user</username>"+
                     "<atlassian-password-cipher-provider>com.atlassian.db.config.password.ciphers.algorithm.AlgorithmCipher</atlassian-password-cipher-provider>"+
                     "<password>cGFzc3dvcmQ=</password>"+
                     "</jdbc-datasource></jira-database-config>";

        final Path file = tempDir.resolve("dbconfig.xml");
        Files.write(file, xml.getBytes());

        Exception e = assertThrows(ApplicationConfiguration.UnsupportedPasswordEncoding.class, () -> {
            jiraConfiguration.getDatabaseConfiguration();
        });
    }


}