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

import React, { ReactElement } from 'react';
import Button from '@atlaskit/button';
import styled from 'styled-components';
import { Link } from 'react-router-dom';

import { overviewPath } from '../utils/RoutePaths';

type HomeProps = {
    title: string;
    synopsis: string;
    exploreMigrationButtonText: string;
};

const HomeContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`;

const ButtonContainer = styled.div`
    margin-top: 250px;
    align-self: flex-end;
`;

export const Home = ({ title, synopsis, exploreMigrationButtonText }: HomeProps): ReactElement => {
    return (
        <HomeContainer>
            <h2>{title}</h2>
            <p>{synopsis}</p>
            <ButtonContainer>
                <Link to={overviewPath}>
                    <Button appearance="primary">{exploreMigrationButtonText}</Button>
                </Link>
            </ButtonContainer>
        </HomeContainer>
    );
};
