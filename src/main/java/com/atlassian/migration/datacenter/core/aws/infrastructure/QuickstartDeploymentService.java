package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationServiceV2;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class QuickstartDeploymentService implements ApplicationDeploymentService {

    private static final String QUICKSTART_TEMPLATE_URL = "https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml";

    private final CfnApi cfnApi;
    private final MigrationServiceV2 migrationService;
    private final ActiveObjects ao;

    public QuickstartDeploymentService(@ComponentImport ActiveObjects ao, CfnApi cfnApi, MigrationServiceV2 migrationService) {
        this.ao = ao;
        this.cfnApi = cfnApi;
        this.migrationService = migrationService;
    }

    /**
     * Commences the deployment of the AWS Quick Start. It will transition the state machine upon completion of the
     * deployment. If the deployment finishes successfully we transition to the next stage, otherwise we transition
     * to an error. The migration will also transition to an error if the deployment takes longer than an hour.
     * @param deploymentId the stack name
     * @param params the parameters for the cloudformation template. The key should be the parameter name and the value
     *               should be the parameter value.
     */
    @Override
    public void deployApplication(String deploymentId, Map<String, String> params) throws InvalidMigrationStageError {
        if(!migrationService.getCurrentStage().equals(MigrationStage.PROVISION_APPLICATION)) {
            throw new InvalidMigrationStageError("must be in " + MigrationStage.PROVISION_APPLICATION.toString() + " to deploy application");
        }

        cfnApi.provisionStack(QUICKSTART_TEMPLATE_URL, deploymentId, params);

        addDeploymentIdToMigrationContext(deploymentId);

        scheduleMigrationServiceTransition(deploymentId);
    }

    @Override
    public ApplicationDeploymentStatus getDeploymentStatus() {
        MigrationContext context = getMigrationContext();
        StackStatus status = cfnApi.getStatus(context.getApplicationDeploymentId());

        switch (status) {
            case CREATE_COMPLETE:
                return ApplicationDeploymentStatus.CREATE_COMPLETE;
            case CREATE_FAILED:
                return ApplicationDeploymentStatus.CREATE_FAILED;
            case CREATE_IN_PROGRESS:
                return ApplicationDeploymentStatus.CREATE_IN_PROGRESS;
            default:
                throw new RuntimeException("Unexpected stack status");
        }
    }

    private void addDeploymentIdToMigrationContext(String deploymentId) {
        MigrationContext context = getMigrationContext();
        context.setApplicationDeploymentId(deploymentId);
        context.save();
    }

    private MigrationContext getMigrationContext() {
        MigrationContext[] migrationContexts = ao.find(MigrationContext.class);
        if (migrationContexts.length == 0) {
            throw new RuntimeException("No migration context exists, are you really in a migration?");
        }
        return migrationContexts[0];
    }

    private void scheduleMigrationServiceTransition(String deploymentId) {
        CompletableFuture<String> stackCompleteFuture = new CompletableFuture<>();

        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> ticker = scheduledExecutorService.scheduleAtFixedRate(() -> {
            final StackStatus status = cfnApi.getStatus(deploymentId);
            if (status.equals(StackStatus.CREATE_COMPLETE)) {
                migrationService.nextStage();
                stackCompleteFuture.complete("");
            }
            if (status.equals(StackStatus.CREATE_FAILED)) {
                migrationService.error();
                stackCompleteFuture.complete("");
            }
        }, 0, 30, TimeUnit.SECONDS);

        ScheduledFuture<?> canceller = scheduledExecutorService.scheduleAtFixedRate(() -> {
            migrationService.error();
            ticker.cancel(true);
            // Need to have non-zero period otherwise we get illegal argument exception
        }, 1, 100, TimeUnit.HOURS);

        stackCompleteFuture.whenComplete((result, thrown) -> {
            ticker.cancel(true);
            canceller.cancel(true);
        });
    }
}
