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

import { I18n } from '@atlassian/wrm-react-i18n';
import { MigrationDuration } from './common';
import { callAppRest } from '../utils/api';

const finalSyncAPIBase = 'migration/final-sync';
const finalSyncRetryBase = `${finalSyncAPIBase}/retry`;
export const finalSyncStatusEndpoint = `${finalSyncAPIBase}/status`;
export const finalSyncStartEndpoint = `${finalSyncAPIBase}/start`;
export const dbLogsEndpoint = `${finalSyncAPIBase}/db-logs`;

export enum DBMigrationStatus {
    NOT_STARTED = 'NOT_STARTED',
    EXPORTING = 'EXPORTING',
    UPLOADING = 'UPLOADING',
    IMPORTING = 'IMPORTING',
    DONE = 'DONE',
    FAILED = 'FAILED',
}

const handleRetryResponse = (res: Response): Promise<void> => {
    switch (res.status) {
        case 202:
            return Promise.resolve();
        case 409:
            return Promise.reject(
                Error(I18n.getText('atlassian.migration.datacenter.sync.fs.retry.error'))
            );
        case 400:
            return Promise.reject(
                Error(I18n.getText('atlassian.migration.datacenter.sync.fs.retry.unnecessary'))
            );
        default:
            return Promise.reject(
                Error(
                    I18n.getText(
                        'atlassian.migration.datacenter.sync.fs.retry.error.unexpected',
                        res.status
                    )
                )
            );
    }
};

export const finalSync = {
    retryFsSync: (): Promise<void> =>
        callAppRest('PUT', `${finalSyncRetryBase}/fs`).then(handleRetryResponse),
    retryDbMigration: (): Promise<void> =>
        callAppRest('PUT', `${finalSyncRetryBase}/db`).then(handleRetryResponse),
};

export const statusToI18nString = (status: DBMigrationStatus): string => {
    const name = status.toString().toLowerCase();
    switch (name) {
        case 'failed':
            return I18n.getText('atlassian.migration.datacenter.db.status.failed');
        case 'exporting':
            return I18n.getText('atlassian.migration.datacenter.db.status.exporting');
        case 'uploading':
            return I18n.getText('atlassian.migration.datacenter.db.status.uploading');
        case 'done':
            return I18n.getText('atlassian.migration.datacenter.db.status.done');
        case 'importing':
            return I18n.getText('atlassian.migration.datacenter.db.status.importing');
        default:
            return I18n.getText('atlassian.migration.datacenter.db.status.unknown');
    }
};

export type FinalSyncStatus = {
    db: DatabaseMigrationStatusResult;
    fs: FinalFileSyncStatus;
    errorMessage: string;
};

export type FinalFileSyncStatus = {
    uploaded: number;
    downloaded: number;
    failed: number;
    hasProgressedToNextStage: boolean;
};

// See DatabaseMigrationProgress.kt
export type DatabaseMigrationStatusResult = {
    status: DBMigrationStatus;
    elapsedTime: MigrationDuration;
};

export type CommandDetails = {
    errorMessage?: string;
    consoleUrl?: string;
    criticalError?: boolean;
};
