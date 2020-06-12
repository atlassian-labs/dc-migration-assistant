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
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.analytics.events.MigrationCompleteEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationCreatedEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationFailedEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationTransitionEvent;
import com.atlassian.migration.datacenter.analytics.events.MigrationTransitionFailedEvent;
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration;
import com.atlassian.migration.datacenter.core.proxy.ReadOnlyEntityInvocationHandler;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationReadyStatus;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.exceptions.MigrationAlreadyExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

import static com.atlassian.migration.datacenter.spi.MigrationStage.ERROR;
import static com.atlassian.migration.datacenter.spi.MigrationStage.NOT_STARTED;
import static java.util.Objects.requireNonNull;

/**
 * Manages a migration from on-premise to self-hosted AWS.
 */
public class AWSMigrationService implements MigrationService {
    private static final Logger log = LoggerFactory.getLogger(AWSMigrationService.class);
    private ActiveObjects ao;
    private ApplicationConfiguration applicationConfiguration;
    private JiraHome jiraHome;
    private EventPublisher eventPublisher;

    /**
     * Creates a new, unstarted AWS Migration
     */
    public AWSMigrationService(ActiveObjects ao, ApplicationConfiguration applicationConfiguration, JiraHome jiraHome, EventPublisher eventPublisher) {
        this.ao = requireNonNull(ao);
        this.applicationConfiguration = applicationConfiguration;
        this.jiraHome = jiraHome;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Migration createMigration() throws MigrationAlreadyExistsException
    {
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
    public void assertCurrentStage(MigrationStage expected) throws InvalidMigrationStageError
    {
        MigrationStage currentStage = getCurrentStage();
        if (currentStage != expected) {
            throw new InvalidMigrationStageError(String.format("wanted to be in stage %s but was in stage %s", expected, currentStage));
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

    @Override
    public void deleteMigrations() {
        final Migration[] migrations = ao.find(Migration.class);
        for (Migration migration : migrations) {
            ao.delete(migration.getContext());
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
                                                                      currentStage.toString(), to.toString()));
            throw InvalidMigrationStageError.errorWithMessage(currentStage, to);
        }
        setCurrentStage(migration, to);
        eventPublisher.publish(new MigrationTransitionEvent(applicationConfiguration.getPluginVersion(),
                                                            currentStage.toString(), to.toString()));
    }

    @Override
    public MigrationReadyStatus getReadyStatus()
    {
        long MAX_FS_SIZE = 1024L * 1024L * 1024L * 400L;
        long size = FileUtils.sizeOfDirectory(jiraHome.getHome());

        Boolean fs = size < MAX_FS_SIZE;
        Boolean db = applicationConfiguration.getDatabaseConfiguration().getType() == DatabaseConfiguration.DBType.POSTGRESQL;
        Boolean os = SystemUtils.IS_OS_LINUX;
        return new MigrationReadyStatus(db, os, fs);
    }

    @Override
    public void error(String message) {
        Migration migration = findFirstOrCreateMigration();
        MigrationContext context = getCurrentContext();

        MigrationStage failStage = context.getMigration().getStage();
        Long now = System.currentTimeMillis() / 1000L;

        setCurrentStage(migration, ERROR);
        context.setErrorMessage(message);
        context.setEndEpoch(now);
        context.save();

        eventPublisher.publish(new MigrationFailedEvent(applicationConfiguration.getPluginVersion(),
                                                        failStage.toString(), message,
                                                        now - context.getStartEpoch()));
    }

    @Override
    public void error(Throwable e)
    {
        error(e.getMessage());
        findFirstOrCreateMigration().getStage().setException(e);
    }

    @Override
    public void finish() throws InvalidMigrationStageError
    {
        // TODO: This may require additional operations

        Long now = System.currentTimeMillis() / 1000L;
        transition(MigrationStage.FINISHED);

        MigrationContext context = getCurrentContext();
        context.setEndEpoch(now);
        context.save();

        eventPublisher.publish(new MigrationCompleteEvent(applicationConfiguration.getPluginVersion(),
                                                          now - context.getStartEpoch()));
    }

    protected synchronized void setCurrentStage(Migration migration, MigrationStage stage) {
        migration.setStage(stage);
        migration.save();
    }

    protected synchronized Migration findFirstOrCreateMigration() {
        Migration[] migrations = ao.find(Migration.class);
        if (migrations.length == 1) {
            // In case we have interrupted migration (e.g. the node went down), we want to pick up where we've
            // left off.
            return migrations[0];
        } else if (migrations.length == 0) {
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
}

