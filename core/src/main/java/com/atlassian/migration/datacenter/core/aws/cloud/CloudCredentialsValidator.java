package com.atlassian.migration.datacenter.core.aws.cloud;

@FunctionalInterface
public interface CloudCredentialsValidator {
    Boolean validate(String accessKeyId, String secretAccessKey);
}
