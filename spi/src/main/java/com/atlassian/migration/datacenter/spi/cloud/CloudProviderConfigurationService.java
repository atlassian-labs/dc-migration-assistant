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

package com.atlassian.migration.datacenter.spi.cloud;


import com.atlassian.migration.datacenter.spi.exceptions.InvalidCredentialsException;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;

public interface CloudProviderConfigurationService {

    /**
     * Configures access to a cloud provider to facilitate the migration
     *
     * @param entity    an identifier for the entity accessing the cloud provider e.g. AWS access key ID
     * @param secret    the secret to authenticate the entity to the cloud provider e.g. AWS secret access key
     * @param geography the geography to use for deployment of resources e.g. AWS region
     */
    void configureCloudProvider(String entity, String secret, String geography) throws InvalidMigrationStageError, InvalidCredentialsException;

}
