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
import { I18n } from '@atlassian/wrm-react-i18n';
import SectionMessage from '@atlaskit/section-message';
import TableTree, { Cell, Row } from '@atlaskit/table-tree';
import { Button } from '@atlaskit/button/dist/esm/components/Button';
import styled from 'styled-components';
import { Redirect } from 'react-router-dom';
import { homePath } from '../../utils/RoutePaths';
import { migration, MigrationStage } from '../../api/migration';
import { provisioning } from '../../api/provisioning';
import { ErrorFlag } from '../shared/ErrorFlag';

const MigrationSummaryContainer = styled.div`
    display: grid;
    justify-items: start;
    margin-top: 25px;
    & li {
        align-self: center;
    }
    & div {
        padding-bottom: 5px;
    }
`;

const MigrationDetailsContainer = styled.div`
    margin-top: 25px;
`;

const SectionMessageContainer = styled.div`
    margin-top: 15px;
    width: 100%;
`;

const LearnMoreLink =
    'https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-Additionalresources';

const MigrationSummaryActionCallout = (): ReactElement => {
    return (
        <MigrationSummaryContainer>
            <div>
                <h4>
                    {I18n.getText('atlassian.migration.datacenter.validation.actions.required')}
                </h4>
            </div>
            <div>
                <ul>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.validation.post.action.aws.login'
                        )}
                    </li>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.validation.post.action.reconnect.external.services'
                        )}
                    </li>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.validation.post.action.aws.test'
                        )}
                    </li>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.validation.post.action.redirect.dns'
                        )}
                    </li>
                </ul>
            </div>
            <div>
                <a target="_blank" rel="noreferrer noopener" href={LearnMoreLink}>
                    {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                </a>
            </div>
        </MigrationSummaryContainer>
    );
};

type MigrationSummaryData = {
    key: string;
    value: string;
};

const INSTANCE_URL_MIGRATION_CONTEXT_KEY = 'instanceUrl';

const MigrationSummary: FunctionComponent = () => {
    const [summaryData, setSummaryData]: [Array<MigrationSummaryData>, Function] = useState<
        Array<MigrationSummaryData>
    >([]);

    useEffect(() => {
        migration
            .getMigrationSummary()
            .then(data => {
                setSummaryData(
                    Object.entries(data).map(summary => {
                        const [key, value] = summary;
                        return { key, value };
                    })
                );
            })
            .catch(() => {
                setSummaryData([]);
            });
    }, []);

    return (
        <MigrationDetailsContainer>
            <div>
                <h4>Migration details</h4>
            </div>
            <TableTree>
                {summaryData
                    .filter(x => x.key === INSTANCE_URL_MIGRATION_CONTEXT_KEY)
                    .map(summary => {
                        return (
                            <Row key={`migration-summary-${summary.key}`} hasChildren={false}>
                                <Cell width={400} singleLine>
                                    {I18n.getText(
                                        'atlassian.migration.datacenter.validation.summary.phrase.instanceUrl'
                                    )}
                                </Cell>
                                <Cell width={400}>
                                    <a
                                        target="_blank"
                                        rel="noreferrer noopener"
                                        href={summary.value}
                                        style={{ whiteSpace: 'nowrap' }}
                                    >
                                        {summary.value}
                                    </a>
                                </Cell>
                            </Row>
                        );
                    })}
            </TableTree>
        </MigrationDetailsContainer>
    );
};

const ValidationSummary: FunctionComponent = () => {
    const [isFinishMigrationSuccess, setIsFinishMigrationSuccess] = useState<boolean>(false);

    const [finishMigrationApiErrorMessage, setFinishMigrationApiErrorMessage] = useState<string>(
        ''
    );

    if (isFinishMigrationSuccess) {
        return <Redirect to={homePath} push />;
    }

    return (
        <>
            <ErrorFlag
                showError={finishMigrationApiErrorMessage !== ''}
                dismissErrorFunc={(): void => setFinishMigrationApiErrorMessage('')}
                title={I18n.getText('atlassian.migration.datacenter.validation.finish.api.error')}
                description="This only impacts the source instance. Please check the logs for the exact cause."
                id="finish-api-error"
            />
            <h3>{I18n.getText('atlassian.migration.datacenter.step.validation.phrase')}</h3>
            <h3>{I18n.getText('atlassian.migration.datacenter.validation.message')}</h3>
            <SectionMessageContainer>
                <SectionMessage appearance="info">
                    {I18n.getText('atlassian.migration.datacenter.validation.section.message')}
                </SectionMessage>
            </SectionMessageContainer>
            <MigrationSummary />
            <MigrationSummaryActionCallout />
            <Button
                isLoading={false}
                appearance="primary"
                style={{
                    marginTop: '15px',
                }}
                onClick={(): void => {
                    migration
                        .finishMigration()
                        .then(() => setIsFinishMigrationSuccess(true))
                        .catch(response => {
                            setFinishMigrationApiErrorMessage(response.message);
                        });
                }}
            >
                {I18n.getText('atlassian.migration.datacenter.validation.next.button')}
            </Button>
            <Button
                appearance="warning"
                onClick={(): any => provisioning.cleanupInfrastructure()}
                style={{
                    marginTop: '15px',
                    marginLeft: '5px',
                }}
            >
                Cleanup migration infrastructure
            </Button>
        </>
    );
};

const InvalidMigrationStageErrorMessage = (): ReactElement => (
    <SectionMessage
        appearance="error"
        actions={[
            {
                key: 'invalid-stage-error-section-link',
                href: homePath,
                text: I18n.getText('atlassian.migration.datacenter.step.validation.redirect.home'),
            },
        ]}
    >
        <p>
            {I18n.getText(
                'atlassian.migration.datacenter.step.validation.incorrect.stage.error.title'
            )}
        </p>
        <p>
            {I18n.getText(
                'atlassian.migration.datacenter.step.validation.incorrect.stage.error.description'
            )}
        </p>
    </SectionMessage>
);

export const ValidateStagePage: FunctionComponent = () => {
    const [isStageValid, setIsStageValid]: [boolean, Function] = useState<boolean>(true);

    useEffect(() => {
        migration
            .getMigrationStage()
            .then(stage => {
                if (stage !== MigrationStage.VALIDATE) {
                    setIsStageValid(false);
                }
            })
            .catch(() => {
                setIsStageValid(false);
            });
    }, []);

    return isStageValid ? <ValidationSummary /> : <InvalidMigrationStageErrorMessage />;
};
