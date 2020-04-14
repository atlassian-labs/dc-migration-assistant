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

package com.atlassian.migration.datacenter.core.aws.cloud;

import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.cloud.CloudProviderConfigurationService;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidCredentialsException;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWSConfigurationService implements CloudProviderConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(AWSConfigurationService.class);

    private final WriteCredentialsService writeCredentialsService;
    private final RegionService regionService;
    private final MigrationService migrationService;
    private CloudCredentialsValidator cloudCredentialsValidator;

    public AWSConfigurationService(WriteCredentialsService writeCredentialsService, RegionService regionService, MigrationService migrationService, CloudCredentialsValidator cloudCredentialsValidator) {
        this.writeCredentialsService = writeCredentialsService;
        this.regionService = regionService;
        this.migrationService = migrationService;
        this.cloudCredentialsValidator = cloudCredentialsValidator;
    }

    /**
     * Configures the app to be able to authenticate with AWS.
     *
     * @param entity    the AWS access key ID
     * @param secret    the AWS secret access key
     * @param geography the AWS region
     * @throws InvalidMigrationStageError when not in {@link MigrationStage#AUTHENTICATION}
     */
    @Override
    public void configureCloudProvider(String entity, String secret, String geography) throws InvalidMigrationStageError, InvalidCredentialsException {
        final MigrationStage currentStage = migrationService.getCurrentStage();
        if (!currentStage.equals(MigrationStage.AUTHENTICATION)) {
            logger.error("tried to configure AWS when in invalid stage {}", currentStage);
            throw new InvalidMigrationStageError("expected to be in stage " + MigrationStage.AUTHENTICATION + " but was in " + currentStage);
        }

        if (!this.cloudCredentialsValidator.validate(entity, secret)) {
            throw new InvalidCredentialsException("At least one of supplied access key and secret key are invalid");
        }

        try {
            regionService.storeRegion(geography);
            logger.info("stored aws region");
        } catch (InvalidAWSRegionException e) {
            logger.error("error storing AWS region", e);
            throw new RuntimeException(e);
        }

        logger.info("Storing AWS credentials");
        writeCredentialsService.storeAccessKeyId(entity);
        writeCredentialsService.storeSecretAccessKey(secret);

        migrationService.transition(MigrationStage.PROVISION_APPLICATION);
    }
}
