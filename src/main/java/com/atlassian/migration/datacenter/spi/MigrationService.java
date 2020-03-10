package com.atlassian.migration.datacenter.spi;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.migration.datacenter.core.exceptions.InfrastructureProvisioningError;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;

import java.util.Optional;

/**
 * Abstraction of an on-premise to cloud migration modeled as a finite state machine.
 */
@Transactional
@Deprecated
public interface MigrationService {

    /**
     * Tries to begin an on-premise to cloud migration. The migration will only be created if a migration doesn't exist.
     *
     * @return true if the migration was created, false otherwise.
     */
    boolean startMigration();

    /**
     * @return the stage that the current migration is up to.
     * @see MigrationStage
     */
    MigrationStage getMigrationStage();

    /**
     * Provisions a CloudFormation stack.
     *
     * @param provisioningConfig contains information required to provision a stack
     * @return a stack identifier of the created stack
     */
    String provisionInfrastructure(ProvisioningConfig provisioningConfig) throws InvalidMigrationStageError, InfrastructureProvisioningError;

    Optional<String> getInfrastructureProvisioningStatus(String stackId);

    boolean startFilesystemMigration();
}
