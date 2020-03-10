package com.atlassian.migration.datacenter.core.application;

import com.atlassian.jira.config.util.JiraHome;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;

/**
 * Copyright Atlassian: 04/03/2020
 */
@Component
public class JiraConfiguration implements ApplicationConfiguration
{
    private static final String DATASOURCE_XPATH = "/jira-database-config/jdbc-datasource/";
    private static final String BASE64_CLASS = "com.atlassian.db.config.password.ciphers.base64.Base64Cipher";

    private JiraHome jiraHome;
    private Optional<DatabaseConfiguration> databaseConfiguration = Optional.empty();

    public JiraConfiguration(JiraHome jiraHome)
    {
        this.jiraHome = jiraHome;
    }

    private String getXPath(String path, Path config) throws ConfigurationReadException
    {
        // We need to recreate the input stream for each read; not the most efficient,
        // but it's only done once and it's simpler than generating a full DOM.
        FileReader reader;
        try {
            reader = new FileReader(config.toFile());
        } catch (FileNotFoundException e) {
            throw new ConfigurationReadException("Failed to open dbconfig.xml", e);
        }

        InputSource inputSource = new InputSource(reader);
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return xPath.evaluate(path, inputSource);
        } catch (Exception e) {
            throw new ConfigurationReadException("Failed to parse dbconfig.xml, XPath: "+path, e);
        }
    }

    private String getParameter(String parameter, Path config) throws ConfigurationReadException
    {
        String path = DATASOURCE_XPATH + parameter;
        String val = getXPath(path, config);

        // Note: XPath will return an empty string if not found;
        // we assume retrieved values can never be empty.
        if (val == null || val.equals(""))
            throw new ConfigurationReadException("No value found for XPath: "+path);

        return val;
    }

    private String decodePassword(Path config) throws ConfigurationReadException
    {
        String password = getParameter("password", config);
        String decoder = getXPath(DATASOURCE_XPATH+"atlassian-password-cipher-provider", config);

        if (decoder == null || decoder.equals("")) {
            return password;
        }

        if (!decoder.equals(BASE64_CLASS)) {
            throw new UnsupportedPasswordEncoding("Unsupported database password encryption in dbconfig.xml; see documentation for detail: "+decoder);
        }

        // Password is Base64 encoded. We don't have direct access to the bundled decoder, but this works fine...
        return new String(Base64.getDecoder().decode(password));
    }

    private DatabaseConfiguration parseDbConfig() throws ConfigurationReadException
    {
        Path dbconfig = Paths.get(jiraHome.getLocalHomePath()).resolve("dbconfig.xml");

        String password = decodePassword(dbconfig);
        String username = getParameter("username", dbconfig);
        String urlStr = getParameter("url", dbconfig);

        try {
            // JDBC URIs are absolute, so we need to parse them in 2 steps.
            URI absURI = URI.create(urlStr);
            URI dbURI = URI.create(absURI.getSchemeSpecificPart());
            String host = dbURI.getHost();
            Integer port = dbURI.getPort();
            if (port == -1)
                port = 5432;
            String name = dbURI.getPath().substring(1); // Remove leading '/'

            return new DatabaseConfiguration(host, port, name, username, password);
        } catch (Exception e) {
            throw new ConfigurationReadException("Failed to parse dbconfig.xml", e);
        }

    }

    @Override
    public DatabaseConfiguration getDatabaseConfiguration() throws ConfigurationReadException
    {
        if (!databaseConfiguration.isPresent()) {
            databaseConfiguration = Optional.of(parseDbConfig());
        }

        return databaseConfiguration.get();
    }
}
