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

package com.atlassian.migration.datacenter.core.fs.captor

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.migration.datacenter.dto.FileSyncRecord
import com.atlassian.migration.datacenter.spi.MigrationService
import org.slf4j.LoggerFactory
import java.nio.file.Path

class DefaultPathCaptor(private val ao: ActiveObjects, private val migrationService: MigrationService): PathCaptor {

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultPathCaptor::class.java)
    }

    override fun commitPath(path: Path) {
        logger.debug("captured path for final sync: {}", path.toString())

        val record: FileSyncRecord = ao.create(FileSyncRecord::class.java)

        record.filePath = path.toString()
        record.migration = migrationService.currentMigration

        record.save()
    }
}