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
import { Button } from '@atlaskit/button/dist/esm/components/Button';
import styled from 'styled-components';
import { Redirect } from 'react-router-dom';
import { Checkbox } from '@atlaskit/checkbox';
import { homePath } from '../../utils/RoutePaths';
import { migration, MigrationStage } from '../../api/migration';
import { provisioning } from '../../api/provisioning';
import { ErrorFlag } from '../shared/ErrorFlag';

const ValidationRoot = styled.div`
    max-width: 920px;
`;

const MigrationSummaryContainer = styled.div`
    display: grid;
    justify-items: start;
    & li {
        align-self: center;
    }
    & div {
        padding-bottom: 5px;
    }
`;

const SectionMessageContainer = styled.div`
    margin-top: 15px;
    width: 100%;
`;

const CheckBoxContainer = styled.div`
    margin: 5px 0 0 -5px;
`;

const LearnMoreLink =
    'https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-Additionalresources';

const MigrationSummaryActionCallout = (): ReactElement => {
    return (
        <MigrationSummaryContainer>
            <div>
                <ul>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.validation.post.action.aws.login'
                        )}
                    </li>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.validation.post.action.index'
                        )}
                    </li>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.validation.post.action.reconnect.external.services'
                        )}
                    </li>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.validation.post.action.aws.verify'
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

const ValidationSummary: FunctionComponent = () => {
    const [areActionsAcknowledged, setAreActionsAcknowledged] = useState<boolean>(false);
    const [isFinishMigrationSuccess, setIsFinishMigrationSuccess] = useState<boolean>(false);
    const [targetUrl, setTargetUrl] = useState<string>('');

    useEffect(() => {
        migration.getMigrationSummary().then(summary => {
            setTargetUrl(summary.instanceUrl);
        });
    });

    const [finishMigrationApiErrorMessage, setFinishMigrationApiErrorMessage] = useState<string>(
        ''
    );

    if (isFinishMigrationSuccess) {
        return <Redirect to={homePath} push />;
    }

    return (
        <>
            <ErrorFlag
                showError={finishMigrationApiErrorMessage && finishMigrationApiErrorMessage !== ''}
                dismissErrorFunc={(): void => setFinishMigrationApiErrorMessage('')}
                title={I18n.getText('atlassian.migration.datacenter.validation.finish.api.error')}
                description={I18n.getText(
                    'atlassian.migration.datacenter.validation.finish.api.error.description'
                )}
                id="finish-api-error"
            />
            <h3>{I18n.getText('atlassian.migration.datacenter.step.validation.phrase')}</h3>
            <p>
                {I18n.getText('atlassian.migration.datacenter.validation.message')}
                <br />
                <a href={targetUrl}>{targetUrl}</a>
                <br />
                {I18n.getText('atlassian.migration.datacenter.validation.slow.start.message')}
            </p>
            <SectionMessageContainer>
                <SectionMessage
                    appearance="info"
                    title={I18n.getText(
                        'atlassian.migration.datacenter.validation.actions.required'
                    )}
                >
                    <MigrationSummaryActionCallout />
                </SectionMessage>
            </SectionMessageContainer>
            <CheckBoxContainer>
                <Checkbox
                    isChecked={areActionsAcknowledged}
                    onChange={(evnt): void => setAreActionsAcknowledged(evnt.target.checked)}
                    label={I18n.getText('atlassian.migration.datacenter.validation.next.checkbox')}
                />
            </CheckBoxContainer>
            <Button
                isLoading={false}
                appearance="primary"
                style={{
                    marginTop: '15px',
                }}
                isDisabled={!areActionsAcknowledged || targetUrl === ''}
                onClick={(): void => {
                    provisioning
                        .cleanupInfrastructure()
                        .then(() => migration.finishMigration())
                        .then(() => {
                            setIsFinishMigrationSuccess(true);
                            window.open(targetUrl);
                        })
                        .catch(response => {
                            setFinishMigrationApiErrorMessage(response.message);
                        });
                }}
            >
                {I18n.getText('atlassian.migration.datacenter.validation.next.button')}
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

    return (
        <ValidationRoot>
            {isStageValid ? <ValidationSummary /> : <InvalidMigrationStageErrorMessage />}
        </ValidationRoot>
    );
};
