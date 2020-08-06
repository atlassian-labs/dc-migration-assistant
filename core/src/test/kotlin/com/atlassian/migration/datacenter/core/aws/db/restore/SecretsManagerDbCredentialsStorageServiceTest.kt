package com.atlassian.migration.datacenter.core.aws.db.restore

import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretResponse
import java.util.function.Supplier
import kotlin.test.assertEquals


@ExtendWith(MockKExtension::class)
class SecretsManagerDbCredentialsStorageServiceTest {

    @MockK
    lateinit var secretsManagerClient: SecretsManagerClient

    @MockK
    lateinit var migrationService: MigrationService

    @MockK
    lateinit var migrationContext: MigrationContext

    lateinit var credentialsStoreService: SecretsManagerDbCredentialsStorageService

    @BeforeEach
    internal fun setUp() {
        every { migrationService.currentContext } returns migrationContext
        every { migrationContext.startEpoch } returns (System.currentTimeMillis() / 1000L - 1000L)

        credentialsStoreService = SecretsManagerDbCredentialsStorageService(Supplier { secretsManagerClient }, migrationService)
    }

    @Test
    internal fun shouldGetSecretNameWhenNameIsNotStoredInDatabase() {
        every { migrationContext.dbSecretName } returns "atl-migration-app-rds-password-54321"

        val secretName = credentialsStoreService.secretName
        assertEquals("atl-migration-app-rds-password-54321", secretName)
    }

    @Test
    internal fun shouldGetSecretNameWhenNameIsStoredInDatabase() {
        every { migrationContext.dbSecretName } returns ""
        every { migrationContext.applicationDeploymentId } returns "foo"
        val secretName = credentialsStoreService.secretName

        assertEquals("atl-foo-migration-app-rds-password", secretName)
    }

    @Test
    internal fun shouldStoreCredentialsInSecretsService() {
        val createSecretResponse = CreateSecretResponse.builder().sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build()).build() as CreateSecretResponse

        val secretCaptureSlot = slot<CreateSecretRequest>()

        every { migrationContext.applicationDeploymentId } returns "foo"
        every { secretsManagerClient.createSecret(capture(secretCaptureSlot)) } returns createSecretResponse
        every { migrationContext.dbSecretName = any() } answers {}
        every { migrationContext.save() } answers {}

        credentialsStoreService.storeCredentials("foobar42")

        val createSecretRequest = secretCaptureSlot.captured
        assertEquals("foobar42", createSecretRequest.secretString())
        MatcherAssert.assertThat(createSecretRequest.name(), Matchers.matchesPattern("atl-foo-migration-app-rds-password-\\d{4,}"))

        assertEquals(true, createSecretRequest.name().startsWith("atl-foo-migration-app-rds-password-"))

        val capturedDbSecretName = slot<String>()
        verify { migrationContext.dbSecretName = capture(capturedDbSecretName) }
        MatcherAssert.assertThat(capturedDbSecretName.captured, Matchers.matchesPattern("atl-foo-migration-app-rds-password-\\d{4,}"))
    }

    @Test
    internal fun shouldRaiseErrorWhenSecretCannotBePersistedInSecretsService() {
        every { migrationContext.applicationDeploymentId } returns "foo"
        every { secretsManagerClient.createSecret(any<CreateSecretRequest>()) } returns CreateSecretResponse.builder().sdkHttpResponse(SdkHttpResponse.builder().statusCode(503).build()).build() as CreateSecretResponse

        assertThrows<DatabaseMigrationFailure> { credentialsStoreService.storeCredentials("foobar42") }
        verify(exactly = 0) { migrationContext.dbSecretName = any() }
    }
}