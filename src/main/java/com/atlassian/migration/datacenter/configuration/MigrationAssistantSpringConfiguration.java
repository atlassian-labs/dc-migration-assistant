package com.atlassian.migration.datacenter.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        MigrationAssistantBeanConfiguration.class,
        MigrationAssistantOsgiImportConfiguration.class
})
public class MigrationAssistantSpringConfiguration {
}
