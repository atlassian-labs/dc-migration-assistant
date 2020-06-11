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

import com.atlassian.migration.datacenter.core.aws.CfnApi
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError
import software.amazon.awssdk.services.cloudformation.model.Stack

open class QuickstartWithVPCMigrationStackInputGatheringStrategy(private val cfnApi: CfnApi, private val standaloneMigrationStackInputGatheringStrategy: QuickstartStandaloneMigrationStackInputGatheringStrategy) : MigrationStackInputGatheringStrategy {

    override fun gatherMigrationStackInputsFromApplicationStack(stack: Stack): Map<String, String> {
        val applicationResources = cfnApi.getStackResources(stack.stackName())

        val jiraStackResource = applicationResources["JiraDCStack"]
        val jiraStack = cfnApi.getStack(jiraStackResource!!.physicalResourceId())
        if (jiraStack.isPresent) {
            return standaloneMigrationStackInputGatheringStrategy.gatherMigrationStackInputsFromApplicationStack(jiraStack.get())
        }
        throw InfrastructureDeploymentError("unable to find JiraDCStack resource in with-vpc cloudformation deployment: ${stack.stackName()}")
    }
}