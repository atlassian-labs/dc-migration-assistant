import { callAppRest } from '../utils/api';

enum RestApiPathConstants {
    fsStatusRestPath = `migration/fs/report`,
}

type FailedFile = {
    path: string;
    reason: string;
};

type FileSystemMigrationStatusResponse = {
    status: 'NOT_STARTED' | 'FAILED' | 'UPLOADING' | 'DOWNLOADING' | 'DONE';
    failedFiles: Array<FailedFile>;
    uploadedFiles: number;
    filesFound: number;
    crawlingFinished: boolean;
    filesInFlight: number;
    downloadedFiles: number;
};

export const fs = {
    getFsMigrationStatus: (): Promise<FileSystemMigrationStatusResponse> => {
        return callAppRest('GET', RestApiPathConstants.fsStatusRestPath).then(result =>
            result.json()
        );
    },
};
