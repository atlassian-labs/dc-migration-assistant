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

package com.atlassian.migration.datacenter.core.aws.infrastructure.migrationStack

import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig

class MigrationStackInputGatheringStrategyFactory(
        private val withVPCStrategy: QuickstartWithVPCMigrationStackInputGatheringStrategy,
        private val standaloneStrategy: QuickstartStandaloneMigrationStackInputGatheringStrategy
) {
    fun getInputGatheringStrategy(mode: ProvisioningConfig.DeploymentMode): MigrationStackInputGatheringStrategy {
        return when(mode) {
            ProvisioningConfig.DeploymentMode.WITH_NETWORK -> withVPCStrategy
            ProvisioningConfig.DeploymentMode.STANDALONE -> standaloneStrategy
        }
    }
}