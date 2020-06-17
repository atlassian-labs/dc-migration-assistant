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
package com.atlassian.migration.datacenter.spi

/**
 * Represents all possible states of an on-premise to cloud migration.
 */
enum class MigrationStage {
    NOT_STARTED(),
    AUTHENTICATION(NOT_STARTED),

    PROVISION_APPLICATION(AUTHENTICATION),
    PROVISION_APPLICATION_WAIT(PROVISION_APPLICATION),
    PROVISION_MIGRATION_STACK(PROVISION_APPLICATION_WAIT),
    PROVISION_MIGRATION_STACK_WAIT(PROVISION_MIGRATION_STACK),
    PROVISIONING_ERROR(PROVISION_APPLICATION, PROVISION_APPLICATION_WAIT, PROVISION_MIGRATION_STACK, PROVISION_MIGRATION_STACK_WAIT),

    FS_MIGRATION_COPY(PROVISION_MIGRATION_STACK_WAIT),
    FS_MIGRATION_COPY_WAIT(FS_MIGRATION_COPY),
    FS_MIGRATION_ERROR(FS_MIGRATION_COPY, FS_MIGRATION_COPY_WAIT),

    OFFLINE_WARNING(FS_MIGRATION_COPY_WAIT),

    DB_MIGRATION_EXPORT(OFFLINE_WARNING),
    DB_MIGRATION_EXPORT_WAIT(DB_MIGRATION_EXPORT),
    DB_MIGRATION_UPLOAD(DB_MIGRATION_EXPORT_WAIT),
    DB_MIGRATION_UPLOAD_WAIT(DB_MIGRATION_UPLOAD),
    DATA_MIGRATION_IMPORT(DB_MIGRATION_UPLOAD_WAIT),
    DATA_MIGRATION_IMPORT_WAIT(DATA_MIGRATION_IMPORT),
    FINAL_SYNC_WAIT(DATA_MIGRATION_IMPORT),
    FINAL_SYNC_ERROR(DB_MIGRATION_EXPORT, DB_MIGRATION_EXPORT_WAIT, DB_MIGRATION_UPLOAD, DB_MIGRATION_UPLOAD_WAIT, DATA_MIGRATION_IMPORT, DATA_MIGRATION_IMPORT_WAIT, FINAL_SYNC_WAIT),

    VALIDATE(FINAL_SYNC_WAIT),
    FINISHED(VALIDATE),
    ERROR();

    private val validFromStages: List<MigrationStage>
    var exception: Throwable?

    constructor() {
        exception = null;
        this.validFromStages = listOf()
    }

    constructor(vararg validFromStages: MigrationStage) {
        exception = null;
        this.validFromStages = validFromStages.asList()
    }


    fun isValidTransition(to: MigrationStage): Boolean {
        when (this) {
            PROVISIONING_ERROR -> return to == PROVISION_APPLICATION
            else -> {
            }
        }

        return when (to) {
            ERROR -> true
            else -> {
                to.validFromStages.any { it == this }
            }
        }
    }

    fun isAfter(stage: MigrationStage): Boolean {
        if (this == stage || this == NOT_STARTED) return false
        if (this == FINISHED || this == ERROR) return true
        return this.validFromStages.map {
            when (it) {
                stage -> true
                else -> it.isAfter(stage)
            }
        }.contains(true)
    }

    // Hacky, but OK for now.
    val isDBPhase: Boolean
        get() =// Hacky, but OK for now.
            this.toString().startsWith("DB_")

    fun isErrorStage(): Boolean {
        return when(this) {
            ERROR, FS_MIGRATION_ERROR, PROVISIONING_ERROR, FINAL_SYNC_ERROR -> true
            else -> false
        }
    }

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}
