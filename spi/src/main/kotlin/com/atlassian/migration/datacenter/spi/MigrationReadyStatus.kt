package com.atlassian.migration.datacenter.spi

class MigrationReadyStatus(val dbCompatible: Boolean,
                           val osCompatible: Boolean,
                           val pgDumpAvailable: Boolean,
                           val pgDumpCompatible: Boolean)
