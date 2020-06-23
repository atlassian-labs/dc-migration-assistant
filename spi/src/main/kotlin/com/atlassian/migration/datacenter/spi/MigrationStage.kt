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
    NOT_STARTED {
        override val validAncestorStages = emptySet<MigrationStage>()
    },
    AUTHENTICATION {
        override val validAncestorStages = setOf(NOT_STARTED)
    },
    PROVISION_APPLICATION {
        override val validAncestorStages = setOf(AUTHENTICATION)
    },
    PROVISION_APPLICATION_WAIT {
        override val validAncestorStages = setOf(PROVISION_APPLICATION)
    },
    PROVISION_MIGRATION_STACK {
        override val validAncestorStages = setOf(PROVISION_APPLICATION_WAIT)
    },
    PROVISION_MIGRATION_STACK_WAIT {
        override val validAncestorStages = setOf(PROVISION_MIGRATION_STACK)
    },
    PROVISIONING_ERROR {
        override val validAncestorStages = setOf(PROVISION_APPLICATION, PROVISION_APPLICATION_WAIT, PROVISION_MIGRATION_STACK, PROVISION_MIGRATION_STACK_WAIT)
    },
    FS_MIGRATION_COPY {
        override val validAncestorStages = setOf(PROVISION_MIGRATION_STACK_WAIT)
    },
    FS_MIGRATION_COPY_WAIT {
        override val validAncestorStages = setOf(FS_MIGRATION_COPY)
    },
    FS_MIGRATION_ERROR {
        override val validAncestorStages = setOf(FS_MIGRATION_COPY, FS_MIGRATION_COPY_WAIT)
    },
    OFFLINE_WARNING {
        override val validAncestorStages = setOf(FS_MIGRATION_COPY_WAIT)
    },

    DB_MIGRATION_EXPORT {
        override val validAncestorStages
            get() = setOf(OFFLINE_WARNING, FINAL_SYNC_ERROR)
    },
    DB_MIGRATION_EXPORT_WAIT {
        override val validAncestorStages = setOf(DB_MIGRATION_EXPORT)
    },
    DB_MIGRATION_UPLOAD {
        override val validAncestorStages = setOf(DB_MIGRATION_EXPORT_WAIT)
    },
    DB_MIGRATION_UPLOAD_WAIT {
        override val validAncestorStages = setOf(DB_MIGRATION_UPLOAD)
    },
    DATA_MIGRATION_IMPORT {
        override val validAncestorStages = setOf(DB_MIGRATION_UPLOAD_WAIT)
    },
    DATA_MIGRATION_IMPORT_WAIT {
        override val validAncestorStages = setOf(DATA_MIGRATION_IMPORT)
    },
    FINAL_SYNC_WAIT {
        override val validAncestorStages
            get() = setOf(DATA_MIGRATION_IMPORT_WAIT, FINAL_SYNC_ERROR)
    },
    FINAL_SYNC_ERROR {
        override val validAncestorStages = setOf(DB_MIGRATION_EXPORT, DB_MIGRATION_EXPORT_WAIT, DB_MIGRATION_UPLOAD, DB_MIGRATION_UPLOAD_WAIT, DATA_MIGRATION_IMPORT, DATA_MIGRATION_IMPORT_WAIT, FINAL_SYNC_WAIT)
    },
    VALIDATE {
        override val validAncestorStages = setOf(FINAL_SYNC_WAIT)
    },
    FINISHED {
        override val validAncestorStages = setOf(VALIDATE)
    },
    ERROR {
        override val validAncestorStages = emptySet<MigrationStage>()
    };

    var exception: Throwable? = null

    abstract val validAncestorStages: Set<MigrationStage>

    fun isValidTransition(to: MigrationStage): Boolean {
        when (this) {
            PROVISIONING_ERROR -> return to == PROVISION_APPLICATION
            else -> {
            }
        }

        return when (to) {
            ERROR -> true
            else -> {
                to.validAncestorStages.any { it == this }
            }
        }
    }


    fun isAfter(stage: MigrationStage): Boolean {
        return isAfter(stage, mutableSetOf())
    }

    private fun isAfter(stage: MigrationStage, visited: MutableSet<MigrationStage>): Boolean {
        if (this == stage || this == NOT_STARTED) return false
        if (this == FINISHED || this == ERROR) return true
        // If we encounter a cycle and haven't found the stage return false
        if (visited.contains(stage)) return false
        return this.validAncestorStages.map {
            when (it) {
                stage -> true
                else -> {
                    visited.add(it)
                    return it.isAfter(stage, visited)
                }
            }
        }.contains(true)
    }

    // Hacky, but OK for now.
    val isDBPhase: Boolean
        get() =// Hacky, but OK for now.
            this.toString().startsWith("DB_")

    fun isErrorStage(): Boolean {
        return when (this) {
            ERROR, FS_MIGRATION_ERROR, PROVISIONING_ERROR, FINAL_SYNC_ERROR -> true
            else -> false
        }
    }

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}
