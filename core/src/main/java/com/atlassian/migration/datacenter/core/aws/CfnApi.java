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

package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.CreateStackResponse;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;
import software.amazon.awssdk.services.cloudformation.model.Tag;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CfnApi {
    private AwsCredentialsProvider credentialsProvider;
    private RegionService regionManager;

    private Optional<CloudFormationAsyncClient> client;

    public CfnApi(AwsCredentialsProvider credentialsProvider, RegionService regionManager) {
        this.credentialsProvider = credentialsProvider;
        this.regionManager = regionManager;
        this.client = Optional.empty();
    }

    /**
     * Package private constructor to consume a CFn Async Client. Currently used for testing. This will not be called by spring as no injectable <code>CloudFormationAsyncClient</code> instance exists in the container.
     *
     * @param client An async CloudFormation client
     */
    CfnApi(CloudFormationAsyncClient client) {
        this.client = Optional.of(client);
    }

    /**
     * Lazily create a CFN client; should only be called after necessary AWS information has been provided.
     */
    private CloudFormationAsyncClient getClient() {
        if (client.isPresent()) {
            return client.get();
        }

        CloudFormationAsyncClient client = CloudFormationAsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionManager.getRegion()))
                .build();

        this.client = Optional.of(client);
        return client;
    }

    public StackStatus getStatus(String stackName) {
        Optional<Stack> stack = getStack(stackName);
        if (!stack.isPresent()) {
            throw StackInstanceNotFoundException
                    .builder()
                    .message(String.format("Stack with name %s not found", stackName))
                    .build();
        }
        return stack.get().stackStatus();
    }

    public Optional<String> provisionStack(String templateUrl, String stackName, Map<String, String> params) {
        Set<Parameter> parameters = params.entrySet()
                .stream()
                .map(e -> Parameter.builder().parameterKey(e.getKey()).parameterValue(e.getValue()).build())
                .collect(Collectors.toSet());

        Tag tag = Tag.builder()
                .key("created_by")
                .value("atlassian-dcmigration")
                .build();

        CreateStackRequest createStackRequest = CreateStackRequest.builder()
                .templateURL(templateUrl)
                .capabilities(Capability.CAPABILITY_AUTO_EXPAND, Capability.CAPABILITY_IAM)
                .stackName(stackName)
                .parameters(parameters)
                .tags(tag)
                .build();

        try {
            String stackId = this.getClient()
                    .createStack(createStackRequest)
                    .thenApply(CreateStackResponse::stackId)
                    .get();
            return Optional.ofNullable(stackId);
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

    public Optional<Stack> getStack(String stackName) {
        DescribeStacksRequest request = DescribeStacksRequest.builder()
                .stackName(stackName)
                .build();

        CompletableFuture<DescribeStacksResponse> asyncResponse = getClient()
                .describeStacks(request);

        try {
            DescribeStacksResponse response = asyncResponse.join();
            Stack stack = response.stacks().get(0);
            return Optional.ofNullable(stack);
        } catch (CompletionException | CancellationException e) {
            return Optional.empty();
        }
    }
}
