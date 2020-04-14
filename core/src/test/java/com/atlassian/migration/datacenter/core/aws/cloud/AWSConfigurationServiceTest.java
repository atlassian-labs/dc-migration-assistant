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

package com.atlassian.migration.datacenter.core.aws.cloud;

import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidCredentialsException;
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AWSConfigurationServiceTest {

    @Mock
    WriteCredentialsService mockCredentialsWriter;

    @Mock
    RegionService mockRegionService;

    @Mock
    MigrationService mockMigrationService;

    AWSConfigurationService sut;

    @BeforeEach
    void setUp() {
        sut = new AWSConfigurationService(mockCredentialsWriter, mockRegionService, mockMigrationService, (accessKeyId, secretAccessKey) -> true);
    }

    @Test
    void shouldStoreCredentials() throws InvalidMigrationStageError, InvalidCredentialsException {
        mockValidMigration();

        final String username = "username";
        final String password = "password";
        sut.configureCloudProvider(username, password, "garbage");

        verify(mockCredentialsWriter).storeAccessKeyId(username);
        verify(mockCredentialsWriter).storeSecretAccessKey(password);
    }

    @Test
    void shouldStoreRegion() throws InvalidAWSRegionException, InvalidMigrationStageError, InvalidCredentialsException {
        mockValidMigration();

        final String region = "region";
        sut.configureCloudProvider("username", "password", region);

        verify(mockRegionService).storeRegion(region);
    }

    @Test
    void shouldStoreCredentialsOnlyWhenStateIsAuthentication() {
        when(mockMigrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_COPY_WAIT);
        assertThrows(InvalidMigrationStageError.class, () -> sut.configureCloudProvider("garbage", "garbage", "garbage"));
    }

    @Test
    void shouldRaiseAnErrorWhenCredentialsAreInvalid() {
        mockValidMigration();
        sut = new AWSConfigurationService(mockCredentialsWriter, mockRegionService, mockMigrationService, (accessKeyId, secretAccessKey) -> false);
        assertThrows(InvalidCredentialsException.class, () -> sut.configureCloudProvider("garbage", "garbage", "garbage"));
    }

    private void mockValidMigration() {
        when(mockMigrationService.getCurrentStage()).thenReturn(MigrationStage.AUTHENTICATION);
    }

    @Test
    void shouldTransitionToProvisionApplicationStageWhenSuccessful() throws InvalidMigrationStageError, InvalidCredentialsException {
        mockValidMigration();

        sut.configureCloudProvider("garbage", "garbage", "garbage");

        verify(mockMigrationService).transition(MigrationStage.PROVISION_APPLICATION);
    }

    @Test
    void shouldThrowErrorAndNotTransitionWhenUnableToCompleteSuccessfully() throws InvalidAWSRegionException, InvalidMigrationStageError {
        mockValidMigration();

        final String testRegion = "region";
        doThrow(new InvalidAWSRegionException()).when(mockRegionService).storeRegion(testRegion);

        try {
            sut.configureCloudProvider("garbage", "garbage", testRegion);
            fail();
        } catch (Exception ex) {
            assertEquals(InvalidAWSRegionException.class, ex.getCause().getClass());
            verify(mockMigrationService, never()).transition(any());
        }
    }
}