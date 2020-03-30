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

package com.atlassian.migration.datacenter.core.aws.auth;

import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Stack;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ProbeAWSAuth {
    private static final Logger logger = LoggerFactory.getLogger(ProbeAWSAuth.class);

    private AwsCredentialsProvider credentialsProvider;
    private RegionService regionService;

    public ProbeAWSAuth(AwsCredentialsProvider credentialsProvider, RegionService regionService) {
        this.credentialsProvider = credentialsProvider;
        this.regionService = regionService;
    }

    /**
     * Queries the Cloudformation stacks in the AWS account using the AWS Java SDK V2 to test the credentials
     * have Cloudformation access
     *
     * @return a list containing the names of the stacks in the account in the current region
     */
    public List<String> probeSDKV2() {
        CloudFormationAsyncClient client = CloudFormationAsyncClient
                .builder()
                .region(Region.of(regionService.getRegion()))
                .credentialsProvider(credentialsProvider)
                .build();

        CompletableFuture<DescribeStacksResponse> futureResponse = client.describeStacks();

        try {
            DescribeStacksResponse response = futureResponse.get();
            List<String> stackNames = response
                    .stacks()
                    .stream()
                    .map(Stack::stackName)
                    .collect(Collectors.toList());
            return stackNames;
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof CloudFormationException) {
                throw (CloudFormationException) e.getCause();
            }
            logger.error("unable to get DescribeStacksResponse", e);
            return Collections.emptyList();
        }
    }
}
