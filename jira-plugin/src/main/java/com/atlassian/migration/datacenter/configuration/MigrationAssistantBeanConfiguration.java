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

package com.atlassian.migration.datacenter.configuration;

import java.nio.file.Paths;
import java.util.function.Supplier;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.JiraConfiguration;
import com.atlassian.migration.datacenter.core.aws.AllowAnyTransitionMigrationServiceFacade;
import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.auth.EncryptedCredentialsStorage;
import com.atlassian.migration.datacenter.core.aws.auth.ProbeAWSAuth;
import com.atlassian.migration.datacenter.core.aws.auth.ReadCredentialsService;
import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import com.atlassian.migration.datacenter.core.aws.cloud.AWSConfigurationService;
import com.atlassian.migration.datacenter.core.aws.cloud.DefaultAwsCloudCredentialsValidator;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseArchivalService;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseArchiveStageTransitionCallback;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseArtifactS3UploadService;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseUploadStageTransitionCallback;
import com.atlassian.migration.datacenter.core.aws.db.restore.DatabaseRestoreStageTransitionCallback;
import com.atlassian.migration.datacenter.core.aws.db.restore.SsmPsqlDatabaseRestoreService;
import com.atlassian.migration.datacenter.core.aws.db.restore.TargetDbCredentialsStorageService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.QuickstartDeploymentService;
import com.atlassian.migration.datacenter.core.aws.region.AvailabilityZoneManager;
import com.atlassian.migration.datacenter.core.aws.region.PluginSettingsRegionManager;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
import com.atlassian.migration.datacenter.core.fs.S3FilesystemMigrationService;
import com.atlassian.migration.datacenter.core.fs.captor.AttachmentPathCaptor;
import com.atlassian.migration.datacenter.core.fs.captor.AttachmentSyncManager;
import com.atlassian.migration.datacenter.core.fs.captor.DefaultAttachmentSyncManager;
import com.atlassian.migration.datacenter.core.fs.copy.S3BulkCopy;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloadManager;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.core.fs.listener.JiraIssueAttachmentListener;
import com.atlassian.migration.datacenter.core.util.EncryptionManager;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.SchedulerService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class MigrationAssistantBeanConfiguration {

    @Bean
    public Supplier<S3AsyncClient> s3AsyncClientSupplier(AwsCredentialsProvider credentialsProvider, RegionService regionService) {
        return () -> S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
    }

    @Bean
    public Supplier<SsmClient> ssmClient(AwsCredentialsProvider credentialsProvider, RegionService regionService) {
        return () -> SsmClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
    }

    @Bean
    public Supplier<SecretsManagerClient> secretsManagerClient(AwsCredentialsProvider credentialsProvider, RegionService regionService) {
        return () -> SecretsManagerClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
    }

    @Bean Supplier<AutoScalingClient> autoScalingClient(AwsCredentialsProvider credentialsProvider, RegionService regionService) {
        return () -> AutoScalingClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(ReadCredentialsService readCredentialsService) {
        return new AtlassianPluginAWSCredentialsProvider(readCredentialsService);
    }

    @Bean
    public EncryptionManager encryptionManager(JiraHome jiraHome) {
        return new EncryptionManager(jiraHome);
    }

    @Bean
    public EncryptedCredentialsStorage encryptedCredentialsStorage(Supplier<PluginSettingsFactory> pluginSettingsFactorySupplier, EncryptionManager encryptionManager) {
        return new EncryptedCredentialsStorage(pluginSettingsFactorySupplier, encryptionManager);
    }

    @Bean
    public TargetDbCredentialsStorageService targetDbCredentialsStorageService(Supplier<SecretsManagerClient> clientSupplier, MigrationService migrationService) {
        return new TargetDbCredentialsStorageService(clientSupplier, migrationService);
    }

    @Bean
    public RegionService regionService(Supplier<PluginSettingsFactory> pluginSettingsFactorySupplier, GlobalInfrastructure globalInfrastructure) {
        return new PluginSettingsRegionManager(pluginSettingsFactorySupplier, globalInfrastructure);
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
    public DatabaseArchiveStageTransitionCallback archiveStageTransitionCallback(MigrationService migrationService) {
        return new DatabaseArchiveStageTransitionCallback(migrationService);
    }

    @Bean
    public DatabaseArtifactS3UploadService databaseArtifactS3UploadService(Supplier<S3AsyncClient> s3AsyncClientSupplier) {
        return new DatabaseArtifactS3UploadService(s3AsyncClientSupplier);
    }

    @Bean
    public DatabaseUploadStageTransitionCallback databaseUploadStageTransitionCallback(MigrationService migrationService) {
        return new DatabaseUploadStageTransitionCallback(migrationService);
    }

    @Bean
    public SsmPsqlDatabaseRestoreService ssmPsqlDatabaseRestoreService(SSMApi ssm, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        return new SsmPsqlDatabaseRestoreService(ssm, migrationHelperDeploymentService);
    }

    @Bean
    public DatabaseRestoreStageTransitionCallback databaseRestoreStageTransitionCallback(MigrationService migrationService) {
        return new DatabaseRestoreStageTransitionCallback(migrationService);
    }

    @Bean
    public DatabaseMigrationService databaseMigrationService(MigrationService databaseMigrationService,
                                                             MigrationRunner migrationRunner,
                                                             DatabaseArchivalService databaseArchivalService,
                                                             DatabaseArchiveStageTransitionCallback archiveStageTransitionCallback,
                                                             DatabaseArtifactS3UploadService s3UploadService,
                                                             DatabaseUploadStageTransitionCallback uploadStageTransitionCallback,
                                                             SsmPsqlDatabaseRestoreService restoreService,
                                                             DatabaseRestoreStageTransitionCallback restoreStageTransitionCallback, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        String tempDirectoryPath = System.getProperty("java.io.tmpdir");
        return new DatabaseMigrationService(
                Paths.get(tempDirectoryPath),
                databaseMigrationService,
                migrationRunner,
                databaseArchivalService,
                archiveStageTransitionCallback,
                s3UploadService,
                uploadStageTransitionCallback,
                restoreService,
                restoreStageTransitionCallback,
                migrationHelperDeploymentService);
    }

    @Bean
    public MigrationService migrationService(ActiveObjects activeObjects, ApplicationConfiguration applicationConfiguration, JiraHome jiraHome) {
        return new AllowAnyTransitionMigrationServiceFacade(activeObjects, applicationConfiguration, jiraHome);
    }

    @Bean
    public SSMApi ssmApi(Supplier<SsmClient> client, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        return new SSMApi(client, migrationHelperDeploymentService);
    }

    @Bean
    public S3SyncFileSystemDownloader s3SyncFileSystemDownloader(SSMApi ssmApi, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        return new S3SyncFileSystemDownloader(ssmApi, migrationHelperDeploymentService);
    }

    @Bean
    public DatabaseExtractor databaseExtractor(ApplicationConfiguration applicationConfiguration) {
        return DatabaseExtractorFactory.getExtractor(applicationConfiguration);
    }

    @Bean
    public DatabaseArchivalService databaseArchivalService(DatabaseExtractor databaseExtractor) {
        return new DatabaseArchivalService(databaseExtractor);
    }

    @Bean
    public S3SyncFileSystemDownloadManager s3SyncFileSystemDownloadManager(S3SyncFileSystemDownloader downloader) {
        return new S3SyncFileSystemDownloadManager(downloader);
    }

    @Bean
    public AvailabilityZoneManager availabilityZoneManager(AwsCredentialsProvider awsCredentialsProvider, GlobalInfrastructure globalInfrastructure) {
        return new AvailabilityZoneManager(awsCredentialsProvider, globalInfrastructure);
    }

    @Bean
    public AWSConfigurationService awsConfigurationService(WriteCredentialsService writeCredentialsService, RegionService regionService, MigrationService migrationService) {
        return new AWSConfigurationService(writeCredentialsService, regionService, migrationService, new DefaultAwsCloudCredentialsValidator());
    }

    @Bean
    public CfnApi cfnApi(AwsCredentialsProvider awsCredentialsProvider, RegionService regionService) {
        return new CfnApi(awsCredentialsProvider, regionService);
    }

    @Bean
    public MigrationRunner migrationRunner(SchedulerService schedulerService) {
        return new MigrationRunner(schedulerService);
    }

    @Bean
    public FilesystemMigrationService filesystemMigrationService(Environment environment, S3SyncFileSystemDownloadManager downloadManager, MigrationService migrationService, MigrationRunner migrationRunner, JiraIssueAttachmentListener attachmentListener, S3BulkCopy bulkCopy) {
        return new S3FilesystemMigrationService(environment, downloadManager, migrationService, migrationRunner, attachmentListener, bulkCopy);
    }

    @Bean
    public S3BulkCopy s3BulkCopy(Supplier<S3AsyncClient> clientSupplier, AWSMigrationHelperDeploymentService helperDeploymentService, JiraHome jiraHome) {
        return new S3BulkCopy(clientSupplier, helperDeploymentService, jiraHome);
    }

    @Bean
    public QuickstartDeploymentService quickstartDeploymentService(CfnApi cfnApi, MigrationService migrationService, TargetDbCredentialsStorageService dbCredentialsStorageService, AWSMigrationHelperDeploymentService awsMigrationHelperDeploymentService) {
        return new QuickstartDeploymentService(cfnApi, migrationService, dbCredentialsStorageService, awsMigrationHelperDeploymentService);
    }

    @Bean
    public AWSMigrationHelperDeploymentService awsMigrationHelperDeploymentService(CfnApi cfnApi, MigrationService migrationService, Supplier<AutoScalingClient> autoScalingClientFactory) {
        return new AWSMigrationHelperDeploymentService(cfnApi, autoScalingClientFactory, migrationService);
    }

    @Bean
    public JiraIssueAttachmentListener jiraIssueAttachmentListener(EventPublisher eventPublisher, AttachmentPathCaptor attachmentPathCaptor, AttachmentStore attachmentStore) {
        return new JiraIssueAttachmentListener(eventPublisher, attachmentPathCaptor, attachmentStore);
    }

    @Bean
    public AttachmentSyncManager attachmentCaptor(ActiveObjects activeObjects, MigrationService migrationService) {
        return new DefaultAttachmentSyncManager(activeObjects, migrationService);
    }
}
