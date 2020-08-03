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

package com.atlassian.migration.datacenter.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.aws.AllowAnyTransitionMigrationServiceFacade;
import com.atlassian.migration.datacenter.core.aws.ReuseInfrastructureAWSMigrationService;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureCleanupStatus;
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class MigrationAssistantProfileSpecificConfiguration {
    @Bean
    @Profile("allowAnyTransition")
    @Primary
    public MigrationService allowAnyTransitionMigrationService(ActiveObjects activeObjects, ApplicationConfiguration applicationConfiguration, EventPublisher eventPublisher) {
        return new AllowAnyTransitionMigrationServiceFacade(activeObjects, applicationConfiguration, eventPublisher);
    }

    @Bean
    @Profile("retainInfra")
    @Primary
    public MigrationService reuseInfrastructureAWSMigrationService(ActiveObjects ao, ApplicationConfiguration applicationConfiguration, EventPublisher eventPublisher) {
        return new ReuseInfrastructureAWSMigrationService(ao, applicationConfiguration, eventPublisher);
    }

    @Bean
    @Profile("retainInfra")
    @Primary
    public MigrationInfrastructureCleanupService noCleanupService() {
        return new MigrationInfrastructureCleanupService() {
            @Override
            public boolean startMigrationInfrastructureCleanup() {
                return true;
            }

            @NotNull
            @Override
            public InfrastructureCleanupStatus getMigrationInfrastructureCleanupStatus() {
                return InfrastructureCleanupStatus.CLEANUP_COMPLETE;
            }
        };
    }
}
