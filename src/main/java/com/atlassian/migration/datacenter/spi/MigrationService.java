package com.atlassian.migration.datacenter.spi;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.exceptions.MigrationAlreadyExistsException;
import com.atlassian.migration.datacenter.dto.Migration;

/**
 * Manages the lifecycle of the migration
 */
public interface MigrationService {

    /**
     * Creates a new migration in the initial stage. Using this method will create just one migration object in the database
     * <b>or</b> find the existing migration object and return it.
     * @throws {@link MigrationAlreadyExistsException} when a migration object already exists.
     */
    Migration createMigration() throws MigrationAlreadyExistsException;

    /**
     * Gets the current stage of the migration
     */
    MigrationStage getCurrentStage();


    /**
     * Gets the Migration Object that can only be read. Setter invocation must to happen through the {@link MigrationService} interface
     *
     * @return a read-only migration object.
     */
    Migration getCurrentMigration();

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
