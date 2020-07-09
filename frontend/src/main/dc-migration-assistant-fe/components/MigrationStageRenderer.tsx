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
import Spinner from '@atlaskit/spinner';
import { useCurrentStageRedirect } from '../hooks/useCurrentStageRedirect';

export const MigrationStageRenderer: FunctionComponent = () => {
    const loading = useCurrentStageRedirect();

    if (loading) {
        return <Spinner />;
    }

    // This should never render becasue when we are not loading, we will be redirected to the appropriate stage
    return null;
};
