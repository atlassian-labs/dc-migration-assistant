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

public class DatabaseConfiguration {
    public static DatabaseConfiguration h2() {
        return new DatabaseConfiguration(DBType.H2, "localhost", 0, "h2", "h2", "h2");
    }

    private String host;
    private String name;
    private String username;
    private String password;
    private Integer port;
    private DBType type;

    public DatabaseConfiguration(DBType type, String host, Integer port, String name, String username, String password)
    {
        this.host = host;
        this.name = name;
        this.username = username;
        this.password = password;
        this.port = port;
        this.type = type;
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

    public Integer getPort() {
        return port;
    }

    public DBType getType() {
        return type;
    }

    public enum DBType {
        POSTGRESQL,
        MYSQL,
        SQLSERVER,
        ORACLE,
        H2
    }
}
