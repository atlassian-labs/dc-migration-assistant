package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.migration.datacenter.core.exceptions.InfrastructureProvisioningError;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.exceptions.MigrationAlreadyExistsException;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
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

import java.util.HashMap;
import java.util.Optional;

import static com.atlassian.migration.datacenter.spi.MigrationStage.AUTHENTICATION;
import static com.atlassian.migration.datacenter.spi.MigrationStage.ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.FS_MIGRATION_COPY;;
import static com.atlassian.migration.datacenter.spi.MigrationStage.NOT_STARTED;
import static com.atlassian.migration.datacenter.spi.MigrationStage.PROVISION_APPLICATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void shouldBeInUnStartedStageWhenNoMigrationExists() {
        setupEntities();

        MigrationStage initialStage = sut.getMigrationStage();

        assertEquals(NOT_STARTED, initialStage);
    }

    @Test
    public void shouldGetCorrectMigrationStage() {
        initializeAndCreateSingleMigrationWithStage(NOT_STARTED);

        assertNumberOfMigrations(1);

        ao.flushAll();

        MigrationStage currentStage = sut.getMigrationStage();

        assertEquals(NOT_STARTED, currentStage);
    }


    @Test
    public void shouldTransitionStageToAuthenticationWhenCreated() {
        setupEntities();

        assertNumberOfMigrations(0);

        assertTrue(sut.startMigration());
        MigrationStage currentStage = sut.getMigrationStage();

        assertEquals(AUTHENTICATION, currentStage);
        assertNumberOfMigrations(1);
    }

    @Test
    public void shouldNotStartMigrationWhenExistingStateIsNotUnStarted() {
        initializeAndCreateSingleMigrationWithStage(AUTHENTICATION);

        ao.flushAll();
        assertNumberOfMigrations(1);

        assertFalse(sut.startMigration());
    }

    @Test
    public void shouldNotStartFileSystemMigrationWhenNoMigrationExists() throws Exception {
        // given
        ao.migrate(Migration.class);
        // when
        boolean success = sut.startFilesystemMigration();
        // then
        assertFalse(success);
        verify(this.filesystemMigrationService, never()).startMigration();
    }

    @Test
    public void shouldStartFsMigrationWhenMigrationStageIsReadyForFsSync() {
        // given
        initializeAndCreateSingleMigrationWithStage(FS_MIGRATION_COPY);
        // when
        boolean success = sut.startFilesystemMigration();
        // then
        assertTrue(success);
    }

    @Test
    public void shouldProvisionInfrastructureUsingCloudFormationTemplate() throws Exception {
        String templateUrl = "https://template.url", stackName = "test_provision";
        HashMap<String, String> params = new HashMap<>();
        String expectedStackId = "arn:stack:test_provision";

        initializeAndCreateSingleMigrationWithStage(PROVISION_APPLICATION);

        when(this.cfnApi.provisionStack(templateUrl, stackName, params)).thenReturn(Optional.of(expectedStackId));

        String actualStackId = sut.provisionInfrastructure(new ProvisioningConfig(templateUrl, stackName, params));

        assertEquals(expectedStackId, actualStackId);
    }

    @Test
    public void shouldRaiseAProvisioningErrorWhenStackProvisioningFails() {
        String templateUrl = "https://template.url", stackName = "test_provision";
        HashMap<String, String> params = new HashMap<>();

        initializeAndCreateSingleMigrationWithStage(PROVISION_APPLICATION);
        when(this.cfnApi.provisionStack(templateUrl, stackName, params)).thenReturn(Optional.empty());

        assertThrows(InfrastructureProvisioningError.class, () -> {
            sut.provisionInfrastructure(new ProvisioningConfig(templateUrl, stackName, params));
        });
        assertNumberOfMigrations(1);
        assertEquals(ERROR, ao.find(Migration.class)[0].getStage());
    }


    @Test
    public void shouldNotProvisionWhenInitialMigrationStateIsNotReadyToProvision() {
        initializeAndCreateSingleMigrationWithStage(NOT_STARTED);

        assertThrows(InvalidMigrationStageError.class, () -> {
            sut.provisionInfrastructure(new ProvisioningConfig("", "", new HashMap<>()));
        });

        verify(this.cfnApi, never()).provisionStack(any(), any(), any());
    }


    // MigrationServiceV2 Tests

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
