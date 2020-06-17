package com.atlassian.migration.datacenter.analytics.events

import com.atlassian.analytics.api.annotations.EventName
import com.atlassian.migration.datacenter.spi.MigrationStage

@EventName("atl.dc.migration.failed")
data class MigrationFailedEvent(
        // NOTE: If additional properties are added here they should also be added to the file analytics_whitelist.json.
        val pluginVersion: String,
        val failedStage: MigrationStage,
        val runTimeSeconds: Long
)