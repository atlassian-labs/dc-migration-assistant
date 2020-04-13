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
import ProgressBar, { SuccessProgressBar } from '@atlaskit/progress-bar';
import SectionMessage from '@atlaskit/section-message';
import styled from 'styled-components';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { Link } from 'react-router-dom';
import { overviewPath } from '../../utils/RoutePaths';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';
import moment from 'moment';

type Action = {
    text: React.ReactNode;
    onClick?: () => void;
    href?: string;
    key: string;
    testId?: string;
};

export type MigrationTransferProps = {
    heading: string;
    description: string;
    infoTitle: string;
    infoContent: string;
    infoActions?: Action[];
    nextText: string;
    started: moment.Moment;
};

const TransferPageContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 25%;
    margin-right: auto;
    margin-left: auto;
    margin-bottom: auto;
`;

const TransferContentContainer = styled.div`
    display: flex;
    flex-direction: column;

    padding-bottom: 20px;
    border-bottom: 2px solid gray;
`;

const TransferActionsContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: space-between;

    margin-top: 20px;
`;

export const MigrationTransferPage: FunctionComponent<MigrationTransferProps> = ({
    description,
    heading,
    infoContent,
    infoTitle,
    infoActions,
    nextText,
    started,
}) => {
    const elapsedTime = moment.duration(moment.now() - started.valueOf());
    const elapsedDays = elapsedTime.days();
    const elapsedHours = elapsedTime.hours();
    const elapsedMins = elapsedTime.minutes();

    return (
        <TransferPageContainer>
            <TransferContentContainer>
                <h1>{heading}</h1>
                <p>{description}</p>
                <SectionMessage title={infoTitle} actions={infoActions || []}>
                    {infoContent}
                </SectionMessage>
                <h4>Phase of copying</h4>
                <ProgressBar isIndeterminate />
                <p>
                    {I18n.getText(
                        'atlassian.migration.datacenter.common.progress.started',
                        started.format('D/MMM/YY h:m A')
                    )}
                </p>
                <p>
                    {I18n.getText(
                        'atlassian.migration.datacenter.common.progress.mins_elapsed',
                        `${elapsedDays * 24 + elapsedHours}`,
                        `${elapsedMins}`
                    )}
                </p>
                <p>45 000 files copied</p>
            </TransferContentContainer>
            <TransferActionsContainer>
                <Link to={overviewPath}>
                    <Button>{I18n.getText('atlassian.migration.datacenter.generic.cancel')}</Button>
                </Link>
                <Button appearance="primary">{nextText}</Button>
            </TransferActionsContainer>
        </TransferPageContainer>
    );
};
