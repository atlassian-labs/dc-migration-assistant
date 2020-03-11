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
     * @return the stage that the current migration is up to.
     * @see MigrationStage
     */
    MigrationStage getMigrationStage();

    boolean startFilesystemMigration();
}
