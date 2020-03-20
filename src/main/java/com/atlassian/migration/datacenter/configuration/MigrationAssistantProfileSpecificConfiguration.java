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

package com.atlassian.migration.datacenter.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.migration.datacenter.core.aws.AllowAnyTransitionMigrationServiceFacade;
import com.atlassian.migration.datacenter.spi.MigrationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class MigrationAssistantProfileSpecificConfiguration {
    @Bean
    @Profile("allowAnyTransition")
    @Primary
    public MigrationService allowAnyTransitionMigrationService(ActiveObjects ao){
        return new AllowAnyTransitionMigrationServiceFacade(ao);
    }
}