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

package com.atlassian.migration.datacenter.core.fs.captor;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.migration.datacenter.dto.FileSyncRecord;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DefaultAttachmentSyncManager implements AttachmentSyncManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAttachmentSyncManager.class);
    private final ActiveObjects activeObjects;
    private final MigrationService migrationService;

    public DefaultAttachmentSyncManager(ActiveObjects activeObjects, MigrationService migrationService) {
        this.activeObjects = activeObjects;
        this.migrationService = migrationService;
    }

    @Override
    public Set<FileSyncRecord> getCapturedAttachments() {
        Set<FileSyncRecord> records = new HashSet<>();
        Migration migration = migrationService.getCurrentMigration();

        if (migration == null) {
            return Collections.emptySet();
        }

        final FileSyncRecord[] recordsForCurrentMigration = activeObjects.find(
                FileSyncRecord.class,
                Query.select().where("MIGRATION_ID = ?", migration.getID()));

        Collections.addAll(records, recordsForCurrentMigration);

        return records;
    }

    @Override
    public Integer getCapturedAttachmentCountForCurrentMigration() {
        Migration migration = migrationService.getCurrentMigration();

        if (migration == null) {
            return 0;
        }

        return activeObjects.count(FileSyncRecord.class, Query.select().where("MIGRATION_ID = ?", migration.getID()));
    }
}
