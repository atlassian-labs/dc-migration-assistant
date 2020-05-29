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
    dbStatusReportEndpoint,
    dbStartEndpoint,
    DatabaseMigrationStatus,
    statusToI18nString,
    dbLogsEndpoint,
    DBMigrationStatus,
    CommandDetails,
} from '../../api/db';
import { MigrationStage } from '../../api/migration';
import { validationPath } from '../../utils/RoutePaths';

const dbMigrationInProgressStages = [
    MigrationStage.DATA_MIGRATION_IMPORT,
    MigrationStage.DATA_MIGRATION_IMPORT_WAIT,
    MigrationStage.DB_MIGRATION_EXPORT,
    MigrationStage.DB_MIGRATION_EXPORT_WAIT,
    MigrationStage.DB_MIGRATION_UPLOAD,
    MigrationStage.DB_MIGRATION_UPLOAD_WAIT,
];

const toProgress = (status: DatabaseMigrationStatus): Progress => {
    const builder = new ProgressBuilder();

    builder.setPhase(statusToI18nString(status.status));
    builder.setElapsedSeconds(status.elapsedTime.seconds);
    builder.setFailed(status.status === DBMigrationStatus.FAILED);

    if (status.status === DBMigrationStatus.DONE) {
        builder.setCompleteness(1);
        builder.setCompleteMessage(
            '',
            I18n.getText('atlassian.migration.datacenter.db.completeMessage')
        );
    }

    return builder.build();
};

const fetchDBMigrationStatus = (): Promise<DatabaseMigrationStatus> => {
    return callAppRest('GET', dbStatusReportEndpoint).then((result: any) => result.json());
};

const startDbMigration = (): Promise<void> => {
    return callAppRest('PUT', dbStartEndpoint).then(result => result.json());
};

const getProgressFromStatus = (): Promise<Progress> => {
    return fetchDBMigrationStatus().then(toProgress);
};

const fetchDBMigrationLogs = (): Promise<CommandDetails> => {
    return callAppRest('GET', dbLogsEndpoint).then(result => result.json());
};

const props: MigrationTransferProps = {
    heading: I18n.getText('atlassian.migration.datacenter.db.title'),
    description: I18n.getText('atlassian.migration.datacenter.db.description'),
    nextText: I18n.getText('atlassian.migration.datacenter.fs.nextStep'),
    startButtonText: I18n.getText('atlassian.migration.datacenter.db.startButton'),
    startMigrationPhase: startDbMigration,
    inProgressStages: dbMigrationInProgressStages,
    getProgress: getProgressFromStatus,
    nextRoute: validationPath,
    getDetails: fetchDBMigrationLogs,
};

export const DatabaseTransferPage: FunctionComponent = () => {
    return <MigrationTransferPage {...props} />;
};
