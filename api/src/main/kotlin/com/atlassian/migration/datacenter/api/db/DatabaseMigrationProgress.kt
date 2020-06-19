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
