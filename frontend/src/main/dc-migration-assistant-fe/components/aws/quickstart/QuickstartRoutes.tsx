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

import React, { FunctionComponent, useState } from 'react';
import { Route, Switch } from 'react-router-dom';
import { asiConfigurationPath, quickstartPath } from '../../../utils/RoutePaths';
import { ASIConfiguration } from './asi/ASIConfiguration';
import { QuickStartDeploy } from './QuickStartDeploy';

export type DeploymentMode = 'with-vpc' | 'standalone';

export const QuickstartRoutes: FunctionComponent = () => {
    const [prefix, setPrefix] = useState<string>();
    const [deploymentMode, setDeploymentMode] = useState<DeploymentMode>();

    return (
        <Switch>
            <Route exact path={asiConfigurationPath}>
                <ASIConfiguration
                    onSelectDeploymentMode={setDeploymentMode}
                    updateASIPrefix={setPrefix}
                />
            </Route>
            <Route exact path={quickstartPath}>
                <QuickStartDeploy
                    deploymentMode={deploymentMode}
                    ASIPrefix={prefix?.length === 0 ? 'ATL-' : prefix}
                />
            </Route>
        </Switch>
    );
};
