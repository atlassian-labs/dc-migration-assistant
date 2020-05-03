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

import React, { ReactElement, useState, useEffect, FunctionComponent } from 'react';
import Button from '@atlaskit/button';
import InlineMessage from '@atlaskit/inline-message';
import styled from 'styled-components';
import { Link, Redirect } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';
import SectionMessage from '@atlaskit/section-message';
import Spinner from '@atlaskit/spinner';

import { migration, MigrationStage, redirectForStage } from '../api/migration';
import { ErrorFlag } from './shared/ErrorFlag';
import { awsAuthPath } from '../utils/RoutePaths';

type HomeProps = {
    title: string;
    synopsis: string;
    startButtonText: string;
};

type ContinuationState = {
    migrationInProgress: boolean;
    currentStage: MigrationStage | undefined;
    continuationPage: string | undefined;
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

const InfoProps = {
    title: I18n.getText('atlassian.migration.datacenter.home.info.title'),
    secondaryText: I18n.getText('atlassian.migration.datacenter.home.info.content'),
};

type ActionSectionProps = {
    continuation: ContinuationState;
    startButtonText: string;
};

const MigrationActionSection: FunctionComponent<ActionSectionProps> = ({
    startButtonText,
    continuation,
}) => {
    const [error, setError] = useState<string>();
    const [loading, setLoading] = useState<boolean>(false);
    const [readyForNextStep, setReadyForNextStep] = useState<boolean>(false);

    const createMigration = (): void => {
        setLoading(true);
        migration
            .createMigration()
            .then(() => {
                setReadyForNextStep(true);
            })
            .catch(err => {
                setLoading(false);
                setError(err);
            });
    };

    if (readyForNextStep) {
        return <Redirect to={awsAuthPath} push />;
    }

    if (continuation.migrationInProgress) {
        return (
            <SectionMessage appearance="warning">
                {I18n.getText('atlassian.migration.datacenter.home.start.alreadyStarted')}
                <ButtonContainer>
                    <Link to={continuation.continuationPage}>
                        <Button>
                            {I18n.getText('atlassian.migration.datacenter.home.continue.button')}
                        </Button>
                    </Link>
                </ButtonContainer>
            </SectionMessage>
        );
    }
    return (
        <>
            <ErrorFlag
                showError={error && error !== ''}
                dismissErrorFunc={(): void => setError('')}
                title={I18n.getText('atlassian.migration.datacenter.home.start.error')}
                description={error}
                id="migration-creation-error"
            />
            <InlineMessage {...InfoProps} />
            <ButtonContainer>
                <Button isLoading={loading} appearance="primary" onClick={createMigration}>
                    {startButtonText}
                </Button>
            </ButtonContainer>
        </>
    );
};

export const Home = ({ title, synopsis, startButtonText }: HomeProps): ReactElement => {
    const [loadingCanStart, setLoadingCanStart] = useState<boolean>(true);
    const [continuation, setContinuation] = useState<ContinuationState>({
        migrationInProgress: false,
        currentStage: undefined,
        continuationPage: undefined,
    });

    useEffect(() => {
        setLoadingCanStart(true);
        migration
            .getMigrationStage()
            .then((stage: string) => {
                if (stage !== 'not_started') {
                    const currentStage = stage as MigrationStage;
                    setContinuation({
                        currentStage,
                        migrationInProgress: true,
                        continuationPage: redirectForStage[currentStage],
                    });
                }
            })
            .finally(() => {
                setLoadingCanStart(false);
            });
    }, []);

    return (
        <HomeContainer>
            <h2>{title}</h2>
            <p>
                {synopsis}{' '}
                <a
                    target="_blank"
                    rel="noreferrer noopener"
                    href="https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html"
                >
                    {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                </a>
            </p>
            {loadingCanStart ? (
                <Spinner />
            ) : (
                <MigrationActionSection
                    startButtonText={startButtonText}
                    continuation={continuation}
                />
            )}
        </HomeContainer>
    );
};
