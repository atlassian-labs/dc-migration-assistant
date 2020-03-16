package com.atlassian.migration.datacenter.core.aws.region;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

/**
 * Manages the persistence and retrieval of the region used to make AWS SDK API calls.
 * The region is stored in the plugin settings of this app.
 */
public class PluginSettingsRegionManager implements RegionService {

    private static final String AWS_REGION_PLUGIN_STORAGE_KEY = "com.atlassian.migration.datacenter.core.aws.region";
    private static final String REGION_PLUGIN_STORAGE_SUFFIX = ".region";

    private final PluginSettingsFactory pluginSettingsFactory;
    private final GlobalInfrastructure globalInfrastructure;

    public PluginSettingsRegionManager(PluginSettingsFactory pluginSettingsFactory, GlobalInfrastructure globalInfrastructure) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.globalInfrastructure = globalInfrastructure;
    }

    /**
     * @return The id of the region that has been stored most recently (e.g. us-east-2, ap-southeast-1). If no region
     * has been configured, it will return the id of the default region.
     */
    @Override
    public String getRegion() {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
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

        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(AWS_REGION_PLUGIN_STORAGE_KEY + REGION_PLUGIN_STORAGE_SUFFIX, region);
    }

    private boolean isValidRegion(String testRegion) {
        return globalInfrastructure.
                getRegions()
                .stream()
                .anyMatch(region -> region.equals(testRegion));
    }
}
