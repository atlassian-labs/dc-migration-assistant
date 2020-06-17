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

import com.atlassian.migration.datacenter.spi.MigrationStage.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue

internal class MigrationStageTest {
    @Test
    fun testErrorFromAnywhere() {
        assertTrue(DB_MIGRATION_EXPORT.isValidTransition(ERROR))
        assertTrue(NOT_STARTED.isValidTransition(ERROR))
    }

    @Test
    fun testValidTransition() {
        assertTrue(PROVISION_APPLICATION.isValidTransition(PROVISION_APPLICATION_WAIT))
        assertTrue(DB_MIGRATION_EXPORT.isValidTransition(DB_MIGRATION_EXPORT_WAIT))
        assertTrue(PROVISION_MIGRATION_STACK.isValidTransition(PROVISIONING_ERROR))
        assertTrue(PROVISION_APPLICATION_WAIT.isValidTransition(PROVISIONING_ERROR))
    }

    @Test
    fun testInvalidTransition() {
        Assertions.assertFalse(DB_MIGRATION_UPLOAD.isValidTransition(DB_MIGRATION_EXPORT))
    }

    @Test
    fun testIsAfterStage(){
        assertTrue(FINISHED.isAfter(NOT_STARTED))
        assertTrue(VALIDATE.isAfter(FINAL_SYNC_WAIT))
        assertTrue(VALIDATE.isAfter(PROVISION_APPLICATION))
        assertTrue(PROVISION_MIGRATION_STACK_WAIT.isAfter(PROVISION_MIGRATION_STACK))
        assertTrue(ERROR.isAfter(PROVISION_APPLICATION))

        Assertions.assertFalse(NOT_STARTED.isAfter(FINISHED))
        Assertions.assertFalse(DB_MIGRATION_EXPORT_WAIT.isAfter(FINAL_SYNC_WAIT))
        Assertions.assertFalse(NOT_STARTED.isAfter(PROVISION_APPLICATION))
        Assertions.assertFalse(FS_MIGRATION_COPY.isAfter(FS_MIGRATION_COPY_WAIT))
        Assertions.assertFalse(FS_MIGRATION_COPY_WAIT.isAfter(FS_MIGRATION_COPY_WAIT))
    }
}