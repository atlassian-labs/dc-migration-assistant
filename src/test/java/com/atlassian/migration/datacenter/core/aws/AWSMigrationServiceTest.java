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
import static com.atlassian.migration.datacenter.spi.MigrationStage.FS_MIGRATION_COPY;
import static com.atlassian.migration.datacenter.spi.MigrationStage.NOT_STARTED;
import static com.atlassian.migration.datacenter.spi.MigrationStage.PROVISION_APPLICATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

;

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

        sut = new AWSMigrationService(ao, filesystemMigrationService, cfnApi, schedulerService);
    }

    @Test
    public void shouldNotStartFileSystemMigrationWhenNoMigrationExists() throws Exception {
        ao.migrate(Migration.class);

        boolean success = sut.startFilesystemMigration();

        assertFalse(success);
        verify(this.filesystemMigrationService, never()).startMigration();
    }

    @Test
    public void shouldStartFsMigrationWhenMigrationStageIsReadyForFsSync() {
        initializeAndCreateSingleMigrationWithStage(FS_MIGRATION_COPY);
        boolean success = sut.startFilesystemMigration();
        assertTrue(success);
    }

    // MigrationServiceV2 Tests

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

        sut.transition(AUTHENTICATION, PROVISION_APPLICATION);

        assertEquals(PROVISION_APPLICATION, sut.getCurrentStage());
    }

    @Test
    public void shouldNotTransitionWhenSourceStageIsNotCurrentStage() {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);
        assertEquals(AUTHENTICATION, sut.getCurrentStage());

        assertThrows(InvalidMigrationStageError.class, () -> sut.transition(FS_MIGRATION_COPY, PROVISION_APPLICATION));
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
    public void errorShouldSetCurrentStageToError() {
        initializeAndCreateSingleMigrationWithStage(PROVISION_APPLICATION);

        sut.error();

        assertEquals(ERROR, sut.getCurrentStage());
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
