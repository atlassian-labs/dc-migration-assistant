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

import com.atlassian.migration.datacenter.api.db.DbMigrationStatus
import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService
import com.atlassian.migration.datacenter.core.aws.db.restore.SsmPsqlDatabaseRestoreService
import com.atlassian.migration.datacenter.core.fs.captor.FinalFileSyncStatus
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncService
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.util.Optional
import javax.ws.rs.core.Response
import kotlin.test.assertEquals


@ExtendWith(MockKExtension::class)
internal class FinalSyncEndpointTest {

    companion object {
        val mapper = ObjectMapper()
                .registerKotlinModule()
                .registerModule(SimpleModule().addDeserializer(Duration::class.java, DurationDeserializer(null)))
    }

    @MockK
    lateinit var databaseMigrationService: DatabaseMigrationService
    @MockK
    lateinit var migrationService: MigrationService
    @MockK
    lateinit var ssmPsqlDatabaseRestoreService: SsmPsqlDatabaseRestoreService
    @MockK
    lateinit var s3FinalSyncService: S3FinalSyncService
    @MockK
    lateinit var migrationContext: MigrationContext
    @InjectMockKs
    lateinit var sut: FinalSyncEndpoint

    class DurationDeserializer(t: Class<Duration>?) : StdDeserializer<Duration>(t) {
        override fun deserialize(jp: JsonParser, ctx: DeserializationContext): Duration {
            val node = jp.codec.readTree<JsonNode>(jp)
            val duration = (node.get("seconds") as IntNode).numberValue() as Int

            return Duration.ofSeconds(duration.toLong())
        }
    }

    @Test
    fun shouldReportDbSyncStatus() {
        every { databaseMigrationService.elapsedTime } returns Optional.of(Duration.ofSeconds(20))
        every { migrationService.currentStage } returns MigrationStage.DATA_MIGRATION_IMPORT
        every { migrationService.currentContext } returns migrationContext
        every { migrationContext.getErrorMessage() } returns ""
        every { s3FinalSyncService.getFinalSyncStatus() } returns FinalFileSyncStatus(0, 0, 0)

        val resp = sut.getMigrationStatus()
        val json = resp.entity as String

        val result = mapper.readValue<FinalSyncEndpoint.FinalSyncStatus>(json)

        assertEquals(20, result.db.elapsedTime.seconds)
        assertEquals(DbMigrationStatus.IMPORTING, result.db.status)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun shouldReportFsSyncStatus() {
        every { databaseMigrationService.elapsedTime } returns Optional.of(Duration.ofSeconds(0))
        every { migrationService.currentStage } returns MigrationStage.DATA_MIGRATION_IMPORT
        every { migrationService.currentContext } returns migrationContext
        every { migrationContext.getErrorMessage() } returns ""
        every { s3FinalSyncService.getFinalSyncStatus() } returns FinalFileSyncStatus(150, 50, 12)

        val resp = sut.getMigrationStatus()
        val json = resp.entity as String

        val result = mapper.readValue<FinalSyncEndpoint.FinalSyncStatus>(json)
        assertEquals(150, result.fs.uploaded)
        assertEquals(88, result.fs.downloaded)
        assertEquals(12, result.fs.failed)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun shouldReportDbSyncStatusWithErrorWhenDbErrorEncountered() {
        every { databaseMigrationService.elapsedTime } returns Optional.of(Duration.ofSeconds(20))
        every { migrationService.currentStage } returns MigrationStage.DATA_MIGRATION_IMPORT
        every { migrationService.currentContext } returns migrationContext
        every { migrationContext.getErrorMessage() } returns "could not connect to server"
        every { s3FinalSyncService.getFinalSyncStatus() } returns FinalFileSyncStatus(0, 0, 0)

        val resp = sut.getMigrationStatus()
        val json = resp.entity as String

        val result = mapper.readValue<FinalSyncEndpoint.FinalSyncStatus>(json)

        assertEquals(20, result.db.elapsedTime.seconds)
        assertEquals(DbMigrationStatus.IMPORTING, result.db.status)
        assertEquals("could not connect to server", result.errorMessage)
    }

    @Test
    fun shouldRestartFsSync() {
        givenFinalSyncHasFailed()
        andFsRestartWillSucceed()
        val res = sut.retryFsSync()
        assertResponseStatusIs(Response.Status.ACCEPTED, res)

        verify { s3FinalSyncService.scheduleSync() }
    }

    @Test
    fun shouldTransitionToFinalSyncWaitWhenRestartingFSSync() {
        givenFinalSyncHasFailed()
        andFsRestartWillSucceed()
        sut.retryFsSync()

        verify { migrationService.transition(MigrationStage.FINAL_SYNC_WAIT) }
    }

    @Test
    fun shouldNotStartFsSyncWhenMigrationIsNotError() {
        givenMigrationHasSucceeded()

        val res = sut.retryFsSync()
        assertResponseStatusIs(Response.Status.BAD_REQUEST, res)
        verify(exactly = 0) { s3FinalSyncService.scheduleSync() }
    }

    @Test
    fun shouldReturnConflictWhenFsSyncCantBeStarted() {
        givenFinalSyncHasFailed()
        andFsRestartWillFail()

        val res = sut.retryFsSync()
        assertResponseStatusIs(Response.Status.CONFLICT, res)
    }

    @Test
    fun shouldRestartDbMigration() {
        givenFinalSyncHasFailed()
        andDbRestartWillSucceed()

        val res = sut.retryDbMigration()

        assertResponseStatusIs(Response.Status.ACCEPTED, res)
        verify { databaseMigrationService.scheduleMigration() }
    }

    @Test
    fun shouldTransitionToDbMigrationExportWhenRestartingDbMigration() {
        givenFinalSyncHasFailed()
        andDbRestartWillSucceed()

        sut.retryDbMigration()
        verify { migrationService.transition(MigrationStage.DB_MIGRATION_EXPORT) }
    }

    @Test
    fun shouldNotStartDbMigrationWhenMigrationIsNotError() {
        givenMigrationHasSucceeded()

        val res = sut.retryDbMigration()
        assertResponseStatusIs(Response.Status.BAD_REQUEST, res)
        verify(exactly = 0) { databaseMigrationService.scheduleMigration() }
    }

    @Test
    fun shouldReturnConflictWhenDbMigrationCantBeRestarted() {
        givenFinalSyncHasFailed()
        andDbRestartWillFail()

        val res = sut.retryDbMigration()
        assertResponseStatusIs(Response.Status.CONFLICT, res)
    }

    private fun givenMigrationHasSucceeded() {
        every { migrationService.currentStage } returns MigrationStage.VALIDATE
    }

    private fun givenFinalSyncHasFailed() {
        every { migrationService.currentStage } returns MigrationStage.FINAL_SYNC_ERROR
        justRun { migrationService.transition(MigrationStage.FINAL_SYNC_WAIT) }
        justRun { migrationService.transition(MigrationStage.DB_MIGRATION_EXPORT) }
    }

    private fun andFsRestartWillSucceed() {
        every { s3FinalSyncService.scheduleSync() } returns true
    }

    private fun andFsRestartWillFail() {
        every { s3FinalSyncService.scheduleSync() } returns false
    }

    private fun andDbRestartWillSucceed() {
        every { databaseMigrationService.scheduleMigration() } returns true
    }

    private fun andDbRestartWillFail() {
        every { databaseMigrationService.scheduleMigration() } returns false
    }

    private fun assertResponseStatusIs(status: Response.Status, res: Response) {
        assertEquals(status.statusCode, res.status)
    }

}
