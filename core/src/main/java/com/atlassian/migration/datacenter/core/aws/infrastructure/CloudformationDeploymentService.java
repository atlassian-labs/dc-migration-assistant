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
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Superclass for classes which manage the deployment of cloudformation
 * templates.
 */
public abstract class CloudformationDeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(CloudformationDeploymentService.class);

    private final CfnApi cfnApi;
    private final int deployStatusPollIntervalSeconds;
    private final MigrationService migrationService;
    private ScheduledFuture<?> deploymentWatcher;

    CloudformationDeploymentService(CfnApi cfnApi, MigrationService migrationService) {
        this(cfnApi, 30, migrationService);
    }

    CloudformationDeploymentService(CfnApi cfnApi, int deployStatusPollIntervalSeconds, MigrationService migrationService) {
        this.cfnApi = cfnApi;
        this.migrationService = migrationService;
        this.deployStatusPollIntervalSeconds = deployStatusPollIntervalSeconds;
    }

    /**
     * Method that will be called if the deployment
     * {@link this#deployCloudformationStack(String, String, Map)} fails
     */
    protected abstract void handleSuccessfulDeployment();

    /**
     * Method that will be called if the deployment succeeds
     */
    protected abstract void handleFailedDeployment(String error);

    /**
     * Deploys a cloudformation stack and starts a thread to monitor the deployment. If the deployment succeeds,
     * the {@link CloudformationDeploymentService#handleSuccessfulDeployment()} callback will be invoked. If the
     * deployment fails, the {@link CloudformationDeploymentService#handleFailedDeployment(String)} will be invoked
     * with the error message as the value.
     *
     * @param templateUrl the S3 url of the cloudformation template to deploy
     * @param stackName   the name for the cloudformation stack
     * @param params      the parameters for the cloudformation template
     *
     * @return a future which is completed when the deployment finishes (successfully or otherwise). It is provided only
     * for timing purposes. Handling the finished deployment should be done in the implementation of
     * {@link CloudformationDeploymentService#handleSuccessfulDeployment()} and
     * {@link CloudformationDeploymentService#handleFailedDeployment(String)}
     */
    protected CompletableFuture<?> deployCloudformationStack(String templateUrl, String stackName, Map<String, String> params) throws InfrastructureDeploymentError {
        cfnApi.provisionStack(templateUrl, stackName, params);
        return beginWatchingDeployment(stackName);
    }

    protected InfrastructureDeploymentState getDeploymentStatus() {
        return migrationService.getCurrentContext().getDeploymentState();
    }

    private CompletableFuture<String> beginWatchingDeployment(String stackName) {
        CompletableFuture<String> stackCompleteFuture = new CompletableFuture<>();

        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        deploymentWatcher = scheduledExecutorService.scheduleAtFixedRate(() -> {
            final InfrastructureDeploymentState status = cfnApi.getStatus(stackName);
            MigrationContext context = migrationService.getCurrentContext();
            context.setDeploymentState(status);
            context.save();
            if (status.equals(InfrastructureDeploymentState.CREATE_COMPLETE)) {
                logger.info("stack {} creation succeeded", stackName);
                handleSuccessfulDeployment();
                stackCompleteFuture.complete("");
            }
            if (isFailedToCreateDeploymentState(status)) {
                //FIXME: implement getting a good error
                String reason = cfnApi.getStackErrorRootCause(stackName).orElse("Deployment failed for unknown reason. Try checking the cloudformation console");
                logger.error("stack {} creation failed with reason {}", stackName, reason);
                handleFailedDeployment(reason);
                stackCompleteFuture.complete("");
            }
        }, 0, deployStatusPollIntervalSeconds, TimeUnit.SECONDS);

        ScheduledFuture<?> canceller = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (deploymentWatcher.isCancelled()) {
                return;
            }
            String message = String.format("timed out while waiting for stack %s to deploy", stackName);
            logger.error(message);
            handleFailedDeployment(message);
            deploymentWatcher.cancel(true);
            // Need to have non-zero period otherwise we get illegal argument exception
        }, 1, 100, TimeUnit.HOURS);

        stackCompleteFuture.whenComplete((result, thrown) -> {
            deploymentWatcher.cancel(true);
            canceller.cancel(true);
        });

        return stackCompleteFuture;
    }

    private boolean isFailedToCreateDeploymentState(InfrastructureDeploymentState state) {
        return !(InfrastructureDeploymentState.CREATE_COMPLETE.equals(state) ||
                InfrastructureDeploymentState.CREATE_IN_PROGRESS.equals(state));
    }
}
