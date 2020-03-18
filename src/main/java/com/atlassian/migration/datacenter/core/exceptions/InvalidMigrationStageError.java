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
