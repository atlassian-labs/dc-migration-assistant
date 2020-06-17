package com.atlassian.migration.datacenter.api.db

import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.MigrationStage.*
import java.time.Duration


enum class DbMigrationStatus {
    NOT_STARTED,
    FAILED,
    EXPORTING,
    UPLOADING,
    IMPORTING,
    DONE,
}


fun stageToStatus(stage: MigrationStage): DbMigrationStatus {
    // Could be data-driven, but this is clear enough.
    return when (stage) {
        NOT_STARTED,
        AUTHENTICATION,
        PROVISION_APPLICATION,
        PROVISION_APPLICATION_WAIT,
        PROVISION_MIGRATION_STACK,
        PROVISION_MIGRATION_STACK_WAIT,
        PROVISIONING_ERROR,
        FS_MIGRATION_COPY,
        FS_MIGRATION_COPY_WAIT,
        FS_MIGRATION_ERROR,
        OFFLINE_WARNING
        -> DbMigrationStatus.NOT_STARTED

        DB_MIGRATION_EXPORT,
        DB_MIGRATION_EXPORT_WAIT
        -> DbMigrationStatus.EXPORTING

        DB_MIGRATION_UPLOAD,
        DB_MIGRATION_UPLOAD_WAIT
        -> DbMigrationStatus.UPLOADING

        DATA_MIGRATION_IMPORT,
        DATA_MIGRATION_IMPORT_WAIT,
        FINAL_SYNC_WAIT
        -> DbMigrationStatus.IMPORTING

        VALIDATE,
        FINISHED
        -> DbMigrationStatus.DONE

        FINAL_SYNC_ERROR,
        ERROR
        -> DbMigrationStatus.FAILED
    }
}

data class DatabaseMigrationStatus(val status: DbMigrationStatus, val elapsedTime: Duration)
