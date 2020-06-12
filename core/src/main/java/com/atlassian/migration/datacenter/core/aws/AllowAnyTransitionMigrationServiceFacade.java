/*
 * Copyright (c) 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and  limitations under the License.
 */

package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;

public class AllowAnyTransitionMigrationServiceFacade extends AWSMigrationService implements MigrationService {
    public AllowAnyTransitionMigrationServiceFacade(ActiveObjects activeObjects, ApplicationConfiguration applicationConfiguration, JiraHome jiraHome, EventPublisher eventPublisher) {
        super(activeObjects, applicationConfiguration, jiraHome, eventPublisher);
    }

    @Override
    public void transition(MigrationStage to) throws InvalidMigrationStageError
    {
        Migration currentMigration = findFirstOrCreateMigration();
        setCurrentStage(currentMigration, to);
    }
}
