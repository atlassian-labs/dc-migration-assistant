package com.atlassian.migration.datacenter.spi.infrastructure;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;

import java.util.Map;

public interface ApplicationDeploymentService {

    void deployApplication(String deploymentId, Map<String, String> params) throws InvalidMigrationStageError;

    ApplicationDeploymentStatus getDeploymentStatus();

    enum ApplicationDeploymentStatus {
        CREATE_IN_PROGRESS, CREATE_COMPLETE, CREATE_FAILED
    }
}
