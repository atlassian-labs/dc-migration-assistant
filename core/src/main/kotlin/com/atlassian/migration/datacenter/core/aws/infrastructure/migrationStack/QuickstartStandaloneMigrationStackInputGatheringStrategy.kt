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
import com.atlassian.migration.datacenter.core.aws.infrastructure.QuickstartDeploymentService
import software.amazon.awssdk.services.cloudformation.model.Stack

open class QuickstartStandaloneMigrationStackInputGatheringStrategy(private val cfnApi: CfnApi) : MigrationStackInputGatheringStrategy {

    override fun gatherMigrationStackInputsFromApplicationStack(stack: Stack): Map<String, String> {
        val applicationStackOutputsMap = stack.outputs().associateBy({ it.outputKey() }, { it.outputValue() })

        val exportPrefix = stack.parameters().stream()
                .filter { parameter -> parameter.parameterKey() == "ExportPrefix" }
                .findFirst()
                .map { it.parameterValue() }
                .orElse("ATL-")

        val cfnExports = cfnApi.exports

        val applicationResources = cfnApi.getStackResources(stack.stackName())
        val efsId = applicationResources["ElasticFileSystem"]!!.physicalResourceId()

        return mapOf(
                "NetworkPrivateSubnet" to cfnExports["${exportPrefix}PriNets"]!!.split(",")[0],
                "EFSFileSystemId" to efsId,
                "EFSSecurityGroup" to applicationStackOutputsMap[securityGroupStackOutputKey]!!,
                "RDSSecurityGroup" to applicationStackOutputsMap[securityGroupStackOutputKey]!!,
                "RDSEndpoint" to applicationStackOutputsMap[dbEndpointAddressStackOutputKey]!!,
                "HelperInstanceType" to "c5.large",
                "HelperVpcId" to cfnExports["${exportPrefix}VPCID"]!!
        )    }
}