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

import { MigrationStage } from '../api/migration';
import * as paths from './RoutePaths';

export const getPathForStage = (stage: MigrationStage): string => {
    switch (stage) {
        case 'not_started':
            return paths.homePath;
        case 'authentication':
            return paths.awsAuthPath;
        case 'provision_application':
            return paths.asiConfigurationPath;
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
            return paths.warningPath;
        case 'db_migration_export':
            return paths.finalSyncPath;
        case 'db_migration_export_wait':
            return paths.finalSyncPath;
        case 'db_migration_upload':
            return paths.finalSyncPath;
        case 'db_migration_upload_wait':
            return paths.finalSyncPath;
        case 'data_migration_import':
            return paths.finalSyncPath;
        case 'data_migration_import_wait':
            return paths.finalSyncPath;
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
