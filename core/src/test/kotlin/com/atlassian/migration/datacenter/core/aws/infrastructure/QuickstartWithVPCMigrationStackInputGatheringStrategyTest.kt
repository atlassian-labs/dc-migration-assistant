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

package com.atlassian.migration.datacenter.core.aws.infrastructure

import com.atlassian.migration.datacenter.core.aws.CfnApi
import com.atlassian.migration.datacenter.core.aws.infrastructure.QuickstartDeploymentService.DATABASE_ENDPOINT_ADDRESS_STACK_OUTPUT_KEY
import com.atlassian.migration.datacenter.core.aws.infrastructure.QuickstartDeploymentService.SECURITY_GROUP_NAME_STACK_OUTPUT_KEY
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
            Output.builder().outputKey(SECURITY_GROUP_NAME_STACK_OUTPUT_KEY).outputValue(testSg).build(),
            Output.builder().outputKey(DATABASE_ENDPOINT_ADDRESS_STACK_OUTPUT_KEY).outputValue(testDbEndpoint).build()
    )
    private val stack = Stack.builder().outputs(mockOutputs).stackName(stackName).build()

    private val mockExportsDefaultPrefix = mapOf("ATL-PriNets" to "$testSubnet1,$testSubnet2", "ATL-VPCID" to testVpc)

    @BeforeEach
    internal fun setUp() {
        sut = QuickstartWithVPCMigrationStackInputGatheringStrategy(cfnApi)
        every { cfnApi.getStack(stackName) } returns Optional.of(stack)
        every { cfnApi.getStackResources(stackName) } returns mockRootResources
        every { cfnApi.getStackResources(jiraStackName) } returns mockJiraResources
    }

    @Test
    fun shouldGatherInputsWithDefaultPrefix() {
        every { cfnApi.exports } returns mockExportsDefaultPrefix

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