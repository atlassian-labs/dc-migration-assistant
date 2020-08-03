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
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.datacenter.analytics.events.MigrationCompleteEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationCreatedEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationFailedEvent;
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
import com.atlassian.migration.datacenter.dto.FileSyncRecord;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.events.MigrationResetEvent;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.exceptions.MigrationAlreadyExistsException;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService;
import com.atlassian.scheduler.SchedulerService;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.atlassian.migration.datacenter.spi.MigrationStage.AUTHENTICATION;
import static com.atlassian.migration.datacenter.spi.MigrationStage.DATA_MIGRATION_IMPORT_WAIT;
import static com.atlassian.migration.datacenter.spi.MigrationStage.ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.FINAL_SYNC_ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.FINISHED;
import static com.atlassian.migration.datacenter.spi.MigrationStage.FS_MIGRATION_COPY;
import static com.atlassian.migration.datacenter.spi.MigrationStage.FS_MIGRATION_COPY_WAIT;
import static com.atlassian.migration.datacenter.spi.MigrationStage.FS_MIGRATION_ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.NOT_STARTED;
import static com.atlassian.migration.datacenter.spi.MigrationStage.PROVISIONING_ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.PROVISION_APPLICATION;
import static com.atlassian.migration.datacenter.spi.MigrationStage.PROVISION_APPLICATION_WAIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


// We have to use the JUnit 4 API because there is no JUnit 5 active objects extension :(
@RunWith(ActiveObjectsJUnitRunner.class)
public class AWSMigrationServiceTest {

    private ActiveObjects ao;
    private EntityManager entityManager;
    private AWSMigrationService sut;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private CfnApi cfnApi;
    @Mock
    private FilesystemMigrationService filesystemMigrationService;
    @Mock
    private SchedulerService schedulerService;
    @Mock
    private ApplicationConfiguration applicationConfiguration;
    @Mock
    private DatabaseExtractorFactory databaseExtractorFactory;
    @Mock
    private DatabaseExtractor databaseExtractor;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private MigrationInfrastructureCleanupService cleanupService;

    @Before
    public void setup() {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        sut = new AwsMigrationServiceWrapper(ao, applicationConfiguration, eventPublisher, cleanupService);
        setupEntities();
        when(applicationConfiguration.getPluginVersion()).thenReturn("DUMMY");
        when(databaseExtractorFactory.getExtractor()).thenReturn(databaseExtractor);
    }

    @Test
    public void shouldBeInNotStartedStageWhenNoMigrationsExist() {
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
        Migration migration = sut.createMigration();
        verify(eventPublisher).publish(any(MigrationCreatedEvent.class));

        assertEquals(NOT_STARTED, migration.getStage());
    }

    @Test
    public void shouldThrowExceptionWhenMigrationExistsAlready() {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);
        assertThrows(MigrationAlreadyExistsException.class, () -> sut.createMigration());
    }

    @Test
    public void shouldHaveBidirectionalRelationshipBetweenMigrationContextAndMigration() {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);

        Migration migration = sut.getCurrentMigration();
        MigrationContext context = migration.getContext();

        final String testDeploymentId = "test-id";
        context.setApplicationDeploymentId(testDeploymentId);
        context.save();

        Migration updatedMigration = sut.getCurrentMigration();

        assertEquals(testDeploymentId, updatedMigration.getContext().getApplicationDeploymentId());
    }

    @Test
    public void shouldRaiseErrorOnGetCurrentMigrationWhenMoreThanOneExists() {
        initializeAndCreateSingleMigrationWithStage(MigrationStage.FS_MIGRATION_COPY_WAIT);
        Migration migration = ao.create(Migration.class);
        migration.setStage(ERROR);
        migration.save();
        assertNumberOfMigrations(2);

        assertThrows(Exception.class, () -> sut.getCurrentMigration(), "Invalid State - should only be 1 migration");
    }

    @Test
    public void shouldGetCurrentMigrationWhenOneExists() {
        Migration existingMigration = initializeAndCreateSingleMigrationWithStage(MigrationStage.FS_MIGRATION_COPY_WAIT);

        Migration currentMigration = sut.getCurrentMigration();
        assertEquals(currentMigration.getID(), existingMigration.getID());
        assertEquals(currentMigration.getStage(), existingMigration.getStage());
    }

    @Test
    public void shouldGetCurrentMigrationWhenASubsequentFinishedMigrationExists() {
        Migration firstMigration = ao.create(Migration.class);
        firstMigration.setStage(FS_MIGRATION_COPY);
        firstMigration.save();

        Migration subsequentFinishedMigration = ao.create(Migration.class);
        subsequentFinishedMigration.setStage(FINISHED);
        subsequentFinishedMigration.save();

        Migration currentMigration = sut.getCurrentMigration();
        assertEquals(currentMigration.getID(), firstMigration.getID());
        assertEquals(currentMigration.getStage(), firstMigration.getStage());
    }

    @Test
    public void shouldGetCurrentMigrationWhenAnEarlierFinishedMigrationExists() {
        Migration earlierFinishedMigration = ao.create(Migration.class);
        earlierFinishedMigration.setStage(FINISHED);
        earlierFinishedMigration.save();

        Migration subsequentMigration = ao.create(Migration.class);
        subsequentMigration.setStage(PROVISION_APPLICATION);
        subsequentMigration.save();

        Migration currentMigration = sut.getCurrentMigration();
        assertEquals(currentMigration.getID(), subsequentMigration.getID());
        assertEquals(currentMigration.getStage(), subsequentMigration.getStage());
    }

    @Test
    public void shouldCreateMigrationWhenNoneExists() {
        Migration migration = sut.getCurrentMigration();
        assertNumberOfMigrations(1);
        assertEquals(NOT_STARTED, migration.getStage());
    }

    @Test
    public void shouldGetLatestMigrationContext() throws MigrationAlreadyExistsException {
        Migration migration = sut.createMigration();
        MigrationContext context = migration.getContext();
        final String testDeploymentId = "test-id";
        context.setApplicationDeploymentId(testDeploymentId);
        context.save();

        MigrationContext newContext = sut.getCurrentContext();
        assertEquals(testDeploymentId, newContext.getApplicationDeploymentId());

        final String newDeploymentId = "next-id";
        newContext.setApplicationDeploymentId(newDeploymentId);
        newContext.save();

        MigrationContext nextContext = sut.getCurrentContext();
        assertEquals(newDeploymentId, nextContext.getApplicationDeploymentId());
        assertEquals(newDeploymentId, sut.getCurrentMigration().getContext().getApplicationDeploymentId());
    }

    @Test
    public void shouldPublishMigrationResetEventOnDeleteOfEachMigration() throws Exception {
        Migration oneMigration = ao.create(Migration.class);
        MigrationContext oneMigrationContext = ao.create(MigrationContext.class);
        oneMigrationContext.setMigration(oneMigration);
        oneMigrationContext.save();


        Migration anotherMigration = ao.create(Migration.class);
        MigrationContext migrationContext = ao.create(MigrationContext.class);
        migrationContext.setMigration(anotherMigration);
        migrationContext.save();

        assertNumberOfMigrations(2);

        sut.resetMigration();

        assertNumberOfMigrations(0);

        verify(eventPublisher, times(2)).publish(argThat(argument -> argument instanceof MigrationResetEvent && Arrays.asList(oneMigration.getID(), anotherMigration.getID()).contains(((MigrationResetEvent) argument).getMigrationId()))
        );
    }

    @Test
    public void shouldDeleteAllMigrationsAndAssociatedContexts() throws Exception {
        Migration migration = sut.createMigration();
        IntStream.range(0,3).forEach(value -> {
            FileSyncRecord fileSyncRecord = ao.create(FileSyncRecord.class);
            fileSyncRecord.setMigration(migration);
            fileSyncRecord.save();
        });
        assertNumberOfMigrations(1);
        assertNumberOfMigrationContexts(1);
        assertNumberOfFileSyncRecords(3);

        sut.resetMigration();

        assertNumberOfMigrations(0);
        assertNumberOfMigrationContexts(0);
        assertNumberOfFileSyncRecords(0);
    }

    @Test
    public void shouldCleanInfraWhenResettingMigration() {
        initializeAndCreateSingleMigrationWithStage(FS_MIGRATION_COPY);
        sut.resetMigration();

        verify(cleanupService).startMigrationInfrastructureCleanup();
    }

    @Test
    public void shouldFinishCurrentMigrationWhenCurrentStageIsValidate() throws InvalidMigrationStageError {
        Migration migration = initializeAndCreateSingleMigrationWithStage(MigrationStage.VALIDATE);

        MigrationContext migrationContext = migration.getContext();
        migrationContext.setMigration(migration);
        migrationContext.setStartEpoch((System.currentTimeMillis() - 1000000L) / 1000L);
        migrationContext.save();

        sut.finishCurrentMigration();

        Migration finishedMigration = ao.find(Migration.class, Query.select().where("ID = ?", migration.getID()))[0];
        assertEquals(FINISHED, finishedMigration.getStage());
        assertTrue(finishedMigration.getContext().getEndEpoch() > finishedMigration.getContext().getStartEpoch());
        verify(eventPublisher).publish(any(MigrationCompleteEvent.class));
    }

    @Test
    public void shouldRaiseInvalidMigrationExceptionWhenFinishingAMigrationNotInValidateStage() throws InvalidMigrationStageError {
        Migration migration = initializeAndCreateSingleMigrationWithStage(ERROR);

        MigrationContext migrationContext = migration.getContext();
        migrationContext.setMigration(migration);
        migrationContext.setStartEpoch((System.currentTimeMillis() - 1000000L) / 1000L);
        migrationContext.save();

        assertThrows(InvalidMigrationStageError.class, () -> sut.finishCurrentMigration());
    }

    @Test
    public void shouldTransitionToStageSpecificErrorWhenStageIsAnErrorStage() {
        initializeAndCreateSingleMigrationWithStage(PROVISION_APPLICATION);

        String errorMessage = "provisioning error";
        sut.error(errorMessage);

        Migration actualMigration = sut.getCurrentMigration();
        assertEquals(PROVISIONING_ERROR, actualMigration.getStage());
        assertEquals(errorMessage, actualMigration.getContext().getErrorMessage());
        verify(eventPublisher).publish(any(MigrationFailedEvent.class));
    }

    @Test
    public void shouldTransitionToGenericErrorWhenCurrentStageDoesNotHaveASpecificErrorStage() {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);

        String errorMessage = "provisioning error";

        sut.error(errorMessage);

        Migration actualMigration = sut.getCurrentMigration();
        assertEquals(ERROR, actualMigration.getStage());
        assertEquals(errorMessage, actualMigration.getContext().getErrorMessage());
        verify(eventPublisher).publish(any(MigrationFailedEvent.class));
    }

    @Test
    public void shouldTransitionToFinalSyncErrorStageWhenCurrentStageIsOneOfFinalSyncStages() {
        initializeAndCreateSingleMigrationWithStage(DATA_MIGRATION_IMPORT_WAIT);

        final String errorMessage = "failure";
        sut.error(errorMessage);

        assertEquals(FINAL_SYNC_ERROR, sut.getCurrentStage());

        MigrationContext context = sut.getCurrentContext();
        assertEquals(errorMessage, context.getErrorMessage());
    }

    @Test
    public void shouldTransitionToFSErrorStageWhenCurrentStageIsOneOfFSMigrationStages() {
        initializeAndCreateSingleMigrationWithStage(FS_MIGRATION_COPY_WAIT);

        final String errorMessage = "failure";
        sut.error(errorMessage);

        assertEquals(FS_MIGRATION_ERROR, sut.getCurrentStage());

        MigrationContext context = sut.getCurrentContext();
        assertEquals(errorMessage, context.getErrorMessage());
    }


    private void assertNumberOfMigrations(int i) {
        assertEquals(i, ao.find(Migration.class).length);
    }

    private void assertNumberOfMigrationContexts(int i) {
        assertEquals(i, ao.find(MigrationContext.class).length);
    }

    private void assertNumberOfFileSyncRecords(int i) {
        assertEquals(i, ao.find(FileSyncRecord.class).length);
    }

    private Migration initializeAndCreateSingleMigrationWithStage(MigrationStage stage) {
        Migration migration;
        try {
            migration = sut.createMigration();
        } catch (MigrationAlreadyExistsException e) {
            throw new RuntimeException("Tried to initialize migration when one exists already", e);
        }
        migration.setStage(stage);
        migration.save();

        return migration;
    }

    private void setupEntities() {
        ao.migrate(Migration.class);
        ao.migrate(MigrationContext.class);
        ao.migrate(FileSyncRecord.class);
    }
}
