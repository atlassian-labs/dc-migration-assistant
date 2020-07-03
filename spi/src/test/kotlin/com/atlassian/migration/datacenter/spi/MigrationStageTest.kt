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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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
        assertFalse(DB_MIGRATION_UPLOAD.isValidTransition(DB_MIGRATION_EXPORT))
    }

    @Test
    fun shouldAllowTransitionFromStageSpecificErrorStageToStageSpecificStartStage() {
        assertTrue(PROVISIONING_ERROR.isValidTransition(PROVISION_APPLICATION))
        assertFalse(PROVISIONING_ERROR.isValidTransition(PROVISION_APPLICATION_WAIT))

        assertTrue(FS_MIGRATION_ERROR.isValidTransition(FS_MIGRATION_COPY))
        assertFalse(FS_MIGRATION_ERROR.isValidTransition(FS_MIGRATION_COPY_WAIT))

        assertTrue(FINAL_SYNC_ERROR.isValidTransition(DB_MIGRATION_EXPORT))
        assertTrue(FINAL_SYNC_ERROR.isValidTransition(FINAL_SYNC_WAIT))
        assertFalse(FS_MIGRATION_ERROR.isValidTransition(DB_MIGRATION_EXPORT_WAIT))
        assertFalse(FS_MIGRATION_ERROR.isValidTransition(VALIDATE))
    }

    @Test
    fun testIsAfterStage(){
        assertTrue(FINISHED.isAfter(NOT_STARTED))
        assertTrue(VALIDATE.isAfter(FINAL_SYNC_WAIT))
        assertTrue(VALIDATE.isAfter(PROVISION_APPLICATION))
        assertTrue(PROVISION_MIGRATION_STACK_WAIT.isAfter(PROVISION_MIGRATION_STACK))
        assertTrue(ERROR.isAfter(PROVISION_APPLICATION))

        assertFalse(NOT_STARTED.isAfter(FINISHED))
        assertFalse(DB_MIGRATION_EXPORT_WAIT.isAfter(FINAL_SYNC_WAIT))
        assertFalse(NOT_STARTED.isAfter(PROVISION_APPLICATION))
        assertFalse(FS_MIGRATION_COPY.isAfter(FS_MIGRATION_COPY_WAIT))
        assertFalse(FS_MIGRATION_COPY_WAIT.isAfter(FS_MIGRATION_COPY_WAIT))
    }

    @Test
    fun testIsErrorStage(){
        assertTrue(ERROR.isErrorStage())
        assertTrue(PROVISIONING_ERROR.isErrorStage())
        assertTrue(FS_MIGRATION_ERROR.isErrorStage())
        assertTrue(FINAL_SYNC_ERROR.isErrorStage())
        assertFalse(NOT_STARTED.isErrorStage())
        assertFalse(VALIDATE.isErrorStage())
    }
}