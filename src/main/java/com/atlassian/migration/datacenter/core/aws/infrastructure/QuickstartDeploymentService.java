package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService;

import java.util.Map;

public class QuickstartDeploymentService implements ApplicationDeploymentService {

    private static final String QUICKSTART_TEMPLATE_URL = "https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml";

    private CfnApi cfnApi;

    public QuickstartDeploymentService(CfnApi cfnApi) {
        this.cfnApi = cfnApi;
    }

    @Override
    public void deployApplication(String deploymentId, Map<String, String> params) {
        cfnApi.provisionStack(QUICKSTART_TEMPLATE_URL, deploymentId, params);
    }

    @Override
    public ApplicationDeploymentStatus getDeploymentStatus() {
        return null;
    }
}
