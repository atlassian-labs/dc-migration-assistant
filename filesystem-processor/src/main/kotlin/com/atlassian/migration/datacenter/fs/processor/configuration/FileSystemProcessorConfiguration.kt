package com.atlassian.migration.datacenter.fs.processor.configuration

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.atlassian.migration.datacenter.fs.processor.services.SQSMessageProcessor
import org.springframework.cloud.aws.context.config.annotation.EnableStackConfiguration
import org.springframework.cloud.aws.core.env.ResourceIdResolver
import org.springframework.cloud.aws.messaging.support.destination.DynamicQueueUrlDestinationResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter
import org.springframework.integration.channel.ExecutorChannel
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.integration.core.MessageProducer
import org.springframework.integration.dispatcher.RoundRobinLoadBalancingStrategy
import org.springframework.integration.endpoint.EventDrivenConsumer
import org.springframework.integration.handler.LoggingHandler
import org.springframework.messaging.SubscribableChannel
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


@Configuration
@ComponentScan
@EnableStackConfiguration
open class FileSystemProcessorConfiguration {

    @Bean
    @ServiceActivator(inputChannel = "errorChannel")
    open fun errorLogging(): LoggingHandler {
        val adapter = LoggingHandler(LoggingHandler.Level.INFO)
        adapter.setLoggerName("ERROR_LOGGER")
        adapter.setLogExpressionString("headers.id + ': ' + payload")
        return adapter
    }

    @Bean
    @ServiceActivator(inputChannel = "loggingChannel")
    open fun discardLogging(): LoggingHandler {
        val adapter = LoggingHandler(LoggingHandler.Level.INFO)
        adapter.setLoggerName("DISCARD_LOGGER")
        adapter.setLogExpressionString("headers.id + ': ' + payload")
        return adapter
    }

    @Bean
    open fun loggingChannel(): SubscribableChannel {
        return PublishSubscribeChannel()
    }


    @Bean
    open fun sqsMessageDrivenChannelAdapter(destinationResolver: DynamicQueueUrlDestinationResolver, errorChannel: PublishSubscribeChannel, inboundChannel: SubscribableChannel, amazonSqs: AmazonSQSAsync): MessageProducer {
        val adapter = SqsMessageDrivenChannelAdapter(amazonSqs, QUEUE_LOGICAL_NAME)
        adapter.setDestinationResolver(destinationResolver)
        adapter.setQueueStopTimeout(60000)
        adapter.outputChannel = inboundChannel
        adapter.errorChannel = errorChannel
        return adapter
    }

    @Bean
    open fun inboundChannel(threadPoolTaskExecutor: ThreadPoolTaskExecutor): SubscribableChannel {
        return ExecutorChannel(threadPoolTaskExecutor, RoundRobinLoadBalancingStrategy())
    }

    @Bean
    open fun filteredChannel(threadPoolTaskExecutor: ThreadPoolTaskExecutor): SubscribableChannel {
        return ExecutorChannel(threadPoolTaskExecutor, RoundRobinLoadBalancingStrategy())
    }

    @Bean
    open fun consumer(filteredChannel: SubscribableChannel, sqsMessageProcessor: SQSMessageProcessor): EventDrivenConsumer {
        return EventDrivenConsumer(filteredChannel, sqsMessageProcessor)
    }

    @Bean
    open fun dynamicQueueUrlDestinationResolver(idResolver: ResourceIdResolver, amazonSqs: AmazonSQSAsync): DynamicQueueUrlDestinationResolver {
        return DynamicQueueUrlDestinationResolver(amazonSqs, idResolver)
    }

    companion object {
        private const val QUEUE_LOGICAL_NAME: String = "MigrationQueue"
    }
}