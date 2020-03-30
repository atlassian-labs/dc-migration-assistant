/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
     *
     * @throws {@link MigrationAlreadyExistsException} when a migration object already exists.
     */
    Migration createMigration() throws MigrationAlreadyExistsException;

    /**
     * Gets the current stage of the migration
     */
    MigrationStage getCurrentStage();

    /**
     * @param expected the migration stage that the caller expects the migration to be in
     * @throws InvalidMigrationStageError when there is a mismatch between the expected stage and the current stage
     */
    void assertCurrentStage(MigrationStage expected) throws InvalidMigrationStageError;

    /**
     * Gets the Migration Object that can only be read. Setter invocation must to happen through the {@link MigrationService} interface
     *
     * @return a read-only migration object.
     */
    Migration getCurrentMigration();


    /**
     * Tries to transition the migration state from one to another
     *
     * @param to the state you want to transition to
     * @throws InvalidMigrationStageError when the transition is invalid
     */
    void transition(MigrationStage to) throws InvalidMigrationStageError;

    /**
     * Moves the migration into an error stage
     *
     * @see MigrationStage#ERROR
     */
    void error();

}
