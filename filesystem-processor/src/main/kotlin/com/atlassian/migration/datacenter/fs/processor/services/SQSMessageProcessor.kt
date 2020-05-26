package com.atlassian.migration.datacenter.fs.processor.services

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.event.S3EventNotification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.io.File
import java.util.function.Consumer

@Component
class SQSMessageProcessor(private val s3Client: AmazonS3, private val threadPoolTaskExecutor: ThreadPoolTaskExecutor, @Value("\${app.jira.file.path}") private val jiraHome: String) : MessageHandler {

    private val log = LoggerFactory.getLogger(SQSMessageProcessor::class.java)

    override fun handleMessage(message: Message<*>) {
        val payload = message.payload as? String
                ?: throw IllegalArgumentException("SQS message is not a string, we can't handle that")

        val s3EventNotificationRecord = S3EventNotification.parseJson(payload)
        if (log.isDebugEnabled) {
            log.debug("Received SQS message {}", s3EventNotificationRecord.toJson())
        }
        val s3EventNotificationRecords = s3EventNotificationRecord.records
        if (log.isDebugEnabled) {
            log.debug("Received " + s3EventNotificationRecords.size.toString() + " records from S3.")
        }
        val jiraHomePath = File(this.jiraHome)
        if (!jiraHomePath.exists()) {
            if (jiraHomePath.mkdir()) {
                if (log.isDebugEnabled) {
                    log.debug("Created Jira Home path " + jiraHomePath.absolutePath)
                }
            }
        }
        if (s3EventNotificationRecords.size == 1) {
            submitTask(s3Client, s3EventNotificationRecords[0].s3, jiraHome)
        } else if (s3EventNotificationRecords.size > 1) {
            s3EventNotificationRecords.forEach(Consumer { record: S3EventNotification.S3EventNotificationRecord -> submitTask(s3Client, record.s3, jiraHome) })
        }
    }

    private fun submitTask(s3Client: AmazonS3, item: S3EventNotification.S3Entity, jiraHome: String) {
        val fileWriter = S3ToFileWriter(s3Client, item, jiraHome)
        threadPoolTaskExecutor.submit(fileWriter)
    }


}