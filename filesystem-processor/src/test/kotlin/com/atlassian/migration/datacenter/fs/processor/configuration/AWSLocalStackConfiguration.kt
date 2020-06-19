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
import com.amazonaws.services.cloudformation.AmazonCloudFormationAsyncClient
import com.amazonaws.services.ec2.AmazonEC2AsyncClient
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementAsyncClient
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.core.config.AmazonWebserviceClientFactoryBean
import org.springframework.cloud.aws.core.region.RegionProvider
import org.springframework.cloud.aws.core.region.StaticRegionProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("localstack")
open class AWSLocalStackConfiguration : IAWSServicesConfiguration {

    @Value("\${app.aws.sqs.localStack.endpoint}")
    lateinit var awsSQSLocalStackEndpoint: String

    @Value("\${app.aws.s3.localStack.endpoint}")
    lateinit var awsS3LocalStackEndpoint: String

    @Value("\${app.aws.ec2.localStack.endpoint}")
    lateinit var awsEC2LocalStackEndpoint: String

    @Value("\${app.aws.cloudformation.localStack.endpoint}")
    lateinit var awsCloudFormationLocalStackEndpoint: String

    @Value("\${app.aws.ssm.localStack.endpoint}")
    lateinit var awsSSMLocalStackEndpoint: String

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
    override fun amazonEc2Client(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonEC2AsyncClient> {
        return LocalStackClientFactoryBean(AmazonEC2AsyncClient::class.java, credentialsProvider, regionProvider, this.awsEC2LocalStackEndpoint)
    }

    @Bean
    @Primary
    override fun awsSqsClient(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonSQSAsyncClient> {
        return LocalStackClientFactoryBean(AmazonSQSAsyncClient::class.java, credentialsProvider, regionProvider, this.awsSQSLocalStackEndpoint)
    }

    @Bean
    @Primary
    override fun amazonCloudFormationClient(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonCloudFormationAsyncClient> {
        return LocalStackClientFactoryBean(AmazonCloudFormationAsyncClient::class.java, credentialsProvider, regionProvider, this.awsCloudFormationLocalStackEndpoint)
    }

    @Bean
    @Primary
    open fun awsS3Client(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonS3Client> {
        return this.amazonS3Client(regionProvider, credentialsProvider)
    }

    @Bean
    override fun amazonS3Client(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonS3Client> {
        return LocalStackClientFactoryBean(AmazonS3Client::class.java, credentialsProvider, regionProvider, this.awsS3LocalStackEndpoint)
    }

    @Bean
    @Primary
    open fun awsSSM(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AWSSimpleSystemsManagementAsyncClient> {
        return LocalStackClientFactoryBean(AWSSimpleSystemsManagementAsyncClient::class.java, credentialsProvider, regionProvider, this.awsSSMLocalStackEndpoint)
    }

}