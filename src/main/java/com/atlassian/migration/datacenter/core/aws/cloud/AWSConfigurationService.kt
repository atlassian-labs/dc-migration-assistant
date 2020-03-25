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
package com.atlassian.migration.datacenter.core.aws.cloud

import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException
import com.atlassian.migration.datacenter.core.aws.region.RegionService
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.cloud.CloudProviderConfigurationService
import org.slf4j.LoggerFactory

class AWSConfigurationService(private val writeCredentialsService: WriteCredentialsService, private val regionService: RegionService, private val migrationService: MigrationService) : CloudProviderConfigurationService {
    /**
     * Configures the app to be able to authenticate with AWS.
     *
     * @param entity    the AWS access key ID
     * @param secret    the AWS secret access key
     * @param geography the AWS region
     * @throws InvalidMigrationStageError when not in [MigrationStage.AUTHENTICATION]
     */
    @Throws(InvalidMigrationStageError::class)
    override fun configureCloudProvider(entity: String, secret: String, geography: String) {
        val currentStage = migrationService.currentStage
        if (currentStage != MigrationStage.AUTHENTICATION) {
            logger.error("tried to configure AWS when in invalid stage {}", currentStage)
            throw InvalidMigrationStageError("expected to be in stage " + MigrationStage.AUTHENTICATION + " but was in " + currentStage)
        }
        try {
            regionService.storeRegion(geography)
            logger.info("stored aws region")
        } catch (e: InvalidAWSRegionException) {
            logger.error("error storing AWS region", e)
            throw RuntimeException(e)
        }
        logger.info("Storing AWS credentials")
        writeCredentialsService.storeAccessKeyId(entity)
        writeCredentialsService.storeSecretAccessKey(secret)
        migrationService.transition(MigrationStage.AUTHENTICATION, MigrationStage.PROVISION_APPLICATION)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AWSConfigurationService::class.java)
    }

}