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
package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.DeploymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MigrationHelperDeploymentServiceCleanupTest {

    @Mock
    MigrationService migrationService;

    @Mock
    MigrationContext migrationContext;

    @Test
    void shouldClearMigrationStackPersistedStackDetails(){
        DeploymentService migrationHelperService = new AWSMigrationHelperDeploymentService(null, () -> null, migrationService);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);

        migrationHelperService.clearPersistedStackDetails();

        verify(migrationContext).setFsRestoreStatusSsmDocument("");
        verify(migrationContext).setFsRestoreSsmDocument("");
        verify(migrationContext).setRdsRestoreSsmDocument("");
        verify(migrationContext).setMigrationBucketName("");
        verify(migrationContext).setMigrationStackAsgIdentifier("");
        verify(migrationContext).setMigrationDLQueueUrl("");
        verify(migrationContext).setMigrationQueueUrl("");

        verify(migrationContext).save();
    }


}
