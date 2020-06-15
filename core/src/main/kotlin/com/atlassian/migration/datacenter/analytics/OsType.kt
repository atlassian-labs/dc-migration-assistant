package com.atlassian.migration.datacenter.analytics

import org.apache.commons.lang3.SystemUtils

enum class OsType {
    Linux,
    Mac,
    Windows,
    UnixOther,
    Other;

    companion object {
        @JvmStatic
        fun fromSystem(): OsType {
            return if (SystemUtils.IS_OS_LINUX) {
                Linux
            } else if (SystemUtils.IS_OS_MAC) {
                Mac
            } else if (SystemUtils.IS_OS_WINDOWS) {
                Windows
            } else if (SystemUtils.IS_OS_UNIX) {
                UnixOther
            } else {
                Other
            }
        }
    }
}