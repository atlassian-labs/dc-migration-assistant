package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService;

import java.util.Map;

public class QuickstartDeploymentService implements ApplicationDeploymentService {

    @Override
    public void deployApplication(String deploymentId, Map<String, String> params) {

    }

    @Override
    public ApplicationDeploymentStatus getDeploymentStatus() {
        return null;
    }
}
