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

import React, { ReactElement, useEffect, useState, FunctionComponent, ReactNode } from 'react';
import Button from '@atlaskit/button';
import styled from 'styled-components';
import { Redirect } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';
import Lozenge from '@atlaskit/lozenge';
import SectionMessage from '@atlaskit/section-message';
import Spinner from '@atlaskit/spinner';

import { migration, MigrationReadyStatus } from '../api/migration';
import { ErrorFlag } from './shared/ErrorFlag';
import { awsAuthPath } from '../utils/RoutePaths';

type HomeProps = {
    title: string;
    synopsis: string;
    startButtonText: string;
};

type ReadyProps = {
    ready: MigrationReadyStatus;
};

const HomeContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
    margin-right: auto;
    margin-bottom: auto;
    padding-left: 15px;
    max-width: 920px;
`;

const MigrationsContainer = styled.div`
    margin-top: 20px;
`;

const InfoProps = {
    title: I18n.getText('atlassian.migration.datacenter.home.info.title'),
};

type ActionSectionProps = {
    startButtonText: string;
};

const MigrationActionSection: FunctionComponent<ActionSectionProps> = ({ startButtonText }) => {
    const [error, setError] = useState<string>();
    const [loading, setLoading] = useState<boolean>(false);
    const [readyForNextStep, setReadyForNextStep] = useState<boolean>(false);
    const [ready, setReady] = useState<MigrationReadyStatus>();

    useEffect(() => {
        migration.getReadyStatus().then(status => {
            setReady(status);
        });
    }, []);

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

    return (
        <>
            <ErrorFlag
                showError={error && error !== ''}
                dismissErrorFunc={(): void => setError('')}
                title={I18n.getText('atlassian.migration.datacenter.home.start.error')}
                description={error}
                id="migration-creation-error"
            />
            <p>
                <ReadyStatus ready={ready} />
            </p>
            <MigrationsContainer>
                <Button
                    isDisabled={!ready?.pgDumpAvailable || !ready?.pgDumpCompatible}
                    isLoading={loading}
                    appearance="primary"
                    onClick={createMigration}
                    data-test="start-migration"
                >
                    {startButtonText}
                </Button>
            </MigrationsContainer>
        </>
    );
};

export const ReadyStatus: FunctionComponent<ReadyProps> = ({ ready }) => {
    const readyString = (state: boolean | undefined): ReactNode => {
        return state === undefined ? (
            <Spinner size="small" />
        ) : state ? (
            <Lozenge appearance="success">OK</Lozenge>
        ) : (
            <Lozenge appearance="removed">Incompatible</Lozenge>
        );
    };

    return (
        <SectionMessage appearance="info" {...InfoProps}>
            <ul>
                <li>
                    uses <strong>PostgreSQL</strong> database &nbsp;{' '}
                    {readyString(ready?.dbCompatible)}
                </li>
                <li>
                    is running on <strong>Linux</strong> &nbsp; {readyString(ready?.osCompatible)}
                </li>
                <li>
                    <strong>pg_dump</strong> utility is available &nbsp;{' '}
                    {readyString(ready?.pgDumpAvailable)}
                </li>
                <li>
                    <strong>pg_dump</strong> utility is compatible with the server version &nbsp;
                    &nbsp; {readyString(ready?.pgDumpCompatible)}
                </li>
                <li>
                    is using a&nbsp;
                    <a
                        target="_blank"
                        rel="noreferrer noopener"
                        href="https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-dclicense"
                    >
                        Data Center license
                    </a>
                </li>
            </ul>
        </SectionMessage>
    );
};

export const Home = ({ title, synopsis, startButtonText }: HomeProps): ReactElement => {
    return (
        <HomeContainer>
            <h1>{title}</h1>
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
            <MigrationActionSection startButtonText={startButtonText} />
        </HomeContainer>
    );
};
