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
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.datacenter.analytics.OsType;
import com.atlassian.migration.datacenter.analytics.events.MigrationCompleteEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationCreatedEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationFailedEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationPrerequisiteEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationTransitionEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationTransitionFailedEvent;
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration;
import com.atlassian.migration.datacenter.core.db.DatabaseClientTools;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
import com.atlassian.migration.datacenter.core.db.PostgresClientTooling;
import com.atlassian.migration.datacenter.core.proxy.ReadOnlyEntityInvocationHandler;
import com.atlassian.migration.datacenter.dto.FileSyncRecord;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.events.MigrationResetEvent;
import com.atlassian.migration.datacenter.spi.MigrationReadyStatus;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.exceptions.MigrationAlreadyExistsException;
import net.swiftzer.semver.SemVer;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.atlassian.migration.datacenter.spi.MigrationStage.ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.NOT_STARTED;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Manages a migration from on-premise to self-hosted AWS.
 */
public class AWSMigrationService implements MigrationService {
    private static final Logger log = LoggerFactory.getLogger(AWSMigrationService.class);
    private ActiveObjects ao;
    private ApplicationConfiguration applicationConfiguration;
    private DatabaseExtractorFactory databaseExtractorFactory;
    private Path localHome;
    private EventPublisher eventPublisher;

    /**
     * Creates a new, unstarted AWS Migration
     */
    public AWSMigrationService(ActiveObjects ao,
                               ApplicationConfiguration applicationConfiguration,
                               DatabaseExtractorFactory databaseExtractorFactory,
                               Path jiraHome,
                               EventPublisher eventPublisher) {
        this.ao = requireNonNull(ao);
        this.applicationConfiguration = applicationConfiguration;
        this.databaseExtractorFactory = databaseExtractorFactory;
        this.localHome = jiraHome;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Migration createMigration() throws MigrationAlreadyExistsException
    {
        Migration migration = findFirstOrCreateMigration();
        if (migration.getStage().equals(NOT_STARTED)) {
            return migration;
        }
        throw new MigrationAlreadyExistsException(format("Found existing migration in Stage - `%s`", migration.getStage()));
    }

    @Override
    public MigrationStage getCurrentStage() {
        return findFirstOrCreateMigration().getStage();
    }

    @Override
    public void assertCurrentStage(MigrationStage expected) throws InvalidMigrationStageError
    {
        MigrationStage currentStage = getCurrentStage();
        if (currentStage != expected) {
            throw new InvalidMigrationStageError(format("wanted to be in stage %s but was in stage %s", expected, currentStage));
        }
    }

    @Override
    public Migration getCurrentMigration() {
        Migration migration = findFirstOrCreateMigration();
        return (Migration) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Migration.class}, new ReadOnlyEntityInvocationHandler<>(migration));
    }

    @Override
    public MigrationContext getCurrentContext() {
        return getCurrentMigration().getContext();
    }

    //Note: Delete migrations deletes all migrations, even `Finished` ones
    // When we add support for multiple migrations, we need to revisit this and ensure that, on migration cancel, we should remove the active migration, not all migrations.
    @Override
    public void deleteMigrations() {
        for (Migration migration : findAllMigrations()) {
            int migrationId = migration.getID();
            eventPublisher.publish(new MigrationResetEvent(migrationId));
            ao.delete(migration.getContext());
            ao.deleteWithSQL(FileSyncRecord.class, format("%s = ?", "MIGRATION_ID"), migrationId);
            ao.delete(migration);
            log.warn("deleted migration {}", migration);
        }
    }

    @Override
    public synchronized void transition(MigrationStage to) throws InvalidMigrationStageError {
        Migration migration = findFirstOrCreateMigration();
        MigrationStage currentStage = migration.getStage();

        if (!currentStage.isValidTransition(to)) {
            eventPublisher.publish(new MigrationTransitionFailedEvent(applicationConfiguration.getPluginVersion(),
                                                                      currentStage, to));
            throw InvalidMigrationStageError.errorWithMessage(currentStage, to);
        }
        setCurrentStage(migration, to);
        eventPublisher.publish(new MigrationTransitionEvent(applicationConfiguration.getPluginVersion(),
                                                            currentStage, to));
    }

    @Override
    public MigrationReadyStatus getReadyStatus()
    {
        /*
         * TODO: For now we only support Postgres. Once support for database types is 
         * expanded introduce a factory here that will return the correct implementation
         */
        DatabaseClientTools dbClientTooling = new PostgresClientTooling(applicationConfiguration);

        Boolean db = applicationConfiguration.getDatabaseConfiguration().getType() == DatabaseConfiguration.DBType.POSTGRESQL;
        Boolean os = SystemUtils.IS_OS_LINUX;
        SemVer pgDumpVer = dbClientTooling.getDatabaseDumpClientVersion();
        SemVer pgServerVer = dbClientTooling.getDatabaseServerVersion();
        Boolean pgDumpAvail = pgDumpVer != null;

       // From the pg_dump manpage: "pg_dump cannot dump from PostgreSQL servers newer than its own
       // major version; it will refuse to even try, rather than risk making an invalid dump. Also,
       // it is not guaranteed that pg_dump's output can be loaded into a server of an older major
       // version — not even if the dump was taken from a server of that version."
        Boolean pgVerCompat = pgDumpAvail && pgServerVer != null && pgDumpVer.getMajor() >= pgServerVer.getMajor();

        MigrationReadyStatus status = new MigrationReadyStatus(db, os, pgDumpAvail, pgVerCompat);

        eventPublisher.publish(new MigrationPrerequisiteEvent(applicationConfiguration.getPluginVersion(),
                                                              db, applicationConfiguration.getDatabaseConfiguration().getType(),
                                                              os, OsType.fromSystem(),
                                                              pgVerCompat));

        return status;
    }

    @Override
    public void error(String message) {
        Migration migration = findFirstOrCreateMigration();
        MigrationContext context = getCurrentContext();

        MigrationStage failStage = context.getMigration().getStage();
        Long now = System.currentTimeMillis() / 1000L;

        setCurrentStage(migration, ERROR);
        // We must truncate the error message to 450 characters so that it fits in the varchar(450) column
        context.setErrorMessage(message.substring(0, Math.min(450, message.length())));
        context.setEndEpoch(now);
        context.save();

        eventPublisher.publish(new MigrationFailedEvent(applicationConfiguration.getPluginVersion(),
                                                        failStage, now - context.getStartEpoch()));
    }

    @Override
    public void error(Throwable e)
    {
        error(e.getMessage());
        findFirstOrCreateMigration().getStage().setException(e);
    }

    @Override
    public void finishCurrentMigration() throws InvalidMigrationStageError {
        long now = System.currentTimeMillis() / 1000L;
        log.info("Finishing current migration. Migration has run for {} seconds", now);

        MigrationContext context = getCurrentContext();

        transition(MigrationStage.FINISHED);

        context.setEndEpoch(now);
        context.save();

        long migrationRunTimeInSeconds = now - context.getStartEpoch();

        eventPublisher.publish(new MigrationCompleteEvent(applicationConfiguration.getPluginVersion(), migrationRunTimeInSeconds));
    }

    protected synchronized void setCurrentStage(Migration migration, MigrationStage stage) {
        migration.setStage(stage);
        migration.save();
    }

    protected synchronized Migration findFirstOrCreateMigration() {
        List<Migration> migrations = findNonFinishedMigrations();
        if (migrations.size() == 1) {
            // In case we have interrupted migration (e.g. the node went down), we want to pick up where we've
            // left off.
            return migrations.get(0);
        }
        if (migrations.isEmpty()) {
            // We didn't start the migration, so we need to create record in the db and a migration context
            Migration migration = ao.create(Migration.class);
            migration.setStage(NOT_STARTED);
            migration.save();

            MigrationContext context = ao.create(MigrationContext.class);
            context.setMigration(migration);
            context.setStartEpoch(System.currentTimeMillis() / 1000L);
            context.save();

            eventPublisher.publish(new MigrationCreatedEvent(applicationConfiguration.getPluginVersion()));

            return migration;
        } else {
            log.error("Expected one Migration, found multiple.");
            throw new RuntimeException("Invalid State - should only be 1 migration");
        }
    }

    //TODO: Verify if ao exposes any negate queries to filter migrations in a query
    private List<Migration> findNonFinishedMigrations() {
        return Arrays.stream(findAllMigrations()).filter(x -> x.getStage() != MigrationStage.FINISHED).collect(Collectors.toList());
    }

    private Migration[] findAllMigrations() {
        return ao.find(Migration.class);
    }
}

