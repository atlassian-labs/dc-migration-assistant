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
package com.atlassian.migration.datacenter.spi

import com.atlassian.migration.datacenter.dto.Migration
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.exceptions.MigrationAlreadyExistsException

/**
 * Manages the lifecycle of the migration
 */
interface MigrationService {
    /**
     * Creates a new migration in the initial stage. Using this method will create just one migration object in the database
     * **or** find the existing migration object and return it.
     *
     * @throws [MigrationAlreadyExistsException] when a migration object already exists.
     */
    @Throws(MigrationAlreadyExistsException::class)
    fun createMigration(): Migration

    /**
     * Gets the current stage of the migration
     */
    val currentStage: MigrationStage

    /**
     * @param expected the migration stage that the caller expects the migration to be in
     * @throws InvalidMigrationStageError when there is a mismatch between the expected stage and the current stage
     */
    @Throws(InvalidMigrationStageError::class)
    fun assertCurrentStage(expected: MigrationStage)

    /**
     * Gets the Migration Object that can only be read. Setter invocation must happen through the [MigrationService] interface
     *
     * @return a read-only migration object.
     */
    val currentMigration: Migration

    /**
     * Gets the current migration context. The migration context can be used to store or query specific data
     * about this migration.
     *
     * @return The migration context Entity for this migration.
     */
    val currentContext: MigrationContext

    /**
     * Deletes all migrations and associated contexts.
     */
    fun deleteMigrations()

    /**
     * Tries to transition the migration state from one to another
     *
     * @param to the state you want to transition to
     * @throws InvalidMigrationStageError when the transition is invalid
     */
    @Throws(InvalidMigrationStageError::class)
    fun transition(to: MigrationStage)

    /**
     * Check whether migration prerequisites met.
     *
     * @return whether migration prerequisites met
     */
    val readyStatus: MigrationReadyStatus

    /**
     * Moves the migration into an error stage
     *
     * @param message a message describing the error
     *
     * @see MigrationStage.ERROR
     */
    fun error(message: String)

    /**
     * Moves the migration into an error stage, storing the cause.
     *
     * @see MigrationStage.ERROR
     */
    fun error(e: Throwable)

    /**
     * Moves the migration to the final state (if valid) and performs any cleanup.
     *
     * @see MigrationStage.FINISHED
     */
    @Throws(InvalidMigrationStageError::class)
    fun finishCurrentMigration()
}