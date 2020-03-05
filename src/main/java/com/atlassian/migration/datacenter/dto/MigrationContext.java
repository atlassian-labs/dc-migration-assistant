package com.atlassian.migration.datacenter.dto;

import net.java.ao.Entity;

public interface MigrationContext extends Entity {

    Migration getMigration();

    void setMigration(Migration migration);

    String getApplicationDeploymentId();

    void setApplicationDeploymentId(String id);
}
