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

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MigrationAssistantSpringConfigurationTest {

    @Test
    void shouldEnsureAllBeansAreDefined() {
        Method[] declaredMethods = MigrationAssistantBeanConfiguration.class.getDeclaredMethods();

        for (Method method : declaredMethods) {
            if (Modifier.isPublic(method.getModifiers())) {
                Bean[] declaredAnnotationsByType = method.getDeclaredAnnotationsByType(Bean.class);
                assertEquals(1, declaredAnnotationsByType.length, "Method " + method.getName() + " does not have a Bean annotation");
            }
        }
    }

    @Test
    void shouldEnsureAllOsgiBeansAreDefined() {
        Method[] declaredMethods = MigrationAssistantOsgiImportConfiguration.class.getDeclaredMethods();

        for (Method method : declaredMethods) {
            if (Modifier.isPublic(method.getModifiers())) {
                Bean[] declaredAnnotationsByType = method.getDeclaredAnnotationsByType(Bean.class);
                assertEquals(1, declaredAnnotationsByType.length, "Method " + method.getName() + " does not have a Bean annotation");
            }
        }
    }
}