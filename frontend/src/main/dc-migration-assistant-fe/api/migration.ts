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

import { callAppRest } from '../utils/api';
import { homePath, quickstartStatusPath, awsAuthPath } from '../utils/RoutePaths';

enum RestApiPathConstants {
    migrationRestPath = `migration`,
    migrationSummaryRestPath = `migration/summary`,
    migrationResetPath = `develop/migration/reset`,
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

export const redirectForStage: Record<MigrationStage, string> = {
    [MigrationStage.NOT_STARTED]: homePath,
    [MigrationStage.AUTHENTICATION]: awsAuthPath,
    // FIXME: To be filled out...
    [MigrationStage.PROVISION_APPLICATION]: 'FIXME',
    [MigrationStage.PROVISION_APPLICATION_WAIT]: 'FIXME',
    [MigrationStage.PROVISION_MIGRATION_STACK]: 'FIXME',
    [MigrationStage.PROVISION_MIGRATION_STACK_WAIT]: 'FIXME',
    [MigrationStage.FS_MIGRATION_COPY]: 'FIXME',
    [MigrationStage.FS_MIGRATION_COPY_WAIT]: 'FIXME',
    [MigrationStage.OFFLINE_WARNING]: 'FIXME',
    [MigrationStage.DB_MIGRATION_EXPORT]: 'FIXME',
    [MigrationStage.DB_MIGRATION_EXPORT_WAIT]: 'FIXME',
    [MigrationStage.DB_MIGRATION_UPLOAD]: 'FIXME',
    [MigrationStage.DB_MIGRATION_UPLOAD_WAIT]: 'FIXME',
    [MigrationStage.DATA_MIGRATION_IMPORT]: 'FIXME',
    [MigrationStage.DATA_MIGRATION_IMPORT_WAIT]: 'FIXME',
    [MigrationStage.VALIDATE]: 'FIXME',
    [MigrationStage.CUTOVER]: 'FIXME',
    [MigrationStage.FINISHED]: 'FIXME',
    [MigrationStage.ERROR]: 'FIXME',
};

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
    resetMigration: (): Promise<void> => {
        return callAppRest('DELETE', RestApiPathConstants.migrationResetPath).then(res => {
            if (res.ok) {
                return Promise.resolve();
            }
            return res.json().then(json => Promise.reject(json.error));
        });
    },
};

// Convenience global for test automation.
declare global {
    interface Window {
        AtlassianMigration: any;
    }
}
window.AtlassianMigration = migration;
