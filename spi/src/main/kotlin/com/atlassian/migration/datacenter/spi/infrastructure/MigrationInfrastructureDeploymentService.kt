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
package com.atlassian.migration.datacenter.spi.infrastructure

import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError

/**
 * This service should be implemented if a migration requires any additional infrastructure to facilitate
 * the migration.
 */
interface MigrationInfrastructureDeploymentService : DeploymentService {
    /**
     * Deploys any infrastructure required to facilitate the migration. Leaves naming of the infrastructure
     * group to the implementor
     * @param params Any parameters for the deployment
     * @throws InvalidMigrationStageError when the current stage is not [com.atlassian.migration.datacenter.spi.MigrationStage.PROVISION_MIGRATION_STACK]
     */
    @Throws(InvalidMigrationStageError::class)
    fun deployMigrationInfrastructure(params: Map<String, String>)
}