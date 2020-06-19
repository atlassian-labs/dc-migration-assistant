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
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.core.config.AmazonWebserviceClientFactoryBean
import org.springframework.cloud.aws.core.region.RegionProvider

interface IAWSServicesConfiguration {

    fun regionProvider(@Value("\${app.region.id}") regionId: String): RegionProvider

    fun credentialsProvider(): DefaultAWSCredentialsProviderChain

    fun amazonEc2Client(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonEC2AsyncClient>

    fun awsSqsClient(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonSQSAsyncClient>

    fun amazonS3Client(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonS3Client>

    fun amazonCloudFormationClient(regionProvider: RegionProvider, credentialsProvider: AWSCredentialsProvider): AmazonWebserviceClientFactoryBean<AmazonCloudFormationAsyncClient>
}