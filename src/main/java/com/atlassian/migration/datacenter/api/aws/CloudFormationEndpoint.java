package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST API Endpoint for managing AWS provisioning.
 */
@Path("/aws/stack")
public class CloudFormationEndpoint {

    private final ApplicationDeploymentService deploymentService;
    private static final Logger log = LoggerFactory.getLogger(CloudFormationEndpoint.class);

    public CloudFormationEndpoint(ApplicationDeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response provisionInfrastructure(ProvisioningConfig provisioningConfig) {
        try {
            String stackName = provisioningConfig.getStackName();
            this.deploymentService.deployApplication(stackName, provisioningConfig.getParams());
            //Should be updated to URI location after get stack details Endpoint is built
            return Response.status(Response.Status.ACCEPTED).entity(stackName).build();
        } catch (InvalidMigrationStageError e) {
            log.error("Migration stage is not valid.", e);
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(ImmutableMap.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfrastructureStatus() {
        try {
            ApplicationDeploymentService.ApplicationDeploymentStatus status = deploymentService.getDeploymentStatus();
            return Response.ok(ImmutableMap.of("status", status)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(ImmutableMap.of("error", e.getMessage())).build();
        }
    }
}
