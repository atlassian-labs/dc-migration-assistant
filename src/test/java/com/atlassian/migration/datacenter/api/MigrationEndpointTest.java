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

package com.atlassian.migration.datacenter.api;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.exceptions.MigrationAlreadyExistsException;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MigrationEndpointTest {

    @Mock
    private MigrationService migrationService;

    @InjectMocks
    private MigrationEndpoint sut;

    @Test
    public void testOKAndMigrationStatusWhenMigrationExists() {
        when(migrationService.getCurrentStage()).thenReturn(MigrationStage.AUTHENTICATION);

        Response response = sut.getMigrationStatus();

        assertThat(response.getEntity().toString(), containsString(MigrationStage.AUTHENTICATION.toString()));
    }

    @Test
    public void testNotFoundWhenMigrationDoesNotExist() {
        when(migrationService.getCurrentStage()).thenReturn(MigrationStage.NOT_STARTED);

        Response response = sut.getMigrationStatus();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testNoContentWhenCreatingMigrationAndNoneExists() throws Exception {
        Migration stubMigration = Mockito.mock(Migration.class);
        when(migrationService.createMigration()).thenReturn(stubMigration);
        doNothing().when(migrationService).transition(MigrationStage.AUTHENTICATION);

        Response response = sut.createMigration();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testBadRequestWhenCreatingMigrationAndOneExists() throws Exception {
        Migration stubMigration = Mockito.mock(Migration.class);
        when(migrationService.createMigration()).thenReturn(stubMigration);
        doThrow(InvalidMigrationStageError.class).when(migrationService).transition(MigrationStage.AUTHENTICATION);


        Response response = sut.createMigration();

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        Map<String, String> entity = (Map<String, String>) response.getEntity();
        assertEquals("Unable to transition migration from initial state", entity.get("error"));
    }

    @Test
    public void testBadRequestWhenCreatingMigrationAndUnableToTransitionPastTheInitialStage() throws Exception {
        doThrow(MigrationAlreadyExistsException.class).when(migrationService).createMigration();

        Response response = sut.createMigration();

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        Map<String, String> entity = (Map<String, String>) response.getEntity();
        assertEquals("migration already exists", entity.get("error"));
        verify(migrationService, never()).transition(MigrationStage.AUTHENTICATION);
    }
}
