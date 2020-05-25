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
package com.atlassian.migration.datacenter.spi.fs.reporting

import com.fasterxml.jackson.annotation.JsonAutoDetect
import java.nio.file.Path

@JsonAutoDetect
class FailedFileMigration(val filePath: Path, val reason: String){

    override fun equals(obj: Any?): Boolean {
        if (obj is FailedFileMigration) {
            return filePath == obj.filePath && reason == obj.reason
        }
        return false
    }
}