package com.atlassian.migration.datacenter.analytics.events

import com.atlassian.analytics.api.annotations.EventName
import com.atlassian.migration.datacenter.spi.MigrationStage

@EventName("atl.dc.migration.transition")
data class MigrationTransitionEvent (
        // NOTE: If additional properties are added here they should also be added to the file analytics_whitelist.json.
        val pluginVersion: String,
        val fromStage: MigrationStage,
        val toStage: MigrationStage
)