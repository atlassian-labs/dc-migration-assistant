package com.atlassian.migration.datacenter.core.aws.infrastructure;

import cloud.localstack.docker.annotation.IEnvironmentVariableProvider;

import java.util.Map;

public class LocalStackEnvironmentVariables implements IEnvironmentVariableProvider {
    @Override
    public Map<String, String> getEnvironmentVariables() {
        return System.getenv();
    }
}
