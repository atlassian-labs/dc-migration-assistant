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

import React, { FunctionComponent, useEffect, useState } from 'react';
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';

import { migration, MigrationStage } from '../api/migration';
import { Link, Redirect } from 'react-router-dom';
import Button from '@atlaskit/button';
import { homePath } from '../utils/RoutePaths';

const MigrationErrorContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`;

export const MigrationError: FunctionComponent = () => {
    const [isInErrorState, setIsInErrorState] = useState<boolean>(false);
    const [redirectToNewMigration, setRedirectToNewMigration] = useState<boolean>(false);

    useEffect(() => {
        migration.getMigrationStage().then((stage: string) => {
            if (stage === MigrationStage.ERROR.valueOf()) {
                setIsInErrorState(true);
            }
        });
    }, []);

    const resetMigration = (): void => {
        migration.resetMigration().then(() => {
            setRedirectToNewMigration(true);
        });
    };

    return (
        <>
            {redirectToNewMigration && <Redirect to={homePath} push />}
            <MigrationErrorContainer>
                <h2>Error</h2>
                <p>
                    This is the skeleton error page for the migration plugin{' '}
                    <a
                        target="_blank"
                        rel="noreferrer noopener"
                        href="https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html"
                    >
                        {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                    </a>
                </p>
                <Button isDisabled={!isInErrorState} onClick={resetMigration} appearance="primary">
                    {I18n.getText('atlassian.migration.datacenter.error.reset.button')}
                </Button>
            </MigrationErrorContainer>
        </>
    );
};
