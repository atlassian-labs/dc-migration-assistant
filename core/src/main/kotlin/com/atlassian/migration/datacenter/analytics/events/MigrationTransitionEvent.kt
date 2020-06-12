package com.atlassian.migration.datacenter.analytics.events

import com.atlassian.analytics.api.annotations.EventName

@EventName("atl.dc.migration.transition")
data class MigrationTransitionEvent (
        // NOTE: If additional properties are added here they should also be added to the file analytics_whitelist.json.
        val pluginVersion: String,
        val fromStage: String,
        val toStage: String
)