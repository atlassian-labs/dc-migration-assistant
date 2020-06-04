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

package com.atlassian.migration.datacenter.core.fs.captor;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.migration.datacenter.dto.FileSyncRecord;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(ActiveObjectsJUnitRunner.class)
public class DefaultAttachmentSyncManagerTest {

    private ActiveObjects ao;
    private EntityManager entityManager;

    private DefaultAttachmentSyncManager sut;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    MigrationService migrationService;

    @Before
    public void setup() {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        sut = new DefaultAttachmentSyncManager(ao, migrationService);
        setupEntities();
    }

    private void setupEntities() {
        ao.migrate(Migration.class);
        ao.migrate(FileSyncRecord.class);
    }

    @Test
    public void shouldRetrieveAllCapturedFilePaths() {
        Migration migration  = givenMigrationExists();
        FileSyncRecord record = givenFileSyncRecordIsInDB("pathOne", migration);

        FileSyncRecord anotherRecord = givenFileSyncRecordIsInDB("pathTwo", migration);

        Set<FileSyncRecord> records = sut.getCapturedAttachments();
        // HashSet does not guarantee iteration order. Evaluate if we really need this or if we require a TreeSet implementation instead?
        assertThat(records, containsInAnyOrder(record, anotherRecord));
    }

    @Test
    public void shouldOnlyRetrieveCapturedFilePathsInCurrentMigration() {
        Migration migration = givenMigrationExists();

        Migration otherMigration = ao.create(Migration.class);
        otherMigration.save();

        FileSyncRecord record = givenFileSyncRecordIsInDB("happyPath", migration);
        givenFileSyncRecordIsInDB("sadPath", otherMigration);

        Set<FileSyncRecord> records = sut.getCapturedAttachments();
        assertThat(records, contains(record));
    }

    @Test
    public void shouldReturnEmptySetWhenNoMigrationInProgress() {
        givenFileSyncRecordIsInDB("test", null);

        assertEquals(0, sut.getCapturedAttachments().size());
    }

    @NotNull
    private FileSyncRecord givenFileSyncRecordIsInDB(String path, Migration migration) {
        FileSyncRecord record = ao.create(FileSyncRecord.class);
        final String pathOne = path;
        record.setFilePath(pathOne);
        record.setMigration(migration);
        record.save();
        return record;
    }

    @NotNull
    private Migration givenMigrationExists() {
        Migration migration = ao.create(Migration.class);
        migration.save();

        when(migrationService.getCurrentMigration()).thenReturn(migration);
        return migration;
    }
}