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

import java.time.Duration;

@JsonSerialize(as = FileSystemMigrationReport.class)
public interface FileSystemMigrationReport extends FileSystemMigrationErrorReport, FileSystemMigrationProgress {

    void setStatus(FilesystemMigrationStatus status);

    FilesystemMigrationStatus getStatus();

    Duration getElapsedTime();

    /**
     * Text representation of filesystem migration report. This can be used to consume the report in the logs.
     * @return human readable representation of the migration report
     */
    String toString();
}
