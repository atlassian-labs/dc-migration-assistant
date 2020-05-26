package com.atlassian.migration.datacenter.fs.processor.configuration;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.messaging.support.destination.DynamicQueueUrlDestinationResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.support.PeriodicTrigger;

@Configuration
public class TestMessagingConfig {

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(200));
        return pollerMetadata;
    }

    @Bean
    public QueueChannel outboundChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel transformChannel() {
        return new QueueChannel();
    }

    @Transformer(inputChannel = "outboundChannel", outputChannel = "transformChannel")
    public String transformPayload(S3EventNotification raw) {
        return raw.toJson();
    }

    @Bean
    @ServiceActivator(inputChannel = "transformChannel")
    public MessageHandler sqsMessageHandler(AmazonSQSAsync amazonSQS, ResourceIdResolver idResolver) {
        final DynamicQueueUrlDestinationResolver destinationResolver = new DynamicQueueUrlDestinationResolver(amazonSQS, idResolver);
        final SqsMessageHandler messageHandler = new SqsMessageHandler(amazonSQS, destinationResolver);
        messageHandler.setMessageDeduplicationId("headers.id");
        messageHandler.setMessageGroupIdExpressionString("headers.timestamp");
        return messageHandler;
    }

}
