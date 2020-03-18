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

package com.atlassian.migration.datacenter.core.aws.region;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.util.concurrent.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;

import javax.annotation.PostConstruct;

/**
 * Manages the persistence and retrieval of the region used to make AWS SDK API calls.
 * The region is stored in the plugin settings of this app.
 */
public class PluginSettingsRegionManager implements RegionService {

    private static final Logger logger = LoggerFactory.getLogger(PluginSettingsRegionManager.class);

    static final String AWS_REGION_PLUGIN_STORAGE_KEY = "com.atlassian.migration.datacenter.core.aws.region";
    static final String REGION_PLUGIN_STORAGE_SUFFIX = ".region";


    private final GlobalInfrastructure globalInfrastructure;
    private Supplier<PluginSettingsFactory> pluginSettingsFactorySupplier;
    private PluginSettings pluginSettings;

    public PluginSettingsRegionManager(Supplier<PluginSettingsFactory> pluginSettingsFactorySupplier, GlobalInfrastructure globalInfrastructure) {
        this.pluginSettingsFactorySupplier = pluginSettingsFactorySupplier;
        this.globalInfrastructure = globalInfrastructure;
    }

    @PostConstruct
    // FIXME: I do not work
    public void postConstruct(){
        logger.debug("setting up plugin settings");
        this.pluginSettings = this.pluginSettingsFactorySupplier.get().createGlobalSettings();
    }

    /**
     * @return The id of the region that has been stored most recently (e.g. us-east-2, ap-southeast-1). If no region
     * has been configured, it will return the id of the default region.
     */
    @Override
    public String getRegion() {
        // FIXME: Need to find a way to inject without calling the supplier every time
        PluginSettings pluginSettings = this.pluginSettingsFactorySupplier.get().createGlobalSettings();
        String pluginSettingsRegion = (String) pluginSettings.get(AWS_REGION_PLUGIN_STORAGE_KEY + REGION_PLUGIN_STORAGE_SUFFIX);
        if (pluginSettingsRegion == null || "".equals(pluginSettingsRegion)) {
            return Region.US_EAST_1.toString();
        }
        return pluginSettingsRegion;
    }

    /**
     * Sets the region to be used for AWS API calls
     *
     * @param region the id of the region to use e.g. us-east-1, eu-central-1
     * @throws InvalidAWSRegionException if the region id provided is not a supported AWS region.
     * @see GlobalInfrastructure
     */
    @Override
    public void storeRegion(String region) throws InvalidAWSRegionException {
        if (!isValidRegion(region)) {
            throw new InvalidAWSRegionException();
        }

        // FIXME: Need to find a way to inject without calling the supplier every time
        PluginSettings pluginSettings = this.pluginSettingsFactorySupplier.get().createGlobalSettings();
        pluginSettings.put(AWS_REGION_PLUGIN_STORAGE_KEY + REGION_PLUGIN_STORAGE_SUFFIX, region);
    }

    private boolean isValidRegion(String testRegion) {
        return globalInfrastructure.
                getRegions()
                .stream()
                .anyMatch(region -> region.equals(testRegion));
    }
}
