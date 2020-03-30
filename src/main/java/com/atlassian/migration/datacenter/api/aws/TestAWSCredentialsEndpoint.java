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

import com.atlassian.migration.datacenter.core.aws.auth.ProbeAWSAuth;
import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("aws/credentials")
public class TestAWSCredentialsEndpoint {

    private final WriteCredentialsService writeCredentialsService;
    private final ProbeAWSAuth probe;

    @Autowired
    public TestAWSCredentialsEndpoint(WriteCredentialsService writeCredentialsService, ProbeAWSAuth probe) {
        this.writeCredentialsService = writeCredentialsService;
        this.probe = probe;
    }

    @POST
    @Path("/test")
    @Produces(APPLICATION_JSON)
    public Response testCredentialsSDKV2() {
        try {
            return Response.ok(probe.probeSDKV2()).build();
        } catch (CloudFormationException cfne) {
            if (cfne.statusCode() == 401 || cfne.statusCode() == 403) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(cfne.getMessage())
                        .build();
            }
            throw cfne;
        }
    }
}
