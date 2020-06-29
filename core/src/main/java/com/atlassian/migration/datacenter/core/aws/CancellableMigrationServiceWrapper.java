package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService;
import com.atlassian.migration.datacenter.core.fs.S3FilesystemMigrationService;
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncService;
import com.atlassian.migration.datacenter.core.util.MigrationRunner;
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
    public void onMigrationResetEvent(OnMigrationResetEvent event){
        int migrationId = event.getMigrationId();
        this.s3FilesystemMigrationService.unscheduleMigration(migrationId);
    }
}

class OnMigrationResetEvent{
    private final int migrationId;

    public OnMigrationResetEvent(int migrationId) {
        this.migrationId = migrationId;
    }

    public int getMigrationId() {
        return migrationId;
    }
}
