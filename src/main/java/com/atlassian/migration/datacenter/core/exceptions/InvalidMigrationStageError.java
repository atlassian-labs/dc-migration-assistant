package com.atlassian.migration.datacenter.core.exceptions;

import com.atlassian.migration.datacenter.spi.MigrationStage;
import org.apache.commons.lang3.StringUtils;

public class InvalidMigrationStageError extends Exception {
    public InvalidMigrationStageError(String message) {
        super(message);
    }

    public static InvalidMigrationStageError errorWithMessage(MigrationStage expected, MigrationStage actual, String prefix) {
        String message = String.format("Attempt to migrate from `%s` to `%s`", expected, actual);
        if (StringUtils.isNotBlank(prefix)) {
            message = String.format("%s. %s", prefix, message);
        }
        return new InvalidMigrationStageError(message);
    }

    public static InvalidMigrationStageError errorWithMessage(MigrationStage from, MigrationStage to) {
        return errorWithMessage(from, to, "");
    }
}
