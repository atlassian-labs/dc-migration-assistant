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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class MigrationStageTest {
    @Test
    fun testErrorFromAnywhere() {
        Assertions.assertTrue(MigrationStage.DB_MIGRATION_EXPORT.isValidTransition(MigrationStage.ERROR))
        Assertions.assertTrue(MigrationStage.NOT_STARTED.isValidTransition(MigrationStage.ERROR))
    }

    @Test
    fun testValidTransition() {
        Assertions.assertTrue(MigrationStage.PROVISION_APPLICATION.isValidTransition(MigrationStage.PROVISION_APPLICATION_WAIT))
        Assertions.assertTrue(MigrationStage.DB_MIGRATION_EXPORT.isValidTransition(MigrationStage.DB_MIGRATION_EXPORT_WAIT))
    }

    @Test
    fun testInvalidTransition() {
        Assertions.assertFalse(MigrationStage.DB_MIGRATION_UPLOAD.isValidTransition(MigrationStage.DB_MIGRATION_EXPORT))
    }

    @Test
    fun testIsAfterStage(){
        Assertions.assertTrue(MigrationStage.FINISHED.isAfter(MigrationStage.NOT_STARTED))
        Assertions.assertTrue(MigrationStage.VALIDATE.isAfter(MigrationStage.FINAL_SYNC_WAIT))
        Assertions.assertTrue(MigrationStage.VALIDATE.isAfter(MigrationStage.PROVISION_APPLICATION))
        Assertions.assertTrue(MigrationStage.PROVISION_MIGRATION_STACK_WAIT.isAfter(MigrationStage.PROVISION_MIGRATION_STACK))
        Assertions.assertTrue(MigrationStage.ERROR.isAfter(MigrationStage.PROVISION_APPLICATION))

        Assertions.assertFalse(MigrationStage.NOT_STARTED.isAfter(MigrationStage.FINISHED))
        Assertions.assertFalse(MigrationStage.DB_MIGRATION_EXPORT_WAIT.isAfter(MigrationStage.FINAL_SYNC_WAIT))
        Assertions.assertFalse(MigrationStage.NOT_STARTED.isAfter(MigrationStage.PROVISION_APPLICATION))
        Assertions.assertFalse(MigrationStage.FS_MIGRATION_COPY.isAfter(MigrationStage.FS_MIGRATION_COPY_WAIT))
        Assertions.assertFalse(MigrationStage.FS_MIGRATION_COPY_WAIT.isAfter(MigrationStage.FS_MIGRATION_COPY_WAIT))
    }
}