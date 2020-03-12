package com.atlassian.migration.datacenter.configuration;

import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Configuration
public class MigrationAssistantBeanConfiguration {

    @Bean
    public S3AsyncClient buildS3Client(AwsCredentialsProvider credentialsProvider, RegionService regionService) {
        return S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
    }
}
