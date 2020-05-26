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