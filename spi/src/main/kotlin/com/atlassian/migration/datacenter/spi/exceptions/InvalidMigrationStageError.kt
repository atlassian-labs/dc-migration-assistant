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
package com.atlassian.migration.datacenter.spi.exceptions

import com.atlassian.migration.datacenter.spi.MigrationStage
import org.apache.commons.lang3.StringUtils

class InvalidMigrationStageError(message: String) : Exception(message) {
    companion object {
        fun errorWithMessage(expected: MigrationStage, actual: MigrationStage, prefix: String): InvalidMigrationStageError {
            var message = "Attempt to migrate from `$expected` to `$actual`."
            if (prefix.isNotBlank()) {
                message = String.format("%s. %s", prefix, message)
            }
            return InvalidMigrationStageError(message)
        }

        @JvmStatic  // TODO: Remove after full conversion
        fun errorWithMessage(from: MigrationStage, to: MigrationStage): InvalidMigrationStageError {
            return errorWithMessage(from, to, "")
        }
    }
}