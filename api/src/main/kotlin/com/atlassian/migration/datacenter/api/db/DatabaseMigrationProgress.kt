package com.atlassian.migration.datacenter.api.db

import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.MigrationStage.*
import java.time.Duration


enum class FinalSyncMigrationStatus {
    NOT_STARTED,
    FAILED,
    EXPORTING,
    UPLOADING,
    IMPORTING,
    DONE,
}


fun stageToStatus(stage: MigrationStage): FinalSyncMigrationStatus {
    // Could be data-driven, but this is clear enough.
    return when (stage) {
        NOT_STARTED,
        AUTHENTICATION,
        PROVISION_APPLICATION,
        PROVISION_APPLICATION_WAIT,
        PROVISION_MIGRATION_STACK,
        PROVISION_MIGRATION_STACK_WAIT,
        FS_MIGRATION_COPY,
        FS_MIGRATION_COPY_WAIT,
        OFFLINE_WARNING
        -> FinalSyncMigrationStatus.NOT_STARTED

        DB_MIGRATION_EXPORT,
        DB_MIGRATION_EXPORT_WAIT
        -> FinalSyncMigrationStatus.EXPORTING

        DB_MIGRATION_UPLOAD,
        DB_MIGRATION_UPLOAD_WAIT
        -> FinalSyncMigrationStatus.UPLOADING

        DATA_MIGRATION_IMPORT,
        DATA_MIGRATION_IMPORT_WAIT,
        FINAL_SYNC_WAIT
        -> FinalSyncMigrationStatus.IMPORTING

        VALIDATE,
        FINISHED
        -> FinalSyncMigrationStatus.DONE

        ERROR
        -> FinalSyncMigrationStatus.FAILED
    }
}

data class DatabaseMigrationStatus(val status: FinalSyncMigrationStatus, val elapsedTime: Duration)
