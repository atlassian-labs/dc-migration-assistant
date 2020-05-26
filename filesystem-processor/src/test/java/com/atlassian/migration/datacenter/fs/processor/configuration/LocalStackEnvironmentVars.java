package com.atlassian.migration.datacenter.fs.processor.configuration;

import cloud.localstack.docker.annotation.IEnvironmentVariableProvider;

import java.util.Map;

public class LocalStackEnvironmentVars implements IEnvironmentVariableProvider {
    @Override
    public Map<String, String> getEnvironmentVariables() {
        return System.getenv();
    }
}
