package com.atlassian.migration.datacenter.spi;

import com.atlassian.migration.datacenter.dto.Migration;

/**
 * Manages the lifecycle of the migration
 */
public interface MigrationServiceV2 {

    /**
     * Creates a new migration in the initial stage
     */
    Migration createMigration();

    /**
     * Gets the current stage of the migration
     */
    MigrationStage getCurrentStage();

    /**
     * Progresses to the next stage of the migration.
     * @see MigrationStage#getNext()
     */
    void nextStage();

    /**
     * Moves the migration into an error stage
     * @see MigrationStage#ERROR
     */
    void error();

}
