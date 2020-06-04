package com.atlassian.migration.datacenter.api.db

import com.atlassian.migration.datacenter.spi.MigrationStage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DatabaseMigrationProgressKtTest {

    @Test
    fun testStageMapping() {
        assertEquals(FinalSyncMigrationStatus.NOT_STARTED, stageToStatus(MigrationStage.PROVISION_APPLICATION))
        assertEquals(FinalSyncMigrationStatus.EXPORTING, stageToStatus(MigrationStage.DB_MIGRATION_EXPORT))
        assertEquals(FinalSyncMigrationStatus.FAILED, stageToStatus(MigrationStage.ERROR))
    }
}