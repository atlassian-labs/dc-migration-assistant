import { callAppRest } from '../utils/api';

enum RestApiPathConstants {
    migrationRestPath = `migration`,
    migrationSummaryRestPath = `migration/summary`,
}

export enum MigrationStage {
    NOT_STARTED = 'not_started',
    AUTHENTICATION = 'authentication',
    PROVISION_APPLICATION = 'provision_application',
    PROVISION_APPLICATION_WAIT = 'provision_application_wait',
    PROVISION_MIGRATION_STACK = 'provision_migration_stack',
    PROVISION_MIGRATION_STACK_WAIT = 'provision_migration_stack_wait',
    FS_MIGRATION_COPY = 'fs_migration_copy',
    FS_MIGRATION_COPY_WAIT = 'fs_migration_copy_wait',
    OFFLINE_WARNING = 'offline_warning',
    DB_MIGRATION_EXPORT = 'db_migration_export',
    DB_MIGRATION_EXPORT_WAIT = 'db_migration_export_wait',
    DB_MIGRATION_UPLOAD = 'db_migration_upload',
    DB_MIGRATION_UPLOAD_WAIT = 'db_migration_upload_wait',
    DATA_MIGRATION_IMPORT = 'data_migration_import',
    DATA_MIGRATION_IMPORT_WAIT = 'data_migration_import_wait',
    VALIDATE = 'validate',
    CUTOVER = 'cutover',
    FINISHED = 'finished',
    ERROR = 'error',
}

type GetMigrationResult = {
    stage: MigrationStage;
};

export const migration = {
    getMigrationStage: (): Promise<MigrationStage> => {
        return callAppRest('GET', RestApiPathConstants.migrationRestPath).then(res => {
            if (res.ok) {
                return res.json().then(json => {
                    const response = json as GetMigrationResult;
                    return response.stage;
                });
            }
            if (res.status === 404) {
                return MigrationStage.NOT_STARTED;
            }
        });
    },
    createMigration: (): Promise<void> => {
        return callAppRest('POST', RestApiPathConstants.migrationRestPath).then(res => {
            if (res.ok) {
                return Promise.resolve();
            }
            return res.json().then(json => Promise.reject(json.error));
        });
    },
    getMigrationSummary: (): Promise<Record<string, string>> => {
        return callAppRest('GET', RestApiPathConstants.migrationSummaryRestPath).then(res =>
            res.json()
        );
    },
};
