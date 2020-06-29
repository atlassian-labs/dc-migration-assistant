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

package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService;
import com.atlassian.migration.datacenter.core.fs.S3FilesystemMigrationService;
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncService;
import org.springframework.beans.factory.InitializingBean;

public class CancellableMigrationServiceWrapper implements InitializingBean {

    private final EventPublisher eventPublisher;
    private final S3FinalSyncService s3FinalSyncService;
    private final S3FilesystemMigrationService s3FilesystemMigrationService;
    private final DatabaseMigrationService databaseMigrationService;

    public CancellableMigrationServiceWrapper(EventPublisher eventPublisher,
                                              S3FinalSyncService s3FinalSyncService,
                                              S3FilesystemMigrationService s3FilesystemMigrationService,
                                              DatabaseMigrationService databaseMigrationService) {
        this.eventPublisher = eventPublisher;
        this.s3FinalSyncService = s3FinalSyncService;
        this.s3FilesystemMigrationService = s3FilesystemMigrationService;
        this.databaseMigrationService = databaseMigrationService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register(this);
    }

    @EventListener
    public void onMigrationResetEvent(MigrationResetEvent event){
        int migrationId = event.getMigrationId();
        this.s3FilesystemMigrationService.unscheduleMigration(migrationId);
        this.s3FinalSyncService.unscheduleMigration(migrationId);
        this.databaseMigrationService.unscheduleMigration(migrationId);
    }
}

