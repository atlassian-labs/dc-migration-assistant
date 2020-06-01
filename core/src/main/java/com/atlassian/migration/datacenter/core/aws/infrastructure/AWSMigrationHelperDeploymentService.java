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

package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus;
import com.atlassian.migration.datacenter.spi.infrastructure.MigrationInfrastructureDeploymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingGroup;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsResponse;
import software.amazon.awssdk.services.autoscaling.model.Instance;
import software.amazon.awssdk.services.cloudformation.model.Output;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackResource;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages the deployment of the migration helper stack which is used to hydrate the new
 * application deployment with data.
 */
public class AWSMigrationHelperDeploymentService extends CloudformationDeploymentService implements MigrationInfrastructureDeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(AWSMigrationHelperDeploymentService.class);
    private static final String MIGRATION_HELPER_TEMPLATE_URL = "https://trebuchet-public-resources.s3.amazonaws.com/migration-helper.yml";
    static final String STACK_RESOURCE_QUEUE_NAME = "MigrationQueue";
    static final String STACK_RESOURCE_DEAD_LETTER_QUEUE_NAME = "DeadLetterQueue";

    private static final String templateUrl = System.getProperty("migration_helper.template.url", MIGRATION_HELPER_TEMPLATE_URL);

    private final Supplier<AutoScalingClient> autoScalingClientFactory;
    private final MigrationService migrationService;
    private final CfnApi cfnApi;


    //    TODO: CHET443-Store in context and not in variables
    private String fsRestoreDocument;
    private String fsRestoreStatusDocument;
    private String rdsRestoreDocument;
    private String migrationStackASG;
    private String migrationBucket;
    private String queueName;
    private String deadLetterQueueName;

    public AWSMigrationHelperDeploymentService(CfnApi cfnApi, Supplier<AutoScalingClient> autoScalingClientFactory, MigrationService migrationService) {
        this(cfnApi, autoScalingClientFactory, migrationService, 30);
    }

    AWSMigrationHelperDeploymentService(CfnApi cfnApi, Supplier<AutoScalingClient> autoScalingClientFactory, MigrationService migrationService, int pollIntervalSeconds) {
        super(cfnApi, pollIntervalSeconds);
        this.migrationService = migrationService;
        this.cfnApi = cfnApi;
        this.autoScalingClientFactory = autoScalingClientFactory;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Will name the stack [application-stack-name]-migration
     */
    @Override
    public void deployMigrationInfrastructure(Map<String, String> params) throws InvalidMigrationStageError {
        resetStackOutputs();

        migrationService.assertCurrentStage(MigrationStage.PROVISION_MIGRATION_STACK);

        String migrationStackDeploymentId = constructMigrationStackDeploymentIdentifier();
        super.deployCloudformationStack(templateUrl, migrationStackDeploymentId, params);
        migrationService.transition(MigrationStage.PROVISION_MIGRATION_STACK_WAIT);

        storeMigrationStackDetailsInContext(migrationStackDeploymentId);
    }


    @Override
    protected void handleSuccessfulDeployment() {
        String stackId = System.getProperty("com.atlassian.migration.migrationStack.id", migrationService.getCurrentContext().getHelperStackDeploymentId());
        Optional<Stack> maybeStack = cfnApi.getStack(stackId);
        if (!maybeStack.isPresent()) {
            throw new InfrastructureDeploymentError("stack was not found by DescribeStack even though it succeeded");
        }

        Stack stack = maybeStack.get();

        Map<String, String> stackOutputs = stack
                .outputs()
                .stream()
                .collect(Collectors.toMap(Output::outputKey, Output::outputValue, (a, b) -> b));

        Map<String, StackResource> migrationStackResources = cfnApi.getStackResources(stackId);

        persistStackDetails(stackId, stackOutputs, migrationStackResources);

        try {
            migrationService.transition(MigrationStage.FS_MIGRATION_COPY);
        } catch (InvalidMigrationStageError invalidMigrationStageError) {
            logger.error("error transitioning to FS_MIGRATION_COPY stage after successful migration stack deployment", invalidMigrationStageError);
            migrationService.error(invalidMigrationStageError.getMessage());
        }
    }

    private void persistStackDetails(String stackId, Map<String, String> outputsMap, Map<String, StackResource> resources) {
        fsRestoreDocument = outputsMap.get("DownloadSSMDocument");
        fsRestoreStatusDocument = outputsMap.get("DownloadStatusSSMDocument");
        rdsRestoreDocument = outputsMap.get("RdsRestoreSSMDocument");
        migrationStackASG = outputsMap.get("ServerGroup");
        migrationBucket = outputsMap.get("MigrationBucket");
        queueName = resources.get("MigrationQueue").physicalResourceId();
        deadLetterQueueName = resources.get("DeadLetterQueue").physicalResourceId();
    }

    @Override
    protected void handleFailedDeployment(String error) {
        migrationService.error(error);
    }

    public String getFsRestoreDocument() {
        return getMigrationStackPropertyOrOverride(fsRestoreDocument, "com.atlassian.migration.s3sync.documentName");
    }

    public String getFsRestoreStatusDocument() {
        return getMigrationStackPropertyOrOverride(fsRestoreStatusDocument, "com.atlassian.migration.s3sync.statusDocumentName");
    }

    public String getDbRestoreDocument() {
        return getMigrationStackPropertyOrOverride(rdsRestoreDocument, "com.atlassian.migration.psql.documentName");
    }

    public String getMigrationS3BucketName() {
        return getMigrationStackPropertyOrOverride(migrationBucket, "S3_TARGET_BUCKET_NAME");
    }

    public String getQueueResource() {
        return getMigrationStackPropertyOrOverride(queueName, "com.atlassian.migration.queue.migrationQueueName");
    }

    public String getDeadLetterQueueResource() {
        return getMigrationStackPropertyOrOverride(deadLetterQueueName, "com.atlassian.migration.queue.deadLetterQueueName");
    }

    public String getMigrationHostInstanceId() {
        final String documentOverride = System.getProperty("com.atlassian.migration.instanceId");
        if (documentOverride != null) {
            return documentOverride;
        } else {
            ensureStackOutputsAreSet();

            AutoScalingClient client = autoScalingClientFactory.get();
            DescribeAutoScalingGroupsResponse response = client.describeAutoScalingGroups(
                    DescribeAutoScalingGroupsRequest.builder()
                            .autoScalingGroupNames(migrationStackASG)
                            .build());

            AutoScalingGroup migrationStackGroup = response.autoScalingGroups().get(0);
            Instance migrationInstance = migrationStackGroup.instances().get(0);

            return migrationInstance.instanceId();
        }
    }

    private String getMigrationStackPropertyOrOverride(String migrationStackProperty, String migrationStackPropertySystemOverrideKey) {
        final String documentOverride = System.getProperty(migrationStackPropertySystemOverrideKey);

        if (documentOverride != null) {
            return documentOverride;
        }

        ensureStackOutputsAreSet();
        return migrationStackProperty;
    }

    private void ensureStackOutputsAreSet() {
        final Stream<String> stackOutputs = Stream.of(
                fsRestoreDocument,
                fsRestoreStatusDocument,
                rdsRestoreDocument,
                migrationBucket,
                migrationStackASG,
                queueName,
                deadLetterQueueName);
        if (stackOutputs.anyMatch(output -> output == null || output.equals(""))) {
            throw new InfrastructureDeploymentError("migration stack outputs are not set");
        }
    }

    @Override
    public InfrastructureDeploymentStatus getDeploymentStatus() {
        return super.getDeploymentStatus(migrationService.getCurrentContext().getHelperStackDeploymentId());
    }

    private String constructMigrationStackDeploymentIdentifier() {
        return String.format("%s-migration", migrationService.getCurrentContext().getApplicationDeploymentId());
    }

    protected void storeMigrationStackDetailsInContext(String migrationStackDeploymentId) {
        final MigrationContext context = migrationService.getCurrentContext();
        context.setHelperStackDeploymentId(migrationStackDeploymentId);
        context.save();
    }

    //TODO: CHET443-Store in context and no longer needed to do this?
    private void resetStackOutputs() {
        fsRestoreDocument = "";
        fsRestoreStatusDocument = "";
        rdsRestoreDocument = "";
        migrationStackASG = "";
        migrationBucket = "";
        queueName = "";
        deadLetterQueueName = "";
    }
}
