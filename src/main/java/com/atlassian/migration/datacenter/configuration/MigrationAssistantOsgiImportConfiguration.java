package com.atlassian.migration.datacenter.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.aws.auth.EncryptedCredentialsStorage;
import com.atlassian.migration.datacenter.core.aws.auth.ReadCredentialsService;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
