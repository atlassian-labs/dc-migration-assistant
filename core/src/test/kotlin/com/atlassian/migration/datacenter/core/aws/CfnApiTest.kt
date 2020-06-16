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

    @BeforeEach
    internal fun setUp() {
        sut = CfnApi(cfnClient)
    }

    @Test
    fun shouldGetFailedMessagesForGivenStack() {
        val stackEvents = listOf<StackEvent>(
                StackEvent.builder()
                        .resourceStatus(ResourceStatus.CREATE_FAILED)
                        .resourceStatusReason("The following resource(s) failed to create: [MigrationStackResourceAccessCustom, HelperServerGroup, HelperLaunchConfig, HelperSecurityGroup].")
                        .timestamp(Instant.now())
                        .build()
        )

        every {
            cfnClient.describeStackEvents(match<DescribeStackEventsRequest> { it.stackName() == testStack })
        } returns
                CompletableFuture.completedFuture(DescribeStackEventsResponse.builder().stackEvents(stackEvents).build())

        val error = "The following resource(s) failed to create: [MigrationStackResourceAccessCustom, HelperServerGroup, HelperLaunchConfig, HelperSecurityGroup]."

        assertEquals(error, sut.getStackErrorRootCause(testStack).get())
    }

    @Test
    fun shouldGetTheEarliestFailedMessage() {
        val earliestError = "AutoscalingGroup deletion cannot be performed because the Terminate process has been suspended; please resume this process and then retry stack deletion."
        val stackEvents = listOf<StackEvent>(
                StackEvent.builder()
                        .resourceStatus(ResourceStatus.CREATE_FAILED)
                        .resourceStatusReason("The following resource(s) failed to create: [MigrationStackResourceAccessCustom, HelperServerGroup, HelperLaunchConfig, HelperSecurityGroup].")
                        .timestamp(Instant.now())
                        .build(),
                StackEvent.builder()
                        .resourceStatus(ResourceStatus.CREATE_FAILED)
                        .resourceStatusReason(earliestError)
                        .timestamp(Instant.now().minusSeconds(20))
                        .build()
        )

        every {
            cfnClient.describeStackEvents(match<DescribeStackEventsRequest> { it.stackName() == testStack })
        } returns
                CompletableFuture.completedFuture(DescribeStackEventsResponse.builder().stackEvents(stackEvents).build())

        assertEquals(earliestError, sut.getStackErrorRootCause(testStack).get())
    }

    @Test
    fun shouldReturnEmptyWhenNoErrorMessages() {
        every {
            cfnClient.describeStackEvents(match<DescribeStackEventsRequest> { it.stackName() == testStack })
        } returns
                CompletableFuture.completedFuture(DescribeStackEventsResponse.builder().stackEvents(emptyList()).build())

        assertEquals(Optional.empty<String>(), sut.getStackErrorRootCause(testStack))
    }

    @Test
    fun shouldFollowEventsForFailuresInNestedStacks() {
        val nestedStackId = "arn:aws:cloudformation:us-east-1:887764444972:stack/tcat-per-jira-37c49438-CloudWatchDashboard-12CC4TVSVEFF7/39c6ec60-aab2-11ea-840e-0a05dbc7583b"
        val earliestError = "Dashboard 'tcat-per-jira-37c49438-dashboard' already exists"
        val stackResourceType = "AWS::CloudFormation::Stack"
        val stackEvents = listOf(
                StackEvent.builder()
                        .resourceStatus(ResourceStatus.CREATE_FAILED)
                        .resourceType(stackResourceType)
                        .resourceStatusReason("Embedded stack arn:aws:cloudformation:us-east-1:887764444972:stack/tcat-per-jira-37c49438-CloudWatchDashboard-12CC4TVSVEFF7/39c6ec60-aab2-11ea-840e-0a05dbc7583b was not successfully created: The following resource(s) failed to create: [Dashboard].")
                        .physicalResourceId(nestedStackId)
                        .timestamp(Instant.now())
                        .build()
        )
        val nestedStackEvents = listOf(
                StackEvent.builder()
                        .resourceStatus(ResourceStatus.CREATE_FAILED)
                        .resourceStatusReason(earliestError)
                        .resourceType("AWS::CloudWatch::Dashboard")
                        .timestamp(Instant.now().minusSeconds(20))
                        .build(),
                StackEvent.builder()
                        .resourceStatus(ResourceStatus.CREATE_FAILED)
                        .resourceStatusReason("The following resource(s) failed to create: [Dashboard].")
                        .timestamp(Instant.now())
                        .resourceType(stackResourceType)
                        .build()
        )

        every {
            cfnClient.describeStackEvents(match<DescribeStackEventsRequest> { it.stackName() == testStack })
        } returns
                CompletableFuture.completedFuture(DescribeStackEventsResponse.builder().stackEvents(stackEvents).build())

        every {
            cfnClient.describeStackEvents(match<DescribeStackEventsRequest> { it.stackName() == nestedStackId })
        } returns CompletableFuture.completedFuture(DescribeStackEventsResponse.builder().stackEvents(nestedStackEvents).build())

        assertEquals(earliestError, sut.getStackErrorRootCause(testStack).get())
    }
}