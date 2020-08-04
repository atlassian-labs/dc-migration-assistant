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

import React, { FunctionComponent } from 'react';
import { I18n } from '@atlassian/wrm-react-i18n';

import { MigrationTransferProps, MigrationTransferPage } from '../shared/MigrationTransferPage';
import { Progress, ProgressBuilder } from '../shared/Progress';
import { callAppRest } from '../../utils/api';
import {
    finalSyncStatusEndpoint,
    finalSyncStartEndpoint,
    DatabaseMigrationStatusResult,
    statusToI18nString,
    dbLogsEndpoint,
    DBMigrationStatus,
    CommandDetails,
    FinalSyncStatus,
    finalSync,
} from '../../api/final-sync';
import { MigrationStage } from '../../api/migration';
import { validationPath } from '../../utils/RoutePaths';

const finalSyncInProgressStages = [
    MigrationStage.DATA_MIGRATION_IMPORT,
    MigrationStage.DATA_MIGRATION_IMPORT_WAIT,
    MigrationStage.DB_MIGRATION_EXPORT,
    MigrationStage.DB_MIGRATION_EXPORT_WAIT,
    MigrationStage.DB_MIGRATION_UPLOAD,
    MigrationStage.DB_MIGRATION_UPLOAD_WAIT,
    MigrationStage.FINAL_SYNC_WAIT,
    MigrationStage.FINAL_SYNC_ERROR,
    MigrationStage.VALIDATE, // Because of the auto-transition from FINAL_SYNC_WAIT to VALIDATE, we need to add/hack this in here :(
];

const dbStatusToProgress = (status: FinalSyncStatus): Progress => {
    const { db, errorMessage } = status;
    const builder = new ProgressBuilder();

    builder.setPhase(statusToI18nString(db.status));
    builder.setElapsedSeconds(db.elapsedTime.seconds);
    if (db.status === DBMigrationStatus.FAILED) {
        builder.setError(
            <p>
                {I18n.getText('atlassian.migration.datacenter.db.retry.error')}
                <br />
                <p />
                <strong>{I18n.getText('atlassian.migration.datacenter.generic.error')}:</strong>
                <br />
                {errorMessage}
            </p>
        );
    }
    switch (db.status) {
        case 'DONE':
            builder.setCompleteness(1);
            break;
        case 'EXPORTING':
            builder.setCompleteness(0.25);
            break;
        case 'UPLOADING':
            builder.setCompleteness(0.5);
            break;
        case 'IMPORTING':
            builder.setCompleteness(0.75);
            break;
        default:
            builder.setCompleteness(0);
    }
    builder.setCompleteMessage(
        '',
        I18n.getText('atlassian.migration.datacenter.db.completeMessage')
    );

    return builder.build();
};

const fsSyncStatusToProgress = (status: FinalSyncStatus): Progress => {
    const { fs, db, errorMessage } = status;
    const { downloaded, uploaded, failed, hasProgressedToNextStage } = fs;
    const builder = new ProgressBuilder();
    builder.setPhase(I18n.getText('atlassian.migration.datacenter.sync.fs.phase'));
    // FIXME: This time will be wrong when one of the components of final sync completes
    builder.setElapsedSeconds(db.elapsedTime.seconds);
    if (hasProgressedToNextStage) {
        builder.setCompleteness(1);
    } else {
        // If there are no files to upload (i.e. uploaded = 0), the state will be transitioned to `Validate` and conditional branch will not be evaluated due to the `hasProgressedToNextStage` check above.
        builder.setCompleteness(uploaded === 0 ? 0 : downloaded / uploaded);
    }

    builder.setCompleteMessage(
        I18n.getText(
            'atlassian.migration.datacenter.sync.fs.completeMessage.boldPrefix',
            downloaded,
            uploaded
        ),
        I18n.getText('atlassian.migration.datacenter.sync.fs.completeMessage.message')
    );

    if (failed) {
        builder.setError(
            <p>
                {I18n.getText('atlassian.migration.datacenter.sync.fs.download.error', fs.failed)}
                <a href="https://status.aws.amazon.com/" target="_blank" rel="noreferrer noopener">
                    {I18n.getText('atlassian.migration.datacenter.common.aws.status')}
                </a>
                <br />
                <p />
                <strong>{I18n.getText('atlassian.migration.datacenter.generic.error')}:</strong>
                <br />
                {errorMessage}
            </p>
        );
    }

    return builder.build();
};

const fetchFinalSyncStatus = async (): Promise<FinalSyncStatus> => {
    return callAppRest('GET', finalSyncStatusEndpoint).then((result: Response) => result.json());
};

const startFinalSync = async (): Promise<void> => {
    return callAppRest('PUT', finalSyncStartEndpoint).then(result => result.json());
};

const getProgressFromStatus = async (): Promise<Array<Progress>> => {
    return fetchFinalSyncStatus().then(result => {
        return [dbStatusToProgress(result), fsSyncStatusToProgress(result)];
    });
};

const fetchDBMigrationLogs = async (): Promise<CommandDetails> => {
    return callAppRest('GET', dbLogsEndpoint).then(result => result.json());
};

const props: MigrationTransferProps = {
    heading: I18n.getText('atlassian.migration.datacenter.finalSync.title'),
    description: I18n.getText('atlassian.migration.datacenter.finalSync.description'),
    nextText: I18n.getText('atlassian.migration.datacenter.fs.nextStep'),
    nextRoute: validationPath,
    startButtonText: I18n.getText('atlassian.migration.datacenter.finalSync.startButton'),
    startMigrationPhase: startFinalSync,
    inProgressStages: finalSyncInProgressStages,
    processes: [
        {
            getProgress: (): Promise<Progress> => getProgressFromStatus().then(result => result[0]),
            retryProps: {
                retryText: I18n.getText('atlassian.migration.datacenter.sync.db.retry'),
                onRetry: finalSync.retryDbMigration,
                canContinueOnFailure: false,
            },
        },
        {
            getProgress: (): Promise<Progress> => getProgressFromStatus().then(result => result[1]),
            retryProps: {
                retryText: I18n.getText('atlassian.migration.datacenter.sync.fs.retry'),
                onRetry: finalSync.retryFsSync,
                canContinueOnFailure: false,
            },
        },
    ],
    getDetails: fetchDBMigrationLogs,
};

export const FinalSyncPage: FunctionComponent = () => {
    return <MigrationTransferPage {...props} />;
};
