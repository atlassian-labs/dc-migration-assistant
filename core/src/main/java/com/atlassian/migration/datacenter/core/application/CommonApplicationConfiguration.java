package com.atlassian.migration.datacenter.core.application;

import com.atlassian.plugin.PluginAccessor;

public abstract class CommonApplicationConfiguration implements ApplicationConfiguration
{
    private final PluginAccessor pluginAccessor;

    public CommonApplicationConfiguration(PluginAccessor pluginAccessor)
    {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public String getPluginVersion()
    {
        return pluginAccessor.getPlugin(getPluginKey())
            .getPluginInformation()
            .getVersion();
    }
}
