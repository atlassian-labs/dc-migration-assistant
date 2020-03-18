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

package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.exceptions.MigrationAlreadyExistsException;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.scheduler.SchedulerService;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.atlassian.migration.datacenter.spi.MigrationStage.AUTHENTICATION;
import static com.atlassian.migration.datacenter.spi.MigrationStage.ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.NOT_STARTED;
import static com.atlassian.migration.datacenter.spi.MigrationStage.PROVISION_APPLICATION;
import static com.atlassian.migration.datacenter.spi.MigrationStage.PROVISION_APPLICATION_WAIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


// We have to use the JUnit 4 API because there is no JUnit 5 active objects extension :(
@RunWith(ActiveObjectsJUnitRunner.class)
public class AWSMigrationServiceTest {

    private ActiveObjects ao;
    private EntityManager entityManager;
    private AWSMigrationService sut;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private CfnApi cfnApi;
    @Mock private FilesystemMigrationService filesystemMigrationService;
    @Mock private SchedulerService schedulerService;

    @Before
    public void setup() {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        sut = new AWSMigrationService(ao);
    }

    @Test
    public void shouldBeInNotStartedStageWhenNoMigrationsExist() {
        setupEntities();
        MigrationStage initialStage = sut.getCurrentStage();
        assertEquals(NOT_STARTED, initialStage);
    }

    @Test
    public void shouldBeAbleToGetCurrentStage() {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);

        assertEquals(AUTHENTICATION, sut.getCurrentStage());
    }

    @Test
    public void shouldTransitionWhenSourceStageIsCurrentStage() throws InvalidMigrationStageError {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);
        assertEquals(AUTHENTICATION, sut.getCurrentStage());

        sut.transition(PROVISION_APPLICATION);

        assertEquals(PROVISION_APPLICATION, sut.getCurrentStage());
    }

    @Test
    public void shouldNotTransitionWhenSourceStageIsNotCurrentStage() {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);
        assertEquals(AUTHENTICATION, sut.getCurrentStage());

        assertThrows(InvalidMigrationStageError.class, () -> sut.transition(PROVISION_APPLICATION_WAIT));
        assertEquals(sut.getCurrentStage(), AUTHENTICATION);
    }

    @Test
    public void shouldCreateMigrationInNotStarted() throws MigrationAlreadyExistsException {
        ao.migrate(Migration.class);
        Migration migration = sut.createMigration();

        assertEquals(NOT_STARTED, migration.getStage());
    }

    @Test
    public void shouldThrowExceptionWhenMigrationExistsAlready() {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);
        assertThrows(MigrationAlreadyExistsException.class, () -> sut.createMigration());
    }

    @Test
    public void shouldSetCurrentStageToErrorOnError() {
        initializeAndCreateSingleMigrationWithStage(PROVISION_APPLICATION);

        sut.error();

        assertEquals(ERROR, sut.getCurrentStage());
    }

    @Test
    public void shouldRaiseErrorOnGetCurrentMigrationWhenMoreThanOneExists(){
        initializeAndCreateSingleMigrationWithStage(MigrationStage.FS_MIGRATION_COPY_WAIT);
        initializeAndCreateSingleMigrationWithStage(ERROR);
        assertNumberOfMigrations(2);

        assertThrows(Exception.class, () -> sut.getCurrentMigration(), "Invalid State - should only be 1 migration");
    }

    @Test
    public void shouldGetCurrentMigrationWhenOneExists(){
        Migration existingMigration = initializeAndCreateSingleMigrationWithStage(MigrationStage.FS_MIGRATION_COPY_WAIT);

        Migration currentMigration = sut.getCurrentMigration();
        assertEquals(currentMigration.getID(), existingMigration.getID());
        assertEquals(currentMigration.getStage(), existingMigration.getStage());
    }

    @Test
    public void shouldCreateMigrationWhenNoneExists(){
        setupEntities();
        Migration migration = sut.getCurrentMigration();
        assertNumberOfMigrations(1);
        assertEquals(NOT_STARTED, migration.getStage());
    }

    private void assertNumberOfMigrations(int i) {
        assertEquals(i, ao.find(Migration.class).length);
    }

    private Migration initializeAndCreateSingleMigrationWithStage(MigrationStage stage) {
        setupEntities();

        Migration migration = ao.create(Migration.class);
        migration.setStage(stage);
        migration.save();

        return migration;
    }

    private void setupEntities() {
        ao.migrate(Migration.class);
        ao.migrate(MigrationContext.class);
    }
}
