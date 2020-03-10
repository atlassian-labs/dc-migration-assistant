package com.atlassian.migration.datacenter.core.application;

/**
 * Copyright Atlassian: 04/03/2020
 */
public class DatabaseConfiguration
{
    private String host;
    private String name;
    private String username;
    private String password;
    private Integer port;

    public DatabaseConfiguration(String host, Integer port, String name, String username, String password)
    {
        this.host = host;
        this.name = name;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public String getHost()
    {
        return host;
    }

    public String getName()
    {
        return name;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public Integer getPort()
    {
        return port;
    }
}
