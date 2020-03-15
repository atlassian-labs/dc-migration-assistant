package com.atlassian.migration.datacenter.configuration;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.JiraConfiguration;
import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.auth.EncryptedCredentialsStorage;
import com.atlassian.migration.datacenter.core.aws.auth.ProbeAWSAuth;
import com.atlassian.migration.datacenter.core.aws.auth.ReadCredentialsService;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService;
import com.atlassian.migration.datacenter.core.aws.region.PluginSettingsRegionManager;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Paths;

@Configuration
@Import({MigrationAssistantOsgiImportConfiguration.class})
public class MigrationAssistantBeanConfiguration {

    @Bean
    public S3AsyncClient s3AsyncClient(AwsCredentialsProvider credentialsProvider, RegionService regionService) {
        return S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(ReadCredentialsService readCredentialsService) {
        return new AtlassianPluginAWSCredentialsProvider(readCredentialsService);
    }

    @Bean
    public ReadCredentialsService readCredentialsService(PluginSettingsFactory pluginSettingsFactory, JiraHome jiraHome) {
        return new EncryptedCredentialsStorage(pluginSettingsFactory, jiraHome);
    }

    @Bean
    public RegionService regionService(PluginSettingsFactory pluginSettingsFactory, GlobalInfrastructure globalInfrastructure) {
        return new PluginSettingsRegionManager(pluginSettingsFactory, globalInfrastructure);
    }

    @Bean
    public GlobalInfrastructure globalInfrastructure() {
        return new GlobalInfrastructure();
    }

    @Bean
    public ProbeAWSAuth probeAWSAuth(AwsCredentialsProvider awsCredentialsProvider, RegionService regionService) {
        return new ProbeAWSAuth(awsCredentialsProvider, regionService);
    }

    @Bean
    public JiraConfiguration jiraConfiguration(JiraHome jiraHome) {
        return new JiraConfiguration(jiraHome);
    }

    @Bean
    public DatabaseMigrationService databaseMigrationService(ApplicationConfiguration jiraConfiguration, S3AsyncClient s3AsyncClient) {
        String tempDirectoryPath = System.getProperty("java.io.tmpdir");
        return new DatabaseMigrationService(jiraConfiguration, Paths.get(tempDirectoryPath), s3AsyncClient);
    }
}
