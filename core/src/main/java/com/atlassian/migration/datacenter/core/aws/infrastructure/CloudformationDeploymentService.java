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

package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Superclass for classes which manage the deployment of cloudformation templates.
 */
public abstract class CloudformationDeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(CloudformationDeploymentService.class);

    private final CfnApi cfnApi;
    private int deployStatusPollIntervalSeconds;

    CloudformationDeploymentService(CfnApi cfnApi) {
        this(cfnApi, 30);
    }

    CloudformationDeploymentService(CfnApi cfnApi, int deployStatusPollIntervalSeconds) {
        this.cfnApi = cfnApi;
        this.deployStatusPollIntervalSeconds = deployStatusPollIntervalSeconds;
    }

    /**
     * Method that will be called if the deployment {@link this#deployCloudformationStack(String, String, Map)} fails
     */
    protected abstract void handleSuccessfulDeployment();

    /**
     * Method that will be called if the deployment succeeds
     */
    protected abstract void handleFailedDeployment();

    /**
     * Deploys a cloudformation stack and starts a thread to monitor the deployment.
     *
     * @param templateUrl the S3 url of the cloudformation template to deploy
     * @param stackName   the name for the cloudformation stack
     * @param params      the parameters for the cloudformation template
     */
    protected void deployCloudformationStack(String templateUrl, String stackName, Map<String, String> params) {
        cfnApi.provisionStack(templateUrl, stackName, params);
        beginWatchingDeployment(stackName);
    }

    protected InfrastructureDeploymentStatus getDeploymentStatus(String stackName) {
        requireNonNull(stackName);
        StackStatus status = cfnApi.getStatus(stackName);

        switch (status) {
            case CREATE_COMPLETE:
                return InfrastructureDeploymentStatus.CREATE_COMPLETE;
            case CREATE_FAILED:
                return InfrastructureDeploymentStatus.CREATE_FAILED;
            case CREATE_IN_PROGRESS:
                return InfrastructureDeploymentStatus.CREATE_IN_PROGRESS;
            default:
                throw new RuntimeException("Unexpected stack status");
        }
    }

    private void beginWatchingDeployment(String stackName) {
        CompletableFuture<String> stackCompleteFuture = new CompletableFuture<>();

        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> ticker = scheduledExecutorService.scheduleAtFixedRate(() -> {
            final StackStatus status = cfnApi.getStatus(stackName);
            if (status.equals(StackStatus.CREATE_COMPLETE)) {
                logger.info("stack {} creation succeeded", stackName);
                handleSuccessfulDeployment();
                stackCompleteFuture.complete("");
            }
            if (isFailedStatus(status)) {
                logger.error("stack {} creation failed", stackName);
                handleFailedDeployment();
                stackCompleteFuture.complete("");
            }
        }, 0, deployStatusPollIntervalSeconds, TimeUnit.SECONDS);

        ScheduledFuture<?> canceller = scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.error("timed out while waiting for stack {} to deploy", stackName);
            handleFailedDeployment();
            ticker.cancel(true);
            // Need to have non-zero period otherwise we get illegal argument exception
        }, 1, 100, TimeUnit.HOURS);

        stackCompleteFuture.whenComplete((result, thrown) -> {
            ticker.cancel(true);
            canceller.cancel(true);
        });
    }

    private boolean isFailedStatus(StackStatus status) {
        return status.equals(StackStatus.CREATE_FAILED) ||
                status.equals(StackStatus.ROLLBACK_COMPLETE) ||
                status.equals(StackStatus.ROLLBACK_FAILED);
    }
}
