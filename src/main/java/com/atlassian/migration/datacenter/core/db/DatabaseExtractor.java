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

package com.atlassian.migration.datacenter.core.db;

import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;

import java.io.File;
import java.nio.file.Path;

public interface DatabaseExtractor
{
    Process startDatabaseDump(Path target) throws DatabaseMigrationFailure;
    Process startDatabaseDump(Path target, Boolean parallel) throws DatabaseMigrationFailure;

    void dumpDatabase(Path to) throws DatabaseMigrationFailure;
}
