package com.atlassian.migration.datacenter.fs.processor

import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.atlassian.migration.datacenter"])
open class FileSystemProcessorApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<FileSystemProcessorApplication>(*args) {
                this.webApplicationType = WebApplicationType.NONE
                this.setBannerMode(Banner.Mode.OFF)
            }
        }
    }


}