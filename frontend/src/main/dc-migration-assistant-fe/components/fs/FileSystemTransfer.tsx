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
import moment from 'moment';

const dummyStarted = moment();

dummyStarted.subtract(49, 'hours');
dummyStarted.subtract(23, 'minutes');

const fsMigrationTranferPageProps: MigrationTransferProps = {
    heading: I18n.getText('atlassian.migration.datacenter.fs.title'),
    description: I18n.getText('atlassian.migration.datacenter.fs.description'),
    infoTitle: I18n.getText('atlassian.migration.datacenter.fs.infoTitle'),
    infoContent: I18n.getText('atlassian.migration.datacenter.fs.infoContent'),
    infoActions: [
        {
            key: 'learn',
            href:
                'https://media0.giphy.com/media/a6OnFHzHgCU1O/giphy.gif?cid=ecf05e472ee78099c642a7d2427127e6f1d4d6f0b77551c7&rid=giphy.gif',
            text: I18n.getText('atlassian.migration.datacenter.common.learn_more'),
        },
    ],
    nextText: I18n.getText('atlassian.migration.datacenter.fs.nextStep'),
    started: dummyStarted,
};

export const FileSystemTransferPage: FunctionComponent = () => {
    return <MigrationTransferPage {...fsMigrationTranferPageProps} />;
};
