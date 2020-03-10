package com.atlassian.migration.datacenter.core.exceptions;

public class MigrationAlreadyExistsException extends Exception {
    public MigrationAlreadyExistsException(String message) {
        super(message);
    }
}
