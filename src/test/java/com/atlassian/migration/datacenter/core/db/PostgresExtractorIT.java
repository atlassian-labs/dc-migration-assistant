package com.atlassian.migration.datacenter.core.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import org.testcontainers.utility.MountableFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Copyright Atlassian: 09/03/2020
 */
@Testcontainers
@ExtendWith(MockitoExtension.class)
class PostgresExtractorIT
{
    @Container
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
        .withDatabaseName("jira")
        .withCopyFileToContainer(MountableFile.forClasspathResource("db/jira.sql"), "/docker-entrypoint-initdb.d/jira.sql");

    @Mock(lenient = true)
    ApplicationConfiguration configuration;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp()
    {
        when(configuration.getDatabaseConfiguration())
            .thenReturn(new DatabaseConfiguration(DatabaseConfiguration.DBType.POSTGRESQL,
                                                  postgres.getContainerIpAddress(),
                                                  postgres.getMappedPort(5432),
                                                  postgres.getDatabaseName(),
                                                  postgres.getUsername(),
                                                  postgres.getPassword()));
    }

    @AfterEach
    void tearDown()
    {
    }

    @Test
    void testPsqlDataImported() throws SQLException
    {
        Properties props = new Properties();
        props.put("user", postgres.getUsername());
        props.put("password", postgres.getPassword());
        Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), props);
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("SELECT id, summary FROM jiraissue WHERE issuenum = 1;");
        assertTrue(r.next());
        String summary = r.getString(2);
        assertTrue(summary.startsWith("As an Agile team, I'd like to learn about Scrum"));
        assertFalse(r.next());

        r.close();
        s.close();
        conn.close();
    }

    @Test
    void testDatabaseDump() throws IOException
    {
        PostgresExtractor migration = new PostgresExtractor(configuration);
        Path target = tempDir.resolve("database.dump");

        migration.dumpDatabase(target);
        assertTrue(target.toFile().exists());
        assertTrue(target.toFile().isDirectory());

        boolean found = false;
        for (Path p: Files.newDirectoryStream(target, "*.gz")) {
            InputStream stream = new GZIPInputStream(new FileInputStream(p.toFile()));
            for (String line : IOUtils.readLines(stream, "UTF-8")) {
                if (line.contains("As an Agile team, I'd like to learn about Scrum")) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found);
    }
}