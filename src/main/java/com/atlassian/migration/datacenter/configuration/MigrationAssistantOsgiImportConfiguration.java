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
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.util.concurrent.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.atlassian.migration.datacenter.configuration.SpringOsgiConfigurationUtil.lazyImportOsgiService;
import static com.atlassian.plugins.osgi.javaconfig.OsgiServices.importOsgiService;

@Configuration
public class MigrationAssistantOsgiImportConfiguration {
    @Bean
    public PluginSettingsFactory getPluginSettingsFactory() {
        return importOsgiService(PluginSettingsFactory.class);
    }

    @Bean
    public SoyTemplateRenderer getSoyTemplateRenderer() {
        return importOsgiService(SoyTemplateRenderer.class);
    }

    @Bean
    public PermissionEnforcer getPermissionEnforcer() {
        return importOsgiService(PermissionEnforcer.class);
    }

    @Bean
    public LoginUriProvider getLoginUriProvider() {
        return importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public JiraHome getJiraHome() {
        return importOsgiService(JiraHome.class);
    }

    @Bean
    public ActiveObjects ao() {
        return importOsgiService(ActiveObjects.class);
    }

    @Bean
    public SchedulerService schedulerService() {
        return importOsgiService(SchedulerService.class);
    }

    @Bean
    public Supplier<PluginSettingsFactory> settingsFactorySupplier() {
        return lazyImportOsgiService(PluginSettingsFactory.class);
    }
}
