package com.atlassian.migration.datacenter.spi;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.exceptions.MigrationAlreadyExistsException;
import com.atlassian.migration.datacenter.dto.Migration;

/**
 * Manages the lifecycle of the migration
 */
public interface MigrationServiceV2 {

    /**
     * Creates a new migration in the initial stage
     */
    Migration createMigration() throws MigrationAlreadyExistsException;

    /**
     * Gets the current stage of the migration
     */
    MigrationStage getCurrentStage();

    /**
     * Tries to transition the migration state from one to another
     * @param from the state you are expected to be in currently when beginning the transition
     * @param to the state you want to transition to
     * @throws InvalidMigrationStageError when the transition is invalid
     */
    void transition(MigrationStage from, MigrationStage to) throws InvalidMigrationStageError;

    /**
     * Moves the migration into an error stage
     * @see MigrationStage#ERROR
     */
    void error();

}
