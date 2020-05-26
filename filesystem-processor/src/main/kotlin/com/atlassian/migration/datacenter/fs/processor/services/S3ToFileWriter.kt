package com.atlassian.migration.datacenter.fs.processor.services

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.event.S3EventNotification
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset

class S3ToFileWriter(private val s3Client: AmazonS3, private val entity: S3EventNotification.S3Entity, private val jiraHome: String) : Runnable {

    private val log = LoggerFactory.getLogger(S3ToFileWriter::class.java)

    override fun run() {
        val key = URLDecoder.decode(entity.getObject().key, Charset.defaultCharset().toString())
        try {
            s3Client.getObject(entity.bucket.name, key).use { s3object ->
                val absolutePathString = "$jiraHome/$key"
                val localPath = File(absolutePathString)
                if (key.endsWith("/")) {
                    log.info("Got request to create directory: $absolutePathString")
                    localPath.mkdirs()
                } else {
                    s3object.objectContent.use { inputStream ->
                        log.info("Got request to write file: $absolutePathString")
                        if (!localPath.parentFile.exists()) {
                            if (localPath.parentFile.mkdirs()) {
                                log.info("Made the missing parent directory {}", localPath.path)
                            }
                        }
                        try {
                            IOUtils.copy(inputStream, FileOutputStream(localPath))
                            log.info("Successfully wrote: $absolutePathString")
                        } catch (e: IOException) {
                            log.error("Failed to write file $absolutePathString", e)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            log.error("Failed to process " + ex.localizedMessage)
            log.error(ex.cause?.localizedMessage)
        }
    }
}