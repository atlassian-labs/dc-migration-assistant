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

package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.cloud.AWSConfigurationService;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("aws/configure")
public class AWSConfigureEndpoint {

    private final AWSConfigurationService awsConfigurationService;

    public AWSConfigureEndpoint(AWSConfigurationService awsConfigurationService) {
        this.awsConfigurationService = awsConfigurationService;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response storeAWSCredentials(AWSConfigureWebObject configure) {
        try {
            awsConfigurationService.configureCloudProvider(configure.accessKeyId, configure.secretAccessKey, configure.region);
        } catch (InvalidMigrationStageError invalidMigrationStageError) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(invalidMigrationStageError.getMessage())
                    .build();
        }

        return Response
                .noContent()
                .build();
    }

    @JsonAutoDetect
    static class AWSConfigureWebObject {

        private String accessKeyId;
        private String secretAccessKey;
        private String region;

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public String getSecretAccessKey() {
            return secretAccessKey;
        }

        public String getRegion() {
            return region;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }
}
