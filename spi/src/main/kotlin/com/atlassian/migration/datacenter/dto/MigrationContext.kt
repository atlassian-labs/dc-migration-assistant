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
package com.atlassian.migration.datacenter.dto

import net.java.ao.Entity

interface MigrationContext : Entity {
    var migration: Migration
    var applicationDeploymentId: String
    var helperStackDeploymentId: String
    var serviceUrl: String
    var errorMessage: String

    var rdsRestoreSsmDocument: String
    var fsRestoreSsmDocument: String
    var fsRestoreStatusSsmDocument: String

    var migrationStackAsgIdentifier: String
    var migrationBucketName: String

    var migrationQueueUrl: String
    // AO mandates that generated field names of properties should be no longer than 30 chars.
    var migrationDLQueueUrl: String
}