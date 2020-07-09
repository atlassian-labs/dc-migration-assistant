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
import com.atlassian.migration.datacenter.core.util.LogUtils;
import com.atlassian.migration.datacenter.spi.exceptions.InfrastructureProvisioningError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.CreateStackResponse;
import software.amazon.awssdk.services.cloudformation.model.DeleteStackResponse;
import software.amazon.awssdk.services.cloudformation.model.DescribeStackEventsRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStackResourcesRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStackResourcesResponse;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.ListExportsResponse;
import software.amazon.awssdk.services.cloudformation.model.ListStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.ListStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackEvent;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;
import software.amazon.awssdk.services.cloudformation.model.StackResource;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;
import software.amazon.awssdk.services.cloudformation.model.StackSummary;
import software.amazon.awssdk.services.cloudformation.model.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.awssdk.services.cloudformation.model.ResourceStatus.CREATE_FAILED;

public class CfnApi {
    private static final Logger logger = LoggerFactory.getLogger(CfnApi.class);

    /**
     * Stored client is only used for testing purposes
     */
    private final Optional<CloudFormationAsyncClient> client;

    private AwsCredentialsProvider credentialsProvider;
    private RegionService regionManager;

    public CfnApi(AwsCredentialsProvider credentialsProvider, RegionService regionManager) {
        this.credentialsProvider = credentialsProvider;
        this.regionManager = regionManager;
        this.client = Optional.empty();
    }

    /**
     * Package private constructor to consume a CFn Async Client. Currently used for testing.
     * his will not be called by spring as no injectable <code>CloudFormationAsyncClient</code> instance exists in the container.
     *
     * @param client An async CloudFormation client
     */
    CfnApi(CloudFormationAsyncClient client) {
        this.client = Optional.of(client);
    }

    /**
     * Return a client should only be called after necessary AWS information has been provided.
     */
    private CloudFormationAsyncClient getClient() {
        return client.orElseGet(
                () -> CloudFormationAsyncClient.builder()
                        .credentialsProvider(credentialsProvider)
                        .region(Region.of(regionManager.getRegion()))
                        .build());

    }

    public InfrastructureDeploymentState getStatus(String stackName) {
        Optional<Stack> stack = getStack(stackName);
        if (!stack.isPresent()) {
            throw StackInstanceNotFoundException
                    .builder()
                    .message(String.format("Stack with name %s not found", stackName))
                    .build();
        }
        Stack theStack = stack.get();
        switch (theStack.stackStatus()) {
            case CREATE_COMPLETE:
                return InfrastructureDeploymentState.CREATE_COMPLETE;
            case CREATE_IN_PROGRESS:
                return InfrastructureDeploymentState.CREATE_IN_PROGRESS;
            case DELETE_IN_PROGRESS:
                return InfrastructureDeploymentState.DELETE_IN_PROGRESS;
            case DELETE_COMPLETE:
                return InfrastructureDeploymentState.DELETE_COMPLETE;
            case DELETE_FAILED:
                return InfrastructureDeploymentState.DELETE_FAILED;
            default:
                return InfrastructureDeploymentState.CREATE_FAILED;
        }
    }

    /**
     * Tries to get the root cause of a stack creation failure. Gets the earliest, non-embedded stack, CREATE_FAILED
     * event reason.
     * @param stackName the name of the stack to get the error message for
     * @return an optional containing the error message for the first stack deployment failure event.
     */
    public Optional<String> getStackErrorRootCause(String stackName) {
        if (StringUtils.isBlank(stackName)){
            return Optional.empty();
        }
        try {
            List<StackEvent> events = this.getClient()
                    .describeStackEvents(DescribeStackEventsRequest.builder().stackName(stackName).build())
                    .get()
                    .stackEvents();

            List<StackEvent> failedEvents =  events.stream()
                    .filter(stackEvent ->
                            CREATE_FAILED.equals(stackEvent.resourceStatus()))
                    .collect(Collectors.toList());

            if (failedEvents.isEmpty()) {
                return Optional.empty();
            }

            StackEvent earliestEvent = getEarliestEventFromStackEvents(failedEvents);

            if (isEmbeddedStackError(earliestEvent)) {
                return getStackErrorRootCause(earliestEvent.physicalResourceId());
            }

            return Optional.of(earliestEvent.resourceStatusReason());

        } catch (InterruptedException | ExecutionException e) {
            logger.error("unable to get stack events", e);
            return Optional.empty();
        }
    }

    private boolean isEmbeddedStackError(StackEvent stackEvent) {
        return stackEvent.resourceType().equals("AWS::CloudFormation::Stack") && stackEvent.resourceStatusReason().contains("Embedded stack");
    }

    @NotNull
    private StackEvent getEarliestEventFromStackEvents(List<StackEvent> failedEvents) {
        final AtomicReference<StackEvent> earliestEventRef = new AtomicReference<>(failedEvents.get(0));

        failedEvents.forEach(stackEvent -> {
            if (earliestEventRef.get().timestamp().isAfter(stackEvent.timestamp())) {
                earliestEventRef.set(stackEvent);
            }
        });
        return earliestEventRef.get();
    }

    /**
     * Begins the provisioning of a cloudformation stack
     * @param templateUrl S3 URL of the template to use for deployment
     * @param stackName The name to give to the stack
     * @param params any parameters for the quickstart form
     * @return an optional containing the stack name when deployment is successful
     * @throws InfrastructureDeploymentError if the deployment is not successful. Will contain the cause of the error.
     */
    public Optional<String> provisionStack(String templateUrl, String stackName, Map<String, String> params) throws InfrastructureDeploymentError {
        logger.info("Deploying stack {} from {} with params : {}", stackName, templateUrl, LogUtils.paramsToString(params));

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
            CreateStackResponse response = this.getClient()
                    .createStack(createStackRequest)
                    .get();

            if (!response.sdkHttpResponse().isSuccessful()) {
                logger.error("create stack {} http response failed with reason: {}", stackName, response.sdkHttpResponse().statusText());
                throw new InfrastructureDeploymentError(response.sdkHttpResponse().statusText().isPresent() ? response.sdkHttpResponse().statusText().get() : "Stack creation failed for unknown reason");
            }
            logger.info("stack {} creation succeeded", stackName);
            return Optional.ofNullable(response.stackId());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deploying cloudformation stack {}", stackName, e);
            if (e.getCause() != null) {
                throw new InfrastructureDeploymentError(e.getCause().getMessage(), e.getCause());
            }
            throw new InfrastructureDeploymentError(e.getMessage(), e);
        }
    }

    public void deleteStack(String stackName) throws InfrastructureDeploymentError {
        logger.trace("received request to delete stack {}", stackName);

        try {
            DeleteStackResponse response = this.getClient()
                    .deleteStack(builder -> builder.stackName(stackName))
                    .get();
            if (!response.sdkHttpResponse().isSuccessful()) {
                throw new InfrastructureDeploymentError("error during stack delete request");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("unable to delete stack {}", stackName);
            if (!e.getCause().getClass().equals(StackInstanceNotFoundException.class)) {
                throw new InfrastructureDeploymentError("error during stack delete request", e);
            }
        }
    }

    /**
     * Gets all Cloudformation exports. If there is an error retrieving the exports, an empty map will be returned
     *
     * @return A map (of export name to export value) containing all cloudformation exports for the current region in the current account.
     */
    public Map<String, String> getExports() {
        CompletableFuture<ListExportsResponse> asyncResponse = getClient().listExports();

        try {
            ListExportsResponse response = asyncResponse.get();
            HashMap<String, String> exportsMap = new HashMap<>();
            response.exports().forEach(export -> exportsMap.put(export.name(), export.value()));
            return exportsMap;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Unable to get cloudformation exports", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Gets all resources for the given stack. If there is an error retrieving the resources, an empty map will be returned
     *
     * @param stackName the name of the stack to get the resources of
     * @return a map of the logical resource ID to the resource for all resources in the given stack
     */
    public Map<String, StackResource> getStackResources(String stackName) {
        DescribeStackResourcesRequest request = DescribeStackResourcesRequest.builder()
                .stackName(stackName)
                .build();

        CompletableFuture<DescribeStackResourcesResponse> asyncResponse = getClient().describeStackResources(request);

        try {
            DescribeStackResourcesResponse response = asyncResponse.get();
            Map<String, StackResource> resources = new HashMap<>();

            response.stackResources().forEach(resource -> resources.put(resource.logicalResourceId(), resource));
            return resources;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting stack {} resources", stackName, e);
            return Collections.emptyMap();
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
            logger.error("Error getting stack {}", stackName, e);
            return Optional.empty();
        }
    }

    /**
     * Wrapper for basic AWS `list-stacks` functionality.
     *
     * @return List of StackSummary for the current region
     */
    public List<StackSummary> listStacks() {
        ListStacksResponse stacks;
        try {
            stacks = getClient().listStacks(
                    ListStacksRequest.builder()
                            .stackStatusFilters(
                                    StackStatus.CREATE_COMPLETE,
                                    StackStatus.UPDATE_COMPLETE)
                            .build()).join();
        } catch (CompletionException | CancellationException e) {
            logger.error("Error getting stacks", e);
            return Collections.emptyList();
        }

        return stacks.stackSummaries();
    }

    /**
     * Wrapper around AWS `list-stacks` that hydrates the stack details.
     *
     * @return List of Stack
     */
    public List<Stack> listStacksFull() {
        return listStacks().stream()
                .map(summary -> getStack(summary.stackName()).orElse(null))
                .filter(stack -> stack != null)
                .collect(Collectors.toList());
    }
}
