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
import SectionMessage from '@atlaskit/section-message';
import { Checkbox } from '@atlaskit/checkbox';
import Button from '@atlaskit/button';
import styled from 'styled-components';
import { finalSyncPath } from '../../utils/RoutePaths';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';
import { CancelButton } from '../shared/CancelButton';

const Paragraph = styled.p`
    margin-bottom: '20px';
`;

const WarningPageContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 920px;
    margin-right: auto;
    margin-bottom: auto;
    padding-left: 15px;
`;

const WarningContentContainer = styled.div`
    display: flex;
    flex-direction: column;
    padding-right: 30px;

    padding-bottom: 5px;
`;

const WarningActionsContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: flex-start;

    margin-top: 20px;
`;

const CheckboxContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    margin-top: 10px;
    padding: 10px;
`;

const nextButtonStyle = {
    padding: '5px',
    marginRight: '5px',
};

const LearnMore: FunctionComponent = () => {
    const LearnMoreLink =
        'https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-downtimepage';

    return (
        <Paragraph>
            <a target="_blank" rel="noreferrer noopener" href={LearnMoreLink}>
                {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
            </a>
        </Paragraph>
    );
};

export const WarningStagePage: FunctionComponent = () => {
    const [agreed, setAgreed] = useState<boolean>(false);

    const agreeOnClick = (event: any): void => {
        setAgreed(event.target.checked);
    };

    const NextButton = (
        <Button
            href={finalSyncPath}
            isDisabled={!agreed}
            appearance="primary"
            style={nextButtonStyle}
        >
            {I18n.getText('atlassian.migration.datacenter.generic.next')}
        </Button>
    );

    return (
        <WarningPageContainer>
            <WarningContentContainer>
                <h1>{I18n.getText('atlassian.migration.datacenter.warning.title')}</h1>
                <Paragraph>
                    {I18n.getText('atlassian.migration.datacenter.warning.description')}
                </Paragraph>
                <LearnMore />
            </WarningContentContainer>
            <SectionMessage
                appearance="info"
                title={I18n.getText('atlassian.migration.datacenter.warning.section.header')}
            >
                <ol>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.warning.section.list.loggedOutUsers'
                        )}
                    </li>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.warning.section.list.dnsRedirection'
                        )}
                    </li>
                </ol>
            </SectionMessage>
            <CheckboxContainer>
                <Checkbox
                    value="agree"
                    label="I'm ready for the next step"
                    onChange={agreeOnClick}
                    name="agree"
                />
            </CheckboxContainer>
            <WarningActionsContainer>
                {NextButton}
                <CancelButton />
            </WarningActionsContainer>
        </WarningPageContainer>
    );
};
