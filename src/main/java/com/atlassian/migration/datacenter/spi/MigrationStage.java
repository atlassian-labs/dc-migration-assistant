package com.atlassian.migration.datacenter.spi;

/**
 * Represents all possible states of an on-premise to cloud migration.
 */
public enum MigrationStage {
    ERROR("error"),
    FINISHED("finished"),
    CUTOVER("cutover"),
    VALIDATE("validate"),
    DB_MIGRATION_IMPORT("db_migration_down"),
    DB_MIGRATION_UPLOAD_COMPLETE("db_migration_upload_complete"), DB_MIGRATION_UPLOAD("db_migration_upload"),
    WAIT_DB_MIGRATION_EXPORT("wait_db_migration_export"), DB_MIGRATION_EXPORT("db_migration_export"),
    OFFLINE_WARNING("cutover_warning"),
    WAIT_FS_MIGRATION_COPY("wait_fs_migration_copy"), FS_MIGRATION_COPY("fs_migration_copy"),
    WAIT_PROVISION_MIGRATION_STACK("wait_provision_migration"), PROVISION_MIGRATION_STACK("provision_migration"),
    WAIT_PROVISION_APPLICATION("wait_provision_app"), PROVISION_APPLICATION("provision_app"),
    AUTHENTICATION("authentication"),
    NOT_STARTED("");

    private String key;

    MigrationStage(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
