package com.atlassian.migration.datacenter.spi

/**
 *
 * A Migration Service that can be unscheduled
 */
interface CancellableMigrationService {
    /**
     * Unschedule a scheduled job for a migration
     *
     * @param migrationId an identifier for a migration that spawned the scheduled job
     *
     * @return true if the job was unscheduled, false when the job to unschedule does not exist
     */
    fun unscheduleMigration(migrationId: Int): Boolean
}