package com.atlassian.migration.datacenter.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * This {@link Configuration} class holds all the bean definitions and osgi imports that are used by the plugin.
 * This class is used by the <a href="https://developer.atlassian.com/server/framework/atlassian-sdk/spring-java-config/">atlassians-spring-java-config</a> library to configure the app with all the required dependencies.
 */
@Configuration
@Import({
        MigrationAssistantBeanConfiguration.class
})
public class MigrationAssistantSpringConfiguration {
}
