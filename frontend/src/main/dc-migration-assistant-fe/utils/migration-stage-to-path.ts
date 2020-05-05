import { MigrationStage } from '../api/migration';
import * as paths from './RoutePaths';

export const getPathForStage = (stage: MigrationStage): string => {
    switch (stage) {
        case 'not_started':
            return paths.homePath;
        case 'authentication':
            return paths.awsAuthPath;
        case 'provision_application':
            return paths.quickstartPath;
        case 'provision_application_wait':
            return paths.quickstartStatusPath;
        case 'provision_migration_stack':
            return paths.quickstartStatusPath;
        case 'provision_migration_stack_wait':
            return paths.quickstartStatusPath;
        case 'fs_migration_copy':
            return paths.fsPath;
        case 'fs_migration_copy_wait':
            return paths.fsPath;
        case 'offline_warning':
            return paths.dbPath;
        case 'db_migration_export':
            return paths.dbPath;
        case 'db_migration_export_wait':
            return paths.dbPath;
        case 'db_migration_upload':
            return paths.dbPath;
        case 'db_migration_upload_wait':
            return paths.dbPath;
        case 'data_migration_import':
            return paths.dbPath;
        case 'data_migration_import_wait':
            return paths.dbPath;
        case 'validate':
            return paths.validationPath;
        case 'cutover':
            return paths.validationPath;
        case 'finished':
            return paths.validationPath;
        case 'error':
            return paths.migrationErrorPath;
        default:
            return paths.homePath;
    }
};
