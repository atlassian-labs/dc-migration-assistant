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
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.cloudformation.model.Output
import software.amazon.awssdk.services.cloudformation.model.Stack
import software.amazon.awssdk.services.cloudformation.model.StackResource
import java.util.Optional

@ExtendWith(MockKExtension::class)
internal class QuickstartWithVPCMigrationStackInputGatheringStrategyTest {

    lateinit var sut: QuickstartWithVPCMigrationStackInputGatheringStrategy

    @MockK
    lateinit var cfnApi: CfnApi

    private val stackName = "my-stack"

    private val testSg = "test-sg"
    private val testDbEndpoint = "my-db.com"
    private val testSubnet1 = "subnet-123"
    private val testSubnet2 = "subnet-456"
    private val testVpc = "vpc-01234"
    private val testEfs = "fs-1234"

    private val jiraStackName = "my-jira-stack"
    private val mockRootResources = mapOf("JiraDCStack" to StackResource.builder().physicalResourceId(jiraStackName).build())
    private val mockJiraResources = mapOf("ElasticFileSystem" to StackResource.builder().physicalResourceId(testEfs).build())
    private val mockOutputs = listOf(
            Output.builder().outputKey(securityGroupStackOutputKey).outputValue(testSg).build(),
            Output.builder().outputKey(dbEndpointAddressStackOutputKey).outputValue(testDbEndpoint).build()
    )
    private val stack = Stack.builder().stackName(stackName).build()
    private val jiraStack = Stack.builder().stackName(jiraStackName).outputs(mockOutputs).build()

    private val mockExportsDefaultPrefix = mapOf("ATL-PriNets" to "$testSubnet1,$testSubnet2", "ATL-VPCID" to testVpc)

    @BeforeEach
    internal fun setUp() {
        sut = QuickstartWithVPCMigrationStackInputGatheringStrategy(cfnApi, QuickstartStandaloneMigrationStackInputGatheringStrategy(cfnApi))
        every { cfnApi.exports } returns mockExportsDefaultPrefix
        every { cfnApi.getStackResources(stackName) } returns mockRootResources
        every { cfnApi.getStackResources(jiraStackName) } returns mockJiraResources
        every { cfnApi.getStack(jiraStackName) } returns Optional.of(jiraStack)
    }

    @Test
    fun shouldGatherInputsWithDefaultPrefix() {
        val params = sut.gatherMigrationStackInputsFromApplicationStack(stack)

        val expectation = mapOf(
                "NetworkPrivateSubnet" to testSubnet1,
                "EFSFileSystemId" to testEfs,
                "EFSSecurityGroup" to testSg,
                "RDSSecurityGroup" to testSg,
                "RDSEndpoint" to testDbEndpoint,
                "HelperInstanceType" to "c5.large",
                "HelperVpcId" to testVpc
        )

        assertEquals(expectation, params)
    }
}