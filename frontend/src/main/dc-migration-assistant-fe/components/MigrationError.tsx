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

import React, { FunctionComponent, ReactElement, useEffect, useState } from 'react';
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';

import { migration, MigrationStage } from '../api/migration';
import { Link, Redirect } from 'react-router-dom';
import Button from '@atlaskit/button';
import { homePath } from '../utils/RoutePaths';
import { getPathForStage } from '../utils/migration-stage-to-path';

const MigrationErrorContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: flex-start;
`;

type ResetMigrationProps = {
    currentStage: MigrationStage;
    resetMigrationFunc: VoidFunction;
    errorContent: string;
};

const StageAwareMigrationSection = ({
    resetMigrationFunc,
    currentStage,
    errorContent,
}: ResetMigrationProps): ReactElement => {
    const learnMoreText = I18n.getText('atlassian.migration.datacenter.common.learn_more');

    if (currentStage === MigrationStage.ERROR) {
        return (
            <>
                <h2>Error</h2>
                <p>
                    The Migration assistant entered an error state <br />
                    Click here to{' '}
                    <a
                        target="_blank"
                        rel="noreferrer noopener"
                        href="https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html?#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-errors"
                    >
                        {learnMoreText.toLowerCase()}
                    </a>{' '}
                    about why this may have occurred
                    {errorContent} <br />
                </p>
                <Button onClick={resetMigrationFunc} appearance="primary">
                    {I18n.getText('atlassian.migration.datacenter.error.reset.button')}
                </Button>
            </>
        );
    }

    return (
        <>
            <p>
                This is an error page that is shown only when a migration fails. You currently have
                a migration in progress.
            </p>
            <p>Click the button below to view the latest status.</p>
            <Link to={getPathForStage(currentStage)}>
                <Button appearance="primary">
                    {I18n.getText('atlassian.migration.datacenter.error.current.stage')}
                </Button>
            </Link>
        </>
    );
};

export const MigrationError: FunctionComponent = () => {
    const [isInErrorState, setIsInErrorState] = useState<boolean>(false);
    const [currentStage, setCurrentStage] = useState<MigrationStage>(MigrationStage.NOT_STARTED);
    const [redirectToNewMigration, setRedirectToNewMigration] = useState<boolean>(false);

    useEffect(() => {
        migration.getMigrationStage().then((stage: string) => {
            setCurrentStage(stage as MigrationStage);
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
                <StageAwareMigrationSection
                    currentStage={currentStage}
                    resetMigrationFunc={resetMigration}
                    errorContent=""
                />
            </MigrationErrorContainer>
        </>
    );
};
