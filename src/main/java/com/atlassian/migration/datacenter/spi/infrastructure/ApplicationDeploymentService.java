package com.atlassian.migration.datacenter.spi.infrastructure;

import java.util.Map;

public interface ApplicationDeploymentService {

    void deployApplication(String deploymentId, Map<String, String> params);

    ApplicationDeploymentStatus getDeploymentStatus();

    enum ApplicationDeploymentStatus {
        CREATE_IN_PROGRESS, CREATE_COMPLETE, CREATE_FAILEd
    }
}
