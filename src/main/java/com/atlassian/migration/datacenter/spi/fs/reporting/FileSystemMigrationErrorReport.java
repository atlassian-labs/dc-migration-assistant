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

package com.atlassian.migration.datacenter.spi.fs.reporting;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Set;

/**
 * Represents the error status of a file system migration
 */
@JsonSerialize(as = FileSystemMigrationErrorReport.class)
public interface FileSystemMigrationErrorReport {

    /**
     * Retrieves a set containing the files which have failed to migrate.
     */
    Set<FailedFileMigration> getFailedFiles();

    /**
     * Reports that a file has failed to migrate. Implementers should be careful that the underlying
     * collection is thread safe as this may be called from multiple file upload threads.
     */
    void reportFileNotMigrated(FailedFileMigration failedFileMigration);

}
