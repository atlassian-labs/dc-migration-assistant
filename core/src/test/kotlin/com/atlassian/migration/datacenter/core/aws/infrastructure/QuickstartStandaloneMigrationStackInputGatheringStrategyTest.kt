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
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.cloudformation.model.Output
import software.amazon.awssdk.services.cloudformation.model.Parameter
import software.amazon.awssdk.services.cloudformation.model.Stack
import software.amazon.awssdk.services.cloudformation.model.StackResource
import java.util.Optional

@ExtendWith(MockKExtension::class)
internal class QuickstartStandaloneMigrationStackInputGatheringStrategyTest {
    lateinit var sut: QuickstartStandaloneMigrationStackInputGatheringStrategy

    @MockK
    lateinit var cfnApi: CfnApi

    private val stackName = "my-stack"

    private val testSg = "test-sg"
    private val testDbEndpoint = "my-db.com"
    private val testSubnet1 = "subnet-123"
    private val testSubnet2 = "subnet-456"
    private val testVpc = "vpc-01234"
    private val testEfs = "fs-1234"

    private val mockResources = mapOf("ElasticFileSystem" to StackResource.builder().physicalResourceId(testEfs).build())
    private val mockOutputs = listOf(
            Output.builder().outputKey(QuickstartDeploymentService.SECURITY_GROUP_NAME_STACK_OUTPUT_KEY).outputValue(testSg).build(),
            Output.builder().outputKey(QuickstartDeploymentService.DATABASE_ENDPOINT_ADDRESS_STACK_OUTPUT_KEY).outputValue(testDbEndpoint).build()
    )
    private val params = listOf(Parameter.builder().parameterKey("ExportPrefix").parameterValue("TEST-").build())

    private val stack = Stack.builder().outputs(mockOutputs).stackName(stackName).parameters(params).build()

    private val mockExports = mapOf("TEST-PriNets" to "$testSubnet1,$testSubnet2", "TEST-VPCID" to testVpc)

    @BeforeEach
    internal fun setUp() {
        sut = QuickstartStandaloneMigrationStackInputGatheringStrategy(cfnApi)
        every { cfnApi.getStack(stackName) } returns Optional.of(stack)
        every { cfnApi.getStackResources(stackName) } returns mockResources
        every { cfnApi.exports } returns mockExports
    }

    @Test
    fun shouldGatherInputs() {
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

        Assertions.assertEquals(expectation, params)
    }

}