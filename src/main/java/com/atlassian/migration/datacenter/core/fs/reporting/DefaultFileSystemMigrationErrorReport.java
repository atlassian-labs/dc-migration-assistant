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

package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages files which have had an error throughout the file migration
 * This class is threadsafe and is intended to be added to by any agents which
 * are a part of the file system migration
 */
public class DefaultFileSystemMigrationErrorReport implements FileSystemMigrationErrorReport {

    private final Set<FailedFileMigration> failedMigrations;

    public DefaultFileSystemMigrationErrorReport() {
        this.failedMigrations = ConcurrentHashMap.newKeySet();
    }

    /**
     * tries to report a failed file migration. If 100 files have failed already the report will be dropped to save memory.
     * In this scenario it is likely the migration will fail and that the lost errors will have the same cause.
     *
     * @param failedFileMigration the failed file migration to report
     */
    public void reportFileNotMigrated(FailedFileMigration failedFileMigration) {
        if (failedMigrations.size() >= 100) {
            return;
        }
        failedMigrations.add(failedFileMigration);
    }

    /**
     * @return an immutable copy of the FailedFileMigrations in this report. Note the returned value
     * is not backed by the underlying collection so will not be updated as other producers add to it.
     */
    public Set<FailedFileMigration> getFailedFiles() {
        return ImmutableSet.copyOf(failedMigrations);
    }
}
