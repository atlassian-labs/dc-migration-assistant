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

import React, { FunctionComponent, useState, useEffect } from 'react';

import { I18n } from '@atlassian/wrm-react-i18n';
import moment from 'moment';
import Spinner from '@atlaskit/spinner';
import { MigrationTransferProps, MigrationTransferPage } from '../shared/MigrationTransferPage';
import { Progress, ProgressBuilder } from '../shared/Progress';
import { fs, FileSystemMigrationStatusResponse } from '../../api/fs';
import { migration, MigrationStage } from '../../api/migration';
import { dbPath } from '../../utils/RoutePaths';

const dummyStarted = moment();

dummyStarted.subtract(49, 'hours');
dummyStarted.subtract(23, 'minutes');

const getErrorFromResult = (result: FileSystemMigrationStatusResponse): string | undefined => {
    if (result?.failedFiles.length > 0) {
        const failedFilesCount = result.failedFiles.length;
        const reasons: Record<string, boolean> = {};

        result.failedFiles.forEach(failure => {
            if (!reasons[failure.reason]) {
                reasons[failure.reason] = true;
            }
        });

        const reasonsString = Object.keys(reasons).join(', ');

        return `Encountered ${failedFilesCount} upload errors. Error details: ${reasonsString}`;
    }
    return undefined;
};

const getCompletenessFromResult = (result: FileSystemMigrationStatusResponse): number => {
    if (result.status === 'UPLOADING' || result.status === 'DOWNLOADING') {
        let offset = 0;
        if (result.status === 'DOWNLOADING') {
            offset = 0.5;
        }
        const downloadProgress = result.downloadedFiles / result.filesFound;
        return offset + 0.5 * downloadProgress;
    }
    if (result.status === 'DONE') {
        return 1;
    }
    return 0;
};

const getPhaseFromStatus = (result: FileSystemMigrationStatusResponse): string => {
    switch (result.status) {
        case 'DONE':
            return I18n.getText('atlassian.migration.datacenter.fs.phase.complete');
        case 'DOWNLOADING':
            return I18n.getText('atlassian.migration.datacenter.fs.phase.download');
        case 'UPLOADING':
            return I18n.getText('atlassian.migration.datacenter.fs.phase.upload');
        case 'NOT_STARTED':
            return I18n.getText('atlassian.migration.datacenter.fs.phase.notStarted');
        default:
            return I18n.getText('atlassian.migration.datacenter.generic.error');
    }
};

const getFsMigrationProgress = (): Promise<Progress> => {
    return fs
        .getFsMigrationStatus()
        .then(result => {
            const builder: ProgressBuilder = new ProgressBuilder();

            builder.setError(getErrorFromResult(result));
            builder.setCompleteness(getCompletenessFromResult(result));
            builder.setPhase(getPhaseFromStatus(result));
            builder.setElapsedSeconds(result.elapsedTime.seconds);

            if (result.status === 'DONE') {
                builder.setCompleteMessage(
                    I18n.getText(
                        'atlassian.migration.datacenter.fs.completeMessage.boldPrefix',
                        result.downloadedFiles,
                        result.filesFound
                    ),
                    I18n.getText('atlassian.migration.datacenter.fs.completeMessage.message')
                );
            }

            return builder.build();
        })
        .catch(err => {
            const error = err as Error;
            // JSON parse error usually means we're querying the progress before the fs migration has started
            if (error.message.indexOf('JSON.parse') >= 0) {
                return {
                    phase: I18n.getText('atlassian.migration.datacenter.fs.phase.notStarted'),
                };
            }
            return {
                phase: I18n.getText('atlassian.migration.datacenter.generic.error'),
                error: error.message,
            };
        });
};

const fsMigrationTranferPageProps: MigrationTransferProps = {
    heading: I18n.getText('atlassian.migration.datacenter.fs.title'),
    description: I18n.getText('atlassian.migration.datacenter.fs.description'),
    nextText: I18n.getText('atlassian.migration.datacenter.fs.nextStep'),
    inProgressStages: [MigrationStage.FS_MIGRATION_COPY_WAIT],
    startMigrationPhase: fs.startFsMigration,
    getProgress: getFsMigrationProgress,
    nextRoute: dbPath,
};

export const FileSystemTransferPage: FunctionComponent = () => {
    const [loading, setLoading] = useState<boolean>(true);
    const [hasStarted, setHasStarted] = useState<boolean>(false);

    useEffect(() => {
        setLoading(true);
        migration
            .getMigrationStage()
            .then(stage => {
                if (stage === MigrationStage.FS_MIGRATION_COPY_WAIT) {
                    setHasStarted(true);
                }
                setLoading(false);
            })
            .catch(() => {
                setHasStarted(false);
                setLoading(false);
            });
    }, []);
    if (loading) {
        return <Spinner />;
    }
    return <MigrationTransferPage {...fsMigrationTranferPageProps} />;
};
