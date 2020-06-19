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

import com.atlassian.util.concurrent.LazyReference;

import java.util.function.Supplier;

import static com.atlassian.plugins.osgi.javaconfig.OsgiServices.importOsgiService;

/**
 * Common methods used in Spring OSGI Configuration of the plugin
 */
public class SpringOsgiConfigurationUtil {
    /**
     * Works around the Spring Configuration problem when we try to import the class which is not available yet.
     */
    public static <T> Supplier<T> lazyImportOsgiService(Class<T> clazz) {
        return toSupplier(new LazyReference<T>() {
            @Override
            protected T create() {
                return importOsgiService(clazz);
            }
        });
    }

    private static <T> Supplier<T> toSupplier(LazyReference<T> lazyReference){
        return lazyReference::get;
    }
}
