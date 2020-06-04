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
import com.atlassian.migration.datacenter.core.aws.db.restore.TargetDbCredentialsStorageService;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentError;
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudformation.model.Stack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuickstartDeploymentService extends CloudformationDeploymentService implements ApplicationDeploymentService {

    static final String SERVICE_URL_STACK_OUTPUT_KEY = "ServiceURL";
    static final String DATABASE_ENDPOINT_ADDRESS_STACK_OUTPUT_KEY = "DBEndpointAddress";
    static final String SECURITY_GROUP_NAME_STACK_OUTPUT_KEY = "SGname";

    private final Logger logger = LoggerFactory.getLogger(QuickstartDeploymentService.class);
    private static final String TREBUCHET_BUCKET = "https://trebuchet-public-resources.s3.amazonaws.com/";
    private static final String QUICKSTART_WITH_VPC_TEMPLATE_URL = TREBUCHET_BUCKET + "quickstart-jira-dc-with-vpc.template.yaml";
    private static final String QUICKSTART_STANDALONE_TEMPLATE_URL = TREBUCHET_BUCKET + "quickstart-jira-dc.template.yaml";

    private static final String withVpcTemplateUrl = System.getProperty("quickstart.template.withVpc.url", QUICKSTART_WITH_VPC_TEMPLATE_URL);
    private static final String standaloneTemplateUrl = System.getProperty("quickstart.template.standalone.url", QUICKSTART_STANDALONE_TEMPLATE_URL);

    private final MigrationService migrationService;
    private final TargetDbCredentialsStorageService dbCredentialsStorageService;
    private final AWSMigrationHelperDeploymentService migrationHelperDeploymentService;
    private final CfnApi cfnApi;

    public QuickstartDeploymentService(CfnApi cfnApi, MigrationService migrationService, TargetDbCredentialsStorageService dbCredentialsStorageService, AWSMigrationHelperDeploymentService migrationHelperDeploymentService) {
        super(cfnApi);

        this.cfnApi = cfnApi;
        this.migrationService = migrationService;
        this.dbCredentialsStorageService = dbCredentialsStorageService;
        this.migrationHelperDeploymentService = migrationHelperDeploymentService;
    }

    /**
     * Commences the deployment of the AWS Quick Start. It will transition the state machine upon completion of the
     * deployment. If the deployment finishes successfully we transition to the next stage, otherwise we transition
     * to an error. The migration will also transition to an error if the deployment takes longer than an hour.
     *
     * @param deploymentId the stack name
     * @param params       the parameters for the cloudformation template. The key should be the parameter name and the value
     *                     should be the parameter value.
     */
    @Override
    public void deployApplication(@NotNull String deploymentId, @NotNull Map<String, String> params) throws InvalidMigrationStageError {
        logger.info("received request to deploy application");
        deployQuickstart(deploymentId, standaloneTemplateUrl, params);
    }

    @Override
    public void deployApplicationWithNetwork(@NotNull String deploymentId, @NotNull Map<String, String> params) throws InvalidMigrationStageError {
        logger.info("received request to deploy application and virtual network");

        deployQuickstart(deploymentId, withVpcTemplateUrl, params);
    }

    private void deployQuickstart(@NotNull String deploymentId, String templateUrl, @NotNull Map<String, String> params) throws InvalidMigrationStageError {
        migrationService.transition(MigrationStage.PROVISION_APPLICATION_WAIT);

        logger.info("deploying application stack");
        super.deployCloudformationStack(templateUrl, deploymentId, params);

        addDeploymentIdToMigrationContext(deploymentId);

        storeDbCredentials(params);
    }

    @Override
    protected void handleFailedDeployment(String error) {
        logger.error("application stack deployment failed");
        migrationService.error(error);
    }

    @Override
    protected void handleSuccessfulDeployment() {
        try {
            logger.debug("application stack deployment succeeded");
            migrationService.transition(MigrationStage.PROVISION_MIGRATION_STACK);

            final String applicationStackName = migrationService.getCurrentContext().getApplicationDeploymentId();
            Optional<Stack> maybeStack = cfnApi.getStack(applicationStackName);
            if (!maybeStack.isPresent()) {
                throw new InfrastructureDeploymentError("could not get details of application stack after deploying it");
            }

            Stack applicationStack = maybeStack.get();
            Map<String, String> applicationStackOutputsMap = new HashMap<>();
            applicationStack.outputs().forEach(output -> applicationStackOutputsMap.put(output.outputKey(), output.outputValue()));

            storeServiceURLInContext(applicationStackOutputsMap.get(SERVICE_URL_STACK_OUTPUT_KEY));

            Map<String, String> migrationStackParams =
                    new QuickstartWithVPCMigrationStackInputGatheringStrategy(cfnApi)
                            .gatherMigrationStackInputsFromApplicationStack(applicationStack);

            migrationHelperDeploymentService.deployMigrationInfrastructure(migrationStackParams);

        } catch (InvalidMigrationStageError invalidMigrationStageError) {
            logger.error("tried to transition migration from {} but got error: {}.", MigrationStage.PROVISION_APPLICATION_WAIT, invalidMigrationStageError.getMessage());
            migrationService.error(invalidMigrationStageError.getMessage());
        }
    }

    private void storeServiceURLInContext(String serviceUrl) {
        logger.info("Storing service URL in migration context");

        MigrationContext context = migrationService.getCurrentContext();
        context.setServiceUrl(serviceUrl);
        context.save();
    }

    private void storeDbCredentials(Map<String, String> params) {
        dbCredentialsStorageService.storeCredentials(params.get("DBPassword"));
    }

    private void addDeploymentIdToMigrationContext(String deploymentId) {
        logger.info("Storing stack name in migration context");

        MigrationContext context = migrationService.getCurrentContext();
        context.setApplicationDeploymentId(deploymentId);
        context.save();
    }

    @Override
    public InfrastructureDeploymentStatus getDeploymentStatus() {
        return super.getDeploymentStatus(migrationService.getCurrentContext().getApplicationDeploymentId());
    }
}
