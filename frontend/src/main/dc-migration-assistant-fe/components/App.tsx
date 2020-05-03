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
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';

import {
    overviewPath,
    homePath,
    awsBasePath,
    fsPath,
    warningPath,
    dbPath,
    validationPath,
} from '../utils/RoutePaths';
import { MigrationOverview } from './MigrationOverview';
import { FileSystemTransferPage } from './fs/FileSystemTransfer';
import { DatabaseTransferPage } from './db/DatabaseMigration';
import { Home } from './Home';
import { AWSRoutes } from './aws/AwsRoutes';
import { ValidateStagePage } from '../stage/Validation';
import { WarningStagePage } from './warning/WarningStage';

export const App: FunctionComponent = () => (
    <Router>
        <Switch>
            <Route exact path={overviewPath}>
                <MigrationOverview />
            </Route>
            <Route path={awsBasePath}>
                <AWSRoutes />
            </Route>
            <Route path={fsPath}>
                <FileSystemTransferPage />
            </Route>
            <Route path={warningPath}>
                <WarningStagePage />
            </Route>
            <Route path={dbPath}>
                <DatabaseTransferPage />
            </Route>
            <Route path={validationPath}>
                <ValidateStagePage />
            </Route>
            <Route exact path={homePath}>
                <Home
                    title={I18n.getText('atlassian.migration.datacenter.home.title')}
                    synopsis={I18n.getText('atlassian.migration.datacenter.home.synopsis')}
                    startButtonText={I18n.getText(
                        'atlassian.migration.datacenter.home.migration.start'
                    )}
                />
            </Route>
        </Switch>
    </Router>
);
