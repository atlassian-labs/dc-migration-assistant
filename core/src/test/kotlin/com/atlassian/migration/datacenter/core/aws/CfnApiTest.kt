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

package com.atlassian.migration.datacenter.core.aws

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient
import software.amazon.awssdk.services.cloudformation.model.DescribeStackEventsRequest
import software.amazon.awssdk.services.cloudformation.model.DescribeStackEventsResponse
import software.amazon.awssdk.services.cloudformation.model.ResourceStatus
import software.amazon.awssdk.services.cloudformation.model.StackEvent
import java.time.Instant
import java.util.Optional
import java.util.concurrent.CompletableFuture

@ExtendWith(MockKExtension::class)
internal class CfnApiTest {

    @MockK
    lateinit var cfnClient: CloudFormationAsyncClient

    lateinit var sut: CfnApi

    private val testStack = "test-stack"
    private val stackResourceType = "AWS::CloudFormation::Stack"

    @BeforeEach
    internal fun setUp() {
        sut = CfnApi(cfnClient)
    }

    @Test
    fun shouldGetFailedMessagesForGivenStack() {
        givenErrorsExistForStack(testStack,
                {
                    it.resourceStatusReason("The following resource(s) failed to create: [MigrationStackResourceAccessCustom, HelperServerGroup, HelperLaunchConfig, HelperSecurityGroup].")
                            .timestamp(Instant.now())
                            .resourceType("dont matter")
                }
        )
        val error = "The following resource(s) failed to create: [MigrationStackResourceAccessCustom, HelperServerGroup, HelperLaunchConfig, HelperSecurityGroup]."

        assertEquals(error, sut.getStackErrorRootCause(testStack).get())
    }

    @Test
    fun shouldGetTheEarliestFailedMessage() {
        val earliestError = "AutoscalingGroup deletion cannot be performed because the Terminate process has been suspended; please resume this process and then retry stack deletion."

        givenErrorsExistForStack(testStack,
                {
                    it.resourceStatusReason("The following resource(s) failed to create: [MigrationStackResourceAccessCustom, HelperServerGroup, HelperLaunchConfig, HelperSecurityGroup].")
                            .timestamp(Instant.now())
                            .resourceType("dont matter")

                },
                {
                    it.resourceStatusReason(earliestError)
                            .resourceType("dont matter")
                            .timestamp(Instant.now().minusSeconds(20))
                }
        )

        assertEquals(earliestError, sut.getStackErrorRootCause(testStack).get())
    }

    @Test
    fun shouldReturnEmptyWhenNoErrorMessages() {
        givenErrorsExistForStack(testStack)

        assertEquals(Optional.empty<String>(), sut.getStackErrorRootCause(testStack))
    }

    @Test
    fun shouldReturnEmptyWhenNoStackIdIsNullOrEmpty() {
        assertEquals(Optional.empty<String>(), sut.getStackErrorRootCause(""))
        assertEquals(Optional.empty<String>(), sut.getStackErrorRootCause(null))
    }

    @Test
    fun shouldFollowEventsForFailuresInNestedStacks() {
        val earliestError = "Dashboard 'tcat-per-jira-37c49438-dashboard' already exists"
        val nestedStackId = "arn:aws:cloudformation:us-east-1:887764444972:stack/tcat-per-jira-37c49438-CloudWatchDashboard-12CC4TVSVEFF7/39c6ec60-aab2-11ea-840e-0a05dbc7583b"

        givenErrorsExistForStack(testStack, {
            it.resourceType(stackResourceType)
                    .resourceStatusReason("Embedded stack $nestedStackId was not successfully created: The following resource(s) failed to create: [Dashboard].")
                    .physicalResourceId(nestedStackId)
                    .timestamp(Instant.now())
        })

        givenErrorsExistForStack(nestedStackId,
                {
                    it
                            .resourceStatusReason(earliestError)
                            .resourceType("AWS::CloudWatch::Dashboard")
                            .timestamp(Instant.now().minusSeconds(20))
                },
                {
                    it
                            .resourceStatusReason("The following resource(s) failed to create: [Dashboard].")
                            .timestamp(Instant.now())
                            .resourceType(stackResourceType)
                })

        assertEquals(earliestError, sut.getStackErrorRootCause(testStack).get())
    }

    @Test
    fun shouldFollowEventsForMultiplyNestedStacks() {
        val nestedStack = "arn:aws:cloudformation:us-east-1:887764444972:stack/tcat-per-jira-37c49438-DB"
        val doublyNestedStack = "$nestedStack-12345512-PostgresDB-ADKLJ2135KD3"
        givenErrorsExistForStack(testStack,
                {
                    it
                            .resourceStatusReason("Embedded stack $nestedStack was not successfully created")
                            .resourceType(stackResourceType)
                            .physicalResourceId(nestedStack)
                            .timestamp(Instant.now())
                }
        )
        givenErrorsExistForStack(nestedStack,
                {
                    it
                            .resourceStatusReason("Embedded stack $doublyNestedStack was not successfully created")
                            .resourceType(stackResourceType)
                            .physicalResourceId(doublyNestedStack)
                            .timestamp(Instant.now().minusSeconds(10))
                }
        )
        val rootFailure = "Permission denied to create resource AWS::RDS::Instance"
        givenErrorsExistForStack(doublyNestedStack,
                {
                    it
                            .resourceStatusReason(rootFailure)
                            .resourceType("AWS::RDS::Instance")
                            .timestamp(Instant.now().minusSeconds(20))
                }
        )

        assertEquals(rootFailure, sut.getStackErrorRootCause(testStack).get())
    }

    private fun givenErrorsExistForStack(stackName: String, vararg builderAcceptors: (StackEvent.Builder) -> StackEvent.Builder) {
        val errors = builderAcceptors.map {
            it.invoke(
                    StackEvent.builder()
                            .resourceStatus(ResourceStatus.CREATE_FAILED))
                    .build()
        }

        every {
            cfnClient.describeStackEvents(match<DescribeStackEventsRequest> { it.stackName() == stackName })
        } returns CompletableFuture.completedFuture(DescribeStackEventsResponse.builder().stackEvents(errors).build())
    }
}