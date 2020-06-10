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
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@ExtendWith(MockKExtension::class)
internal class MigrationStackInputGatheringStrategyFactoryTest {

    @MockK
    lateinit var standaloneStrategy: QuickstartStandaloneMigrationStackInputGatheringStrategy
    @MockK
    lateinit var withVpcStrategy: QuickstartWithVPCMigrationStackInputGatheringStrategy
    @InjectMockKs
    lateinit var sut: MigrationStackInputGatheringStrategyFactory

    @ParameterizedTest
    @EnumSource(value = ProvisioningConfig.DeploymentMode::class, names = ["WITH_NETWORK", "STANDALONE"])
    fun shouldReturnCorrectStrategy(mode: ProvisioningConfig.DeploymentMode) {
        when(mode) {
            ProvisioningConfig.DeploymentMode.WITH_NETWORK -> assertEquals(QuickstartWithVPCMigrationStackInputGatheringStrategy::class, sut.getInputGatheringStrategy(mode)::class)
            ProvisioningConfig.DeploymentMode.STANDALONE -> assertEquals(QuickstartStandaloneMigrationStackInputGatheringStrategy::class, sut.getInputGatheringStrategy(mode)::class)
        }
    }
}