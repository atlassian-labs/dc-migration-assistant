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

package com.atlassian.migration.datacenter.api.develop;

import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DevelopEndpointTest {
    @Mock
    MigrationService migrationService;

    DevelopEndpoint endpoint;

    @BeforeEach
    void setup() {
        endpoint = new DevelopEndpoint(migrationService);
    }

    @Test
    void shouldSetStageWithCorrectTargetStage() throws Exception {
        final MigrationStage initialStage = MigrationStage.AUTHENTICATION;
        when(migrationService.getCurrentStage()).thenReturn(initialStage);


        final ObjectMapper objectMapper = new ObjectMapper();
        final MigrationStage migrationStage = objectMapper.readValue("\"FS_MIGRATION_COPY\"", MigrationStage.class);

        endpoint.setMigrationStage(migrationStage);

        verify(migrationService).transition(initialStage, MigrationStage.FS_MIGRATION_COPY);
    }
}
