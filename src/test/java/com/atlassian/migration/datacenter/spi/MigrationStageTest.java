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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MigrationStageTest
{

    @Test
    void testErrorFromAnywhere()
    {
        assertTrue(MigrationStage.isValidTransition(MigrationStage.DB_MIGRATION_EXPORT, MigrationStage.ERROR));
        assertTrue(MigrationStage.isValidTransition(MigrationStage.NOT_STARTED, MigrationStage.ERROR));
    }

    @Test
    void testValidTransition()
    {
        assertTrue(MigrationStage.isValidTransition(MigrationStage.PROVISION_APPLICATION, MigrationStage.PROVISION_APPLICATION_WAIT));
        assertTrue(MigrationStage.isValidTransition(MigrationStage.DB_MIGRATION_EXPORT, MigrationStage.DB_MIGRATION_EXPORT_WAIT));
    }

    @Test
    void testInvalidTransition()
    {
        assertFalse(MigrationStage.isValidTransition(MigrationStage.DB_MIGRATION_UPLOAD, MigrationStage.DB_MIGRATION_EXPORT));
    }
}