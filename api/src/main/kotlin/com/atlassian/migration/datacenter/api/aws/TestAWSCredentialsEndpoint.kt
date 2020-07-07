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
package com.atlassian.migration.datacenter.api.aws

import com.atlassian.migration.datacenter.core.aws.auth.ProbeAWSAuth
import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService
import com.atlassian.sal.api.websudo.WebSudoRequired
import org.springframework.beans.factory.annotation.Autowired
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("aws/credentials")
@WebSudoRequired
class TestAWSCredentialsEndpoint @Autowired constructor(
    private val writeCredentialsService: WriteCredentialsService,
    private val probe: ProbeAWSAuth
) {
    @POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    fun testCredentialsSDKV2(): Response {
        return try {
            Response.ok(probe.probeSDKV2()).build()
        } catch (cfne: CloudFormationException) {
            if (cfne.statusCode() in arrayOf(401, 403)) {
                return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(cfne.message)
                    .build()
            }
            throw cfne
        }
    }
}