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

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * This {@link Configuration} class holds all the bean definitions and osgi imports that are used by the plugin.
 * This class is used by the <a href="https://developer.atlassian.com/server/framework/atlassian-sdk/spring-java-config/">atlassians-spring-java-config</a> library to configure the app with all the required dependencies.
 */
@Configuration
@Import({
        MigrationAssistantOsgiImportConfiguration.class,
        MigrationAssistantBeanConfiguration.class
})
public class MigrationAssistantSpringConfiguration {
}
