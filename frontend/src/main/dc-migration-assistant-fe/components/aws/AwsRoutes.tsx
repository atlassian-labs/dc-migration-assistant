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
import { Route, Switch } from 'react-router-dom';

import { awsAuthPath, quickstartPath, quickstartStatusPath } from '../../utils/RoutePaths';
import { QuickStartDeploy } from './quickstart/QuickStartDeploy';
import { QuickStartStatus } from './quickstart/QuickStartStatus';
import { AuthenticateAWS, CredSubmitFun, QueryRegionFun } from './auth/AuthenticateAWS';
import { callAppRest, RestApiPathConstants } from '../../utils/api';

const getRegions: QueryRegionFun = () => {
    return callAppRest('GET', RestApiPathConstants.awsRegionListPath).then(response =>
        response.json()
    );
};

const saveAWSCredentials: CredSubmitFun = creds => {
    return callAppRest('POST', RestApiPathConstants.awsCredentialsStorePath, creds).then(response =>
        response.json()
    );
};

export const AWSRoutes: FunctionComponent = () => (
    <Switch>
        <Route exact path={quickstartPath}>
            <QuickStartDeploy />
        </Route>
        <Route exact path={quickstartStatusPath}>
            <QuickStartStatus />
        </Route>
        <Route exact path={awsAuthPath}>
            <AuthenticateAWS getRegions={getRegions} onSubmitCreds={saveAWSCredentials} />
        </Route>
    </Switch>
);
