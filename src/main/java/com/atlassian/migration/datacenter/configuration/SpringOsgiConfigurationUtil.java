package com.atlassian.migration.datacenter.configuration;

import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;

import static com.atlassian.plugins.osgi.javaconfig.OsgiServices.importOsgiService;

/**
 * Common methods used in Spring OSGI Configuration of the plugin
 */
public class SpringOsgiConfigurationUtil {
    /**
     * Works around the Spring Configuration problem when we try to import the class which is not available yet.
     */
    public static <T> Supplier<T> lazyImportOsgiService(Class<T> clazz){
        return new LazyReference<T>() {
            @Override
            protected T create() throws Exception {
                return importOsgiService(clazz);
            }
        };
    }
}
