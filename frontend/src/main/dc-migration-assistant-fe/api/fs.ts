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
import { MigrationDuration } from './common';

enum RestApiPathConstants {
    fsStatusRestPath = `migration/fs/report`,
    fsStartRestPath = `migration/fs/start`,
    fsFinalSyncPath = `migration/fs/final-sync`,
}

type FailedFile = {
    filePath: string;
    reason: string;
};

type GetFinalSyncResponse = {
    files: Array<string>;
};

export type FileSystemMigrationStatusResponse = {
    status: 'NOT_STARTED' | 'FAILED' | 'UPLOADING' | 'DOWNLOADING' | 'DONE';
    elapsedTime: MigrationDuration;
    failedFiles: Array<FailedFile>;
    uploadedFiles: number;
    filesFound: number;
    crawlingFinished: boolean;
    filesInFlight: number;
    downloadedFiles: number;
};

type FileSystemMigrationStartResponse = {
    error?: string;
    status?: string;
    migrationScheduled?: boolean;
};

export const fs = {
    getFsMigrationStatus: async (): Promise<FileSystemMigrationStatusResponse> => {
        const result = await callAppRest('GET', RestApiPathConstants.fsStatusRestPath);
        return result.json();
    },

    startFsMigration: async (): Promise<void> => {
        const result = await callAppRest('PUT', RestApiPathConstants.fsStartRestPath);
        if (result.ok) {
            return Promise.resolve();
        }

        const jsonResult = await result.json();
        const errorJson = jsonResult as FileSystemMigrationStartResponse;
        if (errorJson.error) {
            // Probably invalid migration stage
            return Promise.reject(new Error(errorJson.error));
        }
        if (errorJson.status) {
            // FS migration is already in progress
            return Promise.resolve();
        }
        if (errorJson.migrationScheduled === false) {
            return Promise.reject(new Error('Unable to start file system migration'));
        }
        return Promise.reject(JSON.stringify(result));
    },

    getCapturedFiles: async (): Promise<Array<string>> => {
        const result = await callAppRest('GET', RestApiPathConstants.fsFinalSyncPath);
        const capturedFiles = (await result.json()) as GetFinalSyncResponse;
        return capturedFiles.files || [];
    },
};
