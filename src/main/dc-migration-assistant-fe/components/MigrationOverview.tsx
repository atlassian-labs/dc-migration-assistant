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
import styled from 'styled-components';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { Link } from 'react-router-dom';

import { homePath } from '../utils/RoutePaths';

const OverviewContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`;

const ButtonContainer = styled.div`
    margin-top: 250px;
    align-self: flex-start;
`;
export const MigrationOverview: FunctionComponent = () => {
    return (
        <OverviewContainer>
            <h1>{I18n.getText('atlassian.migration.datacenter.overview.title')}</h1>
            <ButtonContainer>
                <Link to={homePath}>
                    <Button appearance="danger">Abort</Button>
                </Link>
            </ButtonContainer>
        </OverviewContainer>
    );
};
