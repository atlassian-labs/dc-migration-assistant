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
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncService;
import com.atlassian.migration.datacenter.spi.CancellableMigrationService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;
import java.util.List;

public class CancellableMigrationServiceHandler implements InitializingBean, DisposableBean {

    private final EventPublisher eventPublisher;

    private final List<CancellableMigrationService> cancellableServices;

     public CancellableMigrationServiceHandler(EventPublisher eventPublisher,
                                               CancellableMigrationService... services
                                              ) {
        this.eventPublisher = eventPublisher;
        this.cancellableServices = Arrays.asList(services);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register(this);
    }

    @EventListener
    public void onMigrationResetEvent(MigrationResetEvent event){
        int migrationId = event.getMigrationId();
        this.cancellableServices.forEach(x -> x.unscheduleMigration(migrationId));
    }

    @Override
    public void destroy() throws Exception {
        this.eventPublisher.unregister(this);
    }
}

