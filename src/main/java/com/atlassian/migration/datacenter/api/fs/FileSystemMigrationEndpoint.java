package com.atlassian.migration.datacenter.api.fs;

import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/migration/fs")
public class FileSystemMigrationEndpoint {

    private final FilesystemMigrationService fsMigrationService;

    private final ObjectMapper mapper;

    public FileSystemMigrationEndpoint(FilesystemMigrationService fsMigrationService) {
        this.fsMigrationService = fsMigrationService;
        this.mapper = new ObjectMapper();
        this.mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start")
    public Response runFileMigration() {
        if (fsMigrationService.isRunning()) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(ImmutableMap.of("status", fsMigrationService.getReport().getStatus()))
                    .build();
        }
        boolean started = fsMigrationService.scheduleMigration();
        Response.ResponseBuilder builder = started ? Response.status(Response.Status.ACCEPTED) : Response.status(Response.Status.CONFLICT);

        return builder
                .entity(ImmutableMap.of("migrationScheduled", started))
                .build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("/report")
    public Response getFilesystemMigrationStatus() {
        FileSystemMigrationReport report = fsMigrationService.getReport();
        if (report == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", "no file system migration exists"))
                    .build();
        }

        try {
            return Response
                    .ok(mapper.writeValueAsString(report))
                    .build();
        } catch (JsonProcessingException e) {
            return Response
                    .serverError()
                    .entity(String.format("Unable to get file system status. Please contact support and show them this error: %s", e.getMessage()))
                    .build();
        }
    }
}
