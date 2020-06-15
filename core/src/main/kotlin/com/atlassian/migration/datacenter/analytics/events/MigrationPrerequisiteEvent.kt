package com.atlassian.migration.datacenter.analytics.events

import com.atlassian.analytics.api.annotations.EventName
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration
import com.atlassian.migration.datacenter.core.util.AnalyticsHelper

@EventName("atl.dc.migration.prerequisites")
data class MigrationPrerequisiteEvent(
        // NOTE: If additional properties are added here they should also be added to the file analytics_whitelist.json.
        val pluginVersion: String,
        val dbCompatible: Boolean,
        val dbType: DatabaseConfiguration.DBType,
        val osCompatible: Boolean,
        val osType: AnalyticsHelper.OsType,
        val fsSizeCompatible: Boolean,
        val fsSize: Long
)