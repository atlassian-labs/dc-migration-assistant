package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.MigrationServiceV2;
import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.Map;
import java.util.concurrent.Executors;

public class QuickstartDeploymentService implements ApplicationDeploymentService {

    private static final String QUICKSTART_TEMPLATE_URL = "https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml";

    private CfnApi cfnApi;

    private MigrationServiceV2 migrationService;

    public QuickstartDeploymentService(CfnApi cfnApi, MigrationServiceV2 migrationService) {
        this.cfnApi = cfnApi;
        this.migrationService = migrationService;
    }

    @Override
    public void deployApplication(String deploymentId, Map<String, String> params) {
        cfnApi.provisionStack(QUICKSTART_TEMPLATE_URL, deploymentId, params);

        Executors.newFixedThreadPool(1).submit(() -> {
            while (true) {
                final StackStatus status = cfnApi.getStatus(deploymentId);
                if (status.equals(StackStatus.CREATE_COMPLETE)) {
                    migrationService.nextStage();
                    return;
                }
                if (status.equals(StackStatus.CREATE_FAILED)) {
                    migrationService.error();
                    return;
                }
            }
        });
    }

    @Override
    public ApplicationDeploymentStatus getDeploymentStatus() {
        return null;
    }
}
