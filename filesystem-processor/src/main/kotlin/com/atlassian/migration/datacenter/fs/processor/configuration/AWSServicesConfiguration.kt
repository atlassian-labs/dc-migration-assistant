/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.migration.datacenter.fs.processor.configuration

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.AmazonCloudFormationAsyncClient
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2AsyncClient
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import com.atlassian.migration.datacenter.fs.processor.configuration.AWSServicesConfiguration.Companion.STACK_NAME
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.context.annotation.ConditionalOnMissingAmazonClient
import org.springframework.cloud.aws.context.config.annotation.EnableStackConfiguration
import org.springframework.cloud.aws.core.config.AmazonWebserviceClientFactoryBean
import org.springframework.cloud.aws.core.region.RegionProvider
import org.springframework.cloud.aws.core.region.StaticRegionProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("production")
@EnableStackConfiguration
open class AWSServicesConfiguration : IAWSServicesConfiguration {

    @Bean
    override fun regionProvider(@Value("\${app.region.id}") regionId: String): RegionProvider {
        return StaticRegionProvider(regionId)
    }

    @Bean
    @Primary
    override fun credentialsProvider(): DefaultAWSCredentialsProviderChain {
        return DefaultAWSCredentialsProviderChain()
    }

    @Bean
    @Primary
    @ConditionalOnMissingAmazonClient(AmazonEC2::class)
    override fun amazonEc2Client(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonEC2AsyncClient> {
        return AmazonWebserviceClientFactoryBean(AmazonEC2AsyncClient::class.java, credentialsProvider, regionProvider)
    }

    @Bean
    @Primary
    @ConditionalOnMissingAmazonClient(AmazonSQS::class)
    override fun awsSqsClient(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonSQSAsyncClient> {
        return AmazonWebserviceClientFactoryBean(AmazonSQSAsyncClient::class.java, credentialsProvider, regionProvider)
    }

    @Bean
    @Primary
    @ConditionalOnMissingAmazonClient(AmazonS3::class)
    override fun amazonS3Client(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonS3Client> {
        return AmazonWebserviceClientFactoryBean(AmazonS3Client::class.java, credentialsProvider, regionProvider)
    }

    @Bean
    @Primary
    @ConditionalOnMissingAmazonClient(AmazonCloudFormation::class)
    override fun amazonCloudFormationClient(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonCloudFormationAsyncClient> {
        return AmazonWebserviceClientFactoryBean(AmazonCloudFormationAsyncClient::class.java, credentialsProvider, regionProvider)
    }

    companion object {
        const val STACK_NAME = "migration-helper"
    }

}
