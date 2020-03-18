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

import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
@ExtendWith({LocalstackDockerExtension.class})
@LocalstackDockerProperties(services = {"cloudformation", "s3"}, imageTag = "0.10.8")
class CfnApiIT {

    private CfnApi cfnApi;

    static final URI LOCALSTACK_CLOUDFORMATION_URI = URI.create("http://localhost:4581");
    static final URI LOCALSTACK_S3_URI = URI.create("http://localhost:4572");
    static final String CFN_TEMPLATE_S3_BUCKET_CREATE_JSON = "/cfn/create_s3_bucket.json";

    static final String S3_BUCKET_NAME = "dcd-slingshot-templates";
    static final String S3_TEMPLATE_BUCKET_KEY = "create_s3_bucket.json";
    static final String S3_CFN_STACK_URL = String.format("%s/%s/%s", LOCALSTACK_S3_URI.toString(), S3_BUCKET_NAME, S3_TEMPLATE_BUCKET_KEY);

    @BeforeAll
    static void beforeAll() throws IOException {
        S3Client s3Client = S3Client.builder()
                .region(Region.AP_SOUTHEAST_2)
                .endpointOverride(LOCALSTACK_S3_URI)
                .credentialsProvider(new StubAwsCredentialsProvider())
                .build();

        s3Client.createBucket(CreateBucketRequest.builder().bucket(S3_BUCKET_NAME).build());

        InputStream inputStream = CfnApiIT.class.getResourceAsStream(CFN_TEMPLATE_S3_BUCKET_CREATE_JSON);
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer);


        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(S3_BUCKET_NAME).key(S3_TEMPLATE_BUCKET_KEY).acl(ObjectCannedACL.PUBLIC_READ).build();
        s3Client.putObject(putObjectRequest, RequestBody.fromString(writer.toString()));

        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(GetObjectRequest.builder().bucket(S3_BUCKET_NAME).key(S3_TEMPLATE_BUCKET_KEY).build());

        assertEquals(object.response().contentLength(), writer.toString().length());
    }

    @BeforeEach
    void setUp() {
        CloudFormationAsyncClient client = CloudFormationAsyncClient.builder()
                .credentialsProvider(new StubAwsCredentialsProvider())
                .region(Region.AP_SOUTHEAST_2)
                .endpointOverride(LOCALSTACK_CLOUDFORMATION_URI)
                .build();
        cfnApi = new CfnApi(client);
    }

    @Test
    public void shouldRaiseExceptionWhenStackDoesNotExist() {
        String stackArn = "arn:aws:cloudformation:ap-southeast-2:1231231231:stack/i-do-not-exist";
        Assertions.assertThrows(StackInstanceNotFoundException.class, () -> {
            cfnApi.getStatus(stackArn);
        });
    }

    @Test
    void shouldProvisionNewCfnStack() {
        String random = UUID
                .randomUUID().toString().split("-")[0];
        String stackName = String.format("trebuchet-test-%s", random);
        Optional<String> provisionedStackId = cfnApi.provisionStack(S3_CFN_STACK_URL, stackName, new HashMap<String, String>() {{
            put("S3BucketName", stackName + "-bucket");
        }});

        String stackId = provisionedStackId.get();
        assertNotNull(stackId);

        try {
            awaitStackCreation(stackName, cfnApi::getStack).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            Assertions.fail("Timeout while waiting for stack creation to complete", e);
        }
    }

    private CompletableFuture<String> awaitStackCreation(String stackId, Function<String, Optional<Stack>> statusFunc) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        ScheduledFuture<?> scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Optional<Stack> stack = statusFunc.apply(stackId);
            if (stack.isPresent()) {
                if (Objects.equals(stack.get().stackStatus(), StackStatus.CREATE_COMPLETE)) {
                    completableFuture.complete(stackId);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);

        completableFuture.whenComplete((result, thrown) -> {
            scheduledFuture.cancel(true);
        });

        return completableFuture;
    }
}