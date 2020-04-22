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
import moment from 'moment';
import {
    MigrationTransferProps,
    MigrationTransferPage,
    Progress,
} from '../shared/MigrationTransferPage';
import { fs } from '../../api/fs';

const dummyStarted = moment();

dummyStarted.subtract(49, 'hours');
dummyStarted.subtract(23, 'minutes');

const getFsMigrationProgress = (): Promise<Progress> => {
    return fs
        .getFsMigrationStatus()
        .then(result => {
            if (result.status === 'UPLOADING') {
                const progress: Progress = {
                    elapsedTimeSeconds: result.elapsedTime.seconds,
                    phase: I18n.getText('atlassian.migration.datacenter.fs.phase.upload'),
                };

                if (result.crawlingFinished) {
                    const uploadProgress = result.uploadedFiles / result.filesFound;
                    const weightedProgress = 0.5 * uploadProgress;
                    return {
                        ...progress,
                        completeness: weightedProgress,
                    };
                }
                return progress;
            }
            if (result.status === 'DOWNLOADING') {
                const downloadProgress = result.downloadedFiles / result.filesFound;
                const weightedProgress = 0.5 + 0.5 * downloadProgress;
                return {
                    phase: I18n.getText('atlassian.migration.datacenter.fs.phase.download'),
                    completeness: weightedProgress,
                };
            }
            if (result.status === 'DONE') {
                return {
                    phase: I18n.getText('atlassian.migration.datacenter.fs.phase.download'),
                    completeness: 1,
                    completeMessage: {
                        boldPrefix: I18n.getText(
                            'atlassian.migration.datacenter.fs.completeMessage.boldPrefix',
                            result.downloadedFiles,
                            result.filesFound
                        ),
                        message: I18n.getText(
                            'atlassian.migration.datacenter.fs.completeMessage.message'
                        ),
                    },
                };
            }
            if (result.status === 'NOT_STARTED') {
                return {
                    phase: I18n.getText('atlassian.migration.datacenter.fs.phase.notStarted'),
                };
            }
            return {
                phase: I18n.getText('atlassian.migration.datacenter.generic.error'),
                completeness: 0,
            };
        })
        .catch(err => {
            const error = err as Error;
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
    getProgress: getFsMigrationProgress,
};

export const FileSystemTransferPage: FunctionComponent = () => {
    return <MigrationTransferPage {...fsMigrationTranferPageProps} />;
};
