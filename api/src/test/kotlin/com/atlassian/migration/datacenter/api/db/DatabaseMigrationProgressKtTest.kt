package com.atlassian.migration.datacenter.api.db

import com.atlassian.migration.datacenter.spi.MigrationStage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DatabaseMigrationProgressKtTest {

    @Test
    fun testStageMapping() {
        assertEquals(DbMigrationStatus.NOT_STARTED, stageToStatus(MigrationStage.PROVISION_APPLICATION))
        assertEquals(DbMigrationStatus.EXPORTING, stageToStatus(MigrationStage.DB_MIGRATION_EXPORT))
        assertEquals(DbMigrationStatus.FAILED, stageToStatus(MigrationStage.ERROR))
        assertEquals(DbMigrationStatus.IMPORTING, stageToStatus(MigrationStage.FINAL_SYNC_WAIT))
        assertEquals(DbMigrationStatus.IMPORTING, stageToStatus(MigrationStage.DATA_MIGRATION_IMPORT_WAIT))
    }
}