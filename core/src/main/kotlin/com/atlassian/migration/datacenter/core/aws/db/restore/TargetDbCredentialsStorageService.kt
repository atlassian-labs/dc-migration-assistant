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
package com.atlassian.migration.datacenter.core.aws.db.restore

interface TargetDbCredentialsStorageService {
    /**
     * Stores the given database password in AWS [Secrets Manager](https://aws.amazon.com/secrets-manager/)
     * to be used later to restore the database. Will be stored under the key:
     * com.atlassian.migration.db.target.[migration_deployment_id].applicationPassword
     *
     * @param password the database password
     * @throws NullPointerException if the password is null
     * @see MigrationContext.getApplicationDeploymentId
     */
    fun storeCredentials(password: String)

    val secretName: String
}