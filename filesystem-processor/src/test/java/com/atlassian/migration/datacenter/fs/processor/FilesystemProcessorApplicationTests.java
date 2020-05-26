package com.atlassian.migration.datacenter.fs.processor;

import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.atlassian.migration.datacenter.fs.processor.configuration.LocalStackEnvironmentVars;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.aws.context.config.annotation.EnableStackConfiguration;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.core.region.RegionProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest()
@ActiveProfiles("localstack,test")
@EnableStackConfiguration(stackName = "migration-helper")
@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(environmentVariableProvider = LocalStackEnvironmentVars.class, useSingleDockerContainer = true, services = "sqs,s3,ssm,cfn,ec2,cloudformation", imageTag = "0.10.8")
@Disabled
class FilesystemProcessorApplicationTests {

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private MessageChannel outboundChannel;

    @Autowired
    private RegionProvider regionProvider;

    @Autowired
    private ResourceIdResolver idResolver;

    @SneakyThrows
    private S3EventNotification createNotification() {

        String payload = RandomStringUtils.randomAlphabetic(75);
        String bucket = this.idResolver.resolveToPhysicalResourceId("MigrationBucket");

        this.s3Client.putObject(bucket, "bootstrap.properties", payload);

        S3EventNotification.S3ObjectEntity objEntity = new S3EventNotification.S3ObjectEntity("bootstrap.properties", 1L, null, null);
        S3EventNotification.S3BucketEntity bucketEntity = new S3EventNotification.S3BucketEntity(this.idResolver.resolveToPhysicalResourceId("MigrationBucket"), null, null);
        S3EventNotification.S3Entity entity = new S3EventNotification.S3Entity(null, bucketEntity, objEntity, null);

        S3EventNotification.S3EventNotificationRecord rec = new S3EventNotification.S3EventNotificationRecord(regionProvider.toString(), "s3:ObjectCreated:Put", null,
                "1970-01-01T00:00:00.000Z", null, null, null, entity, null);

        List<S3EventNotification.S3EventNotificationRecord> notifications = new ArrayList<>(2);
        notifications.add(rec);

        return new S3EventNotification(notifications);
    }

    @BeforeEach
    public void setup() {

    }

    @SneakyThrows
    @Test
    @Disabled
    public void testQueueSend() {
        final S3EventNotification eventNotification = createNotification();
        Message<S3EventNotification> message = MessageBuilder.withPayload(eventNotification)
                .setHeader("aws_queue", "MigrationQueue")
                .build();
        assertTrue(this.outboundChannel.send(message), "Message not sent to channel");
    }

}
