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

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.JiraConfiguration;
import com.atlassian.migration.datacenter.core.aws.AllowAnyTransitionMigrationServiceFacade;
import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.migration.datacenter.core.aws.SqsApi;
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
import com.atlassian.migration.datacenter.core.aws.infrastructure.cleanup.AWSCleanupTaskFactory;
import com.atlassian.migration.datacenter.core.aws.infrastructure.cleanup.AWSMigrationInfrastructureCleanupService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.AtlassianInfrastructureService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.QuickstartDeploymentService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.cleanup.AWSMigrationStackCleanupService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.cleanup.DatabaseSecretCleanupService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.cleanup.MigrationBucketCleanupService;
import com.atlassian.migration.datacenter.core.aws.infrastructure.migrationStack.MigrationStackInputGatheringStrategyFactory;
import com.atlassian.migration.datacenter.core.aws.infrastructure.migrationStack.QuickstartStandaloneMigrationStackInputGatheringStrategy;
import com.atlassian.migration.datacenter.core.aws.infrastructure.migrationStack.QuickstartWithVPCMigrationStackInputGatheringStrategy;
import com.atlassian.migration.datacenter.core.aws.region.AvailabilityZoneManager;
import com.atlassian.migration.datacenter.core.aws.region.PluginSettingsRegionManager;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractor;
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory;
import com.atlassian.migration.datacenter.core.fs.S3FilesystemMigrationService;
import com.atlassian.migration.datacenter.core.fs.captor.AttachmentSyncManager;
import com.atlassian.migration.datacenter.core.fs.captor.DefaultAttachmentSyncManager;
import com.atlassian.migration.datacenter.core.fs.captor.QueueWatcher;
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncRunner;
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncService;
import com.atlassian.migration.datacenter.core.fs.captor.SqsQueueWatcher;
import com.atlassian.migration.datacenter.core.fs.copy.S3BulkCopy;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloadManager;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.core.fs.jira.captor.AttachmentCaptor;
import com.atlassian.migration.datacenter.core.fs.jira.captor.DefaultAttachmentCaptor;
import com.atlassian.migration.datacenter.core.fs.jira.listener.JiraIssueAttachmentListener;
import com.atlassian.migration.datacenter.core.util.EncryptionManager;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureCleanupService;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.SchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.nio.file.Paths;
import java.util.function.Supplier;

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
    public Supplier<S3Client> s3ClientSupplier(AwsCredentialsProvider credentialsProvider, RegionService regionService) {
        return () -> S3Client.builder()
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

    @Bean Supplier<SqsAsyncClient> sqsAsyncClient(AwsCredentialsProvider awsCredentialsProvider, RegionService regionService){
        return () -> SqsAsyncClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(ReadCredentialsService readCredentialsService) {
        return new AtlassianPluginAWSCredentialsProvider(readCredentialsService);
    }

    @Bean
    public EncryptionManager encryptionManager(JiraHome jiraHome) {
        return new EncryptionManager(jiraHome.getHome().toPath());
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
    public JiraConfiguration jiraConfiguration(JiraHome jiraHome, PluginAccessor pluginAccessor) {
        return new JiraConfiguration(jiraHome, pluginAccessor);
    }

    @Bean
    public DatabaseArchiveStageTransitionCallback archiveStageTransitionCallback(MigrationService migrationService) {
        return new DatabaseArchiveStageTransitionCallback(migrationService);
    }

    @Bean
    public DatabaseUploadStageTransitionCallback databaseUploadStageTransitionCallback(MigrationService migrationService) {
        return new DatabaseUploadStageTransitionCallback(migrationService);
    }

    @Bean
    public DatabaseArtifactS3UploadService databaseArtifactS3UploadService(Supplier<S3AsyncClient> s3AsyncClientSupplier, DatabaseUploadStageTransitionCallback uploadStageTransitionCallback) {
        return new DatabaseArtifactS3UploadService(s3AsyncClientSupplier, uploadStageTransitionCallback);
    }

    @Bean
    public DatabaseRestoreStageTransitionCallback databaseRestoreStageTransitionCallback(MigrationService migrationService) {
        return new DatabaseRestoreStageTransitionCallback(migrationService);
    }

    @Bean
    public SsmPsqlDatabaseRestoreService ssmPsqlDatabaseRestoreService(SSMApi ssm, AWSMigrationHelperDeploymentService migrationHelperDeploymentService, DatabaseRestoreStageTransitionCallback restoreStageTransitionCallback) {
        return new SsmPsqlDatabaseRestoreService(ssm, migrationHelperDeploymentService, restoreStageTransitionCallback);
    }

    @Bean
    public DatabaseMigrationService databaseMigrationService(MigrationService migrationService,
                                                             MigrationRunner migrationRunner,
                                                             DatabaseArchivalService databaseArchivalService,
                                                             DatabaseArtifactS3UploadService s3UploadService,
                                                             SsmPsqlDatabaseRestoreService restoreService, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        String tempDirectoryPath = System.getProperty("java.io.tmpdir");
        return new DatabaseMigrationService(
                Paths.get(tempDirectoryPath),
                migrationService,
                migrationRunner,
                databaseArchivalService,
                s3UploadService,
                restoreService,
                migrationHelperDeploymentService);
    }

    @Bean
    public MigrationService migrationService(ActiveObjects activeObjects, ApplicationConfiguration applicationConfiguration, JiraHome jiraHome, EventPublisher eventPublisher) {
        return new AllowAnyTransitionMigrationServiceFacade(activeObjects, applicationConfiguration, jiraHome.getHome().toPath(), eventPublisher);
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
    public DatabaseArchivalService databaseArchivalService(DatabaseExtractor databaseExtractor, DatabaseArchiveStageTransitionCallback archiveStageTransitionCallback) {
        return new DatabaseArchivalService(databaseExtractor, archiveStageTransitionCallback);
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
        return new S3BulkCopy(clientSupplier, helperDeploymentService, jiraHome.getHome().toPath());
    }

    @Bean
    public QuickstartDeploymentService quickstartDeploymentService(
            CfnApi cfnApi,
            MigrationService migrationService,
            TargetDbCredentialsStorageService dbCredentialsStorageService,
            AWSMigrationHelperDeploymentService awsMigrationHelperDeploymentService,
            MigrationStackInputGatheringStrategyFactory strategyFactory) {
        return new QuickstartDeploymentService(cfnApi, migrationService, dbCredentialsStorageService, awsMigrationHelperDeploymentService, strategyFactory);
    }

    @Bean
    public AWSMigrationHelperDeploymentService awsMigrationHelperDeploymentService(CfnApi cfnApi, MigrationService migrationService, Supplier<AutoScalingClient> autoScalingClientFactory) {
        return new AWSMigrationHelperDeploymentService(cfnApi, autoScalingClientFactory, migrationService);
    }

    @Bean
    public JiraIssueAttachmentListener jiraIssueAttachmentListener(EventPublisher eventPublisher, AttachmentCaptor attachmentCaptor) {
        return new JiraIssueAttachmentListener(eventPublisher, attachmentCaptor);
    }

    @Bean
    public AttachmentSyncManager attachmentSyncManager(ActiveObjects activeObjects, MigrationService migrationService) {
        return new DefaultAttachmentSyncManager(activeObjects, migrationService);
    }

    @Bean
    public AttachmentCaptor attachmentCaptor(ActiveObjects activeObjects, MigrationService migrationService, AttachmentStore attachmentStore) {
        return new DefaultAttachmentCaptor(activeObjects, migrationService, attachmentStore);
    }

    @Bean
    public AtlassianInfrastructureService atlassianInfrastructureService(CfnApi cfnApi) {
        return new AtlassianInfrastructureService(cfnApi);
    }

    @Bean
    public QueueWatcher queueWatcher(MigrationService migrationService, SqsApi sqsApi) {
        return new SqsQueueWatcher(sqsApi, migrationService);
    }

    @Bean
    public S3FinalSyncRunner s3FinalSyncRunner(AttachmentSyncManager attachmentSyncManager, Supplier<S3AsyncClient> s3ClientSupplier, JiraHome jiraHome, AWSMigrationHelperDeploymentService helperDeploymentService, QueueWatcher queueWatcher, JiraIssueAttachmentListener attachmentListener) {
        return new S3FinalSyncRunner(attachmentSyncManager, s3ClientSupplier, jiraHome.getHome().toPath(), helperDeploymentService, queueWatcher, attachmentListener);
    }

    @Bean
    public SqsApi emptyQueueSqsApi() {
        return queueUrl -> 0;
    }

    @Bean
    public S3FinalSyncService s3FinalSyncService(MigrationRunner migrationRunner, S3FinalSyncRunner finalSyncRunner, MigrationService migrationService, SqsApi sqsApi, QueueWatcher queueWatcher, AttachmentSyncManager attachmentSyncManager) {
        return new S3FinalSyncService(migrationRunner, finalSyncRunner, migrationService, sqsApi, attachmentSyncManager);
    }

    @Bean
    public DatabaseSecretCleanupService databaseSecretCleanupService(Supplier<SecretsManagerClient> secretsManagerClientSupplier, TargetDbCredentialsStorageService targetDbCredentialsStorageService
    ) {
        return new DatabaseSecretCleanupService(secretsManagerClientSupplier, targetDbCredentialsStorageService);
    }

    @Bean
    public AWSMigrationStackCleanupService migrationStackCleanupService(CfnApi cfnApi, MigrationService migrationService) {
        return new AWSMigrationStackCleanupService(cfnApi, migrationService);
    }

    @Bean
    public MigrationBucketCleanupService bucketCleanupService(MigrationService migrationService, Supplier<S3Client> clientSupplier) {
        return new MigrationBucketCleanupService(migrationService, clientSupplier);
    }

    @Bean
    public AWSCleanupTaskFactory cleanupTaskFactory(DatabaseSecretCleanupService databaseSecretCleanupService, AWSMigrationStackCleanupService migrationStackCleanupService, MigrationBucketCleanupService bucketCleanupService) {
        return new AWSCleanupTaskFactory(databaseSecretCleanupService, migrationStackCleanupService, bucketCleanupService);
    }

    @Bean
    @Primary
    public MigrationInfrastructureCleanupService awsMigrationInfrastructureCleanupService(AWSCleanupTaskFactory cleanupTaskFactory) {
        return new AWSMigrationInfrastructureCleanupService(cleanupTaskFactory);
    }

    @Bean
    public QuickstartWithVPCMigrationStackInputGatheringStrategy withVPCMigrationStackInputGatheringStrategy(CfnApi cfnApi, QuickstartStandaloneMigrationStackInputGatheringStrategy standaloneStrategy) {
        return new QuickstartWithVPCMigrationStackInputGatheringStrategy(cfnApi, standaloneStrategy);
    }

    @Bean
    public QuickstartStandaloneMigrationStackInputGatheringStrategy standaloneMigrationStackInputGatheringStrategy(CfnApi cfnApi) {
        return new QuickstartStandaloneMigrationStackInputGatheringStrategy(cfnApi);
    }

    @Bean
    public MigrationStackInputGatheringStrategyFactory migrationStackInputGatheringStrategyFactory(
            QuickstartWithVPCMigrationStackInputGatheringStrategy withVpcStrategy,
            QuickstartStandaloneMigrationStackInputGatheringStrategy standaloneStrategy) {
        return new MigrationStackInputGatheringStrategyFactory(withVpcStrategy, standaloneStrategy);
    }
}
