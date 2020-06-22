package com.atlassian.migration.datacenter.core.fs

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport

interface FileSystemMigrationReportManager {

    /**
     * Return the current migration report for the specified transfer type.
     *
     * @return migration report
     */
    fun getCurrentReport(type: ReportType): FileSystemMigrationReport?

    /**
     * Reset the current specified report.
     *
     * @return the new report
     */
    fun resetReport(type: ReportType): FileSystemMigrationReport
}