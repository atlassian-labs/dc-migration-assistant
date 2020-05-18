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
