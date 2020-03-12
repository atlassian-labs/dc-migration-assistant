package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.exceptions.MigrationAlreadyExistsException;
import com.atlassian.migration.datacenter.core.proxy.ReadOnlyEntityInvocationHandler;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

import static com.atlassian.migration.datacenter.spi.MigrationStage.ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.NOT_STARTED;
import static java.util.Objects.requireNonNull;

/**
 * Manages a migration from on-premise to self-hosted AWS.
 */
@Component
public class AWSMigrationService implements MigrationService {
    private static final Logger log = LoggerFactory.getLogger(AWSMigrationService.class);
    private ActiveObjects ao;

    /**
     * Creates a new, unstarted AWS Migration
     */
    public AWSMigrationService(ActiveObjects ao) {
        this.ao = requireNonNull(ao);
    }

    @Override
    public Migration createMigration() throws MigrationAlreadyExistsException {
        Migration migration = findFirstOrCreateMigration();
        if (migration.getStage().equals(NOT_STARTED)) {
            return migration;
        }
        throw new MigrationAlreadyExistsException(String.format("Found existing migration in Stage - `%s`", migration.getStage()));
    }

    @Override
    public MigrationStage getCurrentStage() {
        return findFirstOrCreateMigration().getStage();
    }

    @Override
    public Migration getCurrentMigration() {
        Migration migration = findFirstOrCreateMigration();
        return (Migration) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Migration.class}, new ReadOnlyEntityInvocationHandler<>(migration));
    }

    @Override
    public void transition(MigrationStage from, MigrationStage to) throws InvalidMigrationStageError {
        Migration migration = findFirstOrCreateMigration();
        final MigrationStage currentStage = migration.getStage();
        if (!currentStage.equals(from)) {
            throw InvalidMigrationStageError.errorWithMessage(from,currentStage);
        }
        setCurrentStage(migration, to);
    }

    @Override
    public void error() {
        Migration migration = findFirstOrCreateMigration();
        setCurrentStage(migration, ERROR);
    }

    private void setCurrentStage(Migration migration, MigrationStage stage) {
        migration.setStage(stage);
        migration.save();
    }

    private Migration findFirstOrCreateMigration() {
        Migration[] migrations = ao.find(Migration.class);
        if (migrations.length == 1) {
            // In case we have interrupted migration (e.g. the node went down), we want to pick up where we've
            // left off.
            return migrations[0];
        } else if (migrations.length == 0){
            // We didn't start the migration, so we need to create record in the db and a migration context
            Migration migration = ao.create(Migration.class);
            migration.setStage(NOT_STARTED);
            migration.save();

            MigrationContext context = ao.create(MigrationContext.class);
            context.setMigration(migration);
            context.save();

            return migration;
        } else {
            log.error("Expected one Migration, found multiple.");
            throw new RuntimeException("Invalid State - should only be 1 migration");
        }
    }
}

