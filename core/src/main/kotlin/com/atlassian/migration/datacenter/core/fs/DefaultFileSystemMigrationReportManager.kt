package com.atlassian.migration.datacenter.core.fs

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultFileSystemMigrationReportManager : FileSystemMigrationReportManager {

    val reports: HashMap<ReportType, FileSystemMigrationReport> = hashMapOf(
            ReportType.Filesystem to DefaultFileSystemMigrationReport(),
            ReportType.Database to DefaultFileSystemMigrationReport(),
            ReportType.Final to DefaultFileSystemMigrationReport()
    )

    override fun resetReport(type: ReportType): FileSystemMigrationReport {
        val report = DefaultFileSystemMigrationReport()
        reports[type] = report
        return report
    }

    override fun getCurrentReport(type: ReportType): FileSystemMigrationReport? {
        return reports[type]
    }
}
