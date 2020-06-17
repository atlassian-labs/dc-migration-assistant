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
package com.atlassian.migration.datacenter.api

import com.atlassian.migration.datacenter.api.db.DatabaseMigrationStatus
import com.atlassian.migration.datacenter.api.db.stageToStatus
import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService
import com.atlassian.migration.datacenter.core.aws.db.restore.SsmPsqlDatabaseRestoreService
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncService
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.collect.ImmutableMap
import java.time.Duration
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/migration/final-sync")
class FinalSyncEndpoint(
        private val databaseMigrationService: DatabaseMigrationService,
        private val migrationService: MigrationService,
        private val ssmPsqlDatabaseRestoreService: SsmPsqlDatabaseRestoreService,
        private val finalSyncService: S3FinalSyncService
) {
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    init {
        mapper.setVisibility(
                PropertyAccessor.ALL,
                JsonAutoDetect.Visibility.ANY
        )
    }

    data class FSSyncStatus(val uploaded: Int, val downloaded: Int, val hasProgressedToNextStage: Boolean)
    data class FinalSyncStatus(val db: DatabaseMigrationStatus, val fs: FSSyncStatus)

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start")
    fun runMigration(): Response {
        if (migrationService.currentStage.isDBPhase) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(mapOf("status" to migrationService.currentStage))
                    .build()
        }
        val started = databaseMigrationService.scheduleMigration()
        val builder = if (started) {
            val fsSyncStarted = finalSyncService.scheduleSync()
            if (fsSyncStarted) Response.status(Response.Status.ACCEPTED) else {
                databaseMigrationService.abortMigration()
                Response.status(
                        Response.Status.CONFLICT
                )
            }
        } else {
            Response.status(
                    Response.Status.CONFLICT
            )
        }
        return builder
                .entity(ImmutableMap.of("migrationScheduled", started))
                .build()
    }

    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    fun getMigrationStatus(): Response {
        val elapsed = databaseMigrationService.elapsedTime
                .orElse(Duration.ZERO)
        val currentStage = migrationService.currentStage
        val db = DatabaseMigrationStatus(
                stageToStatus(currentStage),
                elapsed
        )
        val isCurrentStageAfterFinalSync = currentStage.isAfter(MigrationStage.FINAL_SYNC_WAIT)
        val fsSyncStatus = finalSyncService.getFinalSyncStatus()

        val fs = FSSyncStatus(fsSyncStatus.uploadedFileCount, fsSyncStatus.uploadedFileCount - fsSyncStatus.enqueuedFileCount, isCurrentStageAfterFinalSync)
        val status = FinalSyncStatus(db, fs)

        return try {
            Response
                    .ok(mapper.writeValueAsString(status))
                    .build()
        } catch (e: JsonProcessingException) {
            Response
                    .serverError()
                    .entity("Unable to get sync status. Please contact support and show them this error: ${e.message}")
                    .build()
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/abort")
    fun abortMigration(): Response {
        return try {
            databaseMigrationService.abortMigration()
            Response
                    .ok(mapOf("cancelled" to true))
                    .build()
        } catch (e: InvalidMigrationStageError) {
            Response
                    .status(Response.Status.CONFLICT)
                    .entity(mapOf("error" to "sync is not in progress"))
                    .build()
        } finally {
            finalSyncService.abortMigration()
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/db-logs")
    fun getCommandOutputs(): Response {
        return try {
            Response.ok(ssmPsqlDatabaseRestoreService.fetchCommandResult()).build()
        } catch (e: SsmPsqlDatabaseRestoreService.SsmCommandNotInitialisedException) {
            return Response.status(Response.Status.CONFLICT).entity(mapOf("error" to "SSM command wasn't executed"))
                    .build()
        }
    }
}