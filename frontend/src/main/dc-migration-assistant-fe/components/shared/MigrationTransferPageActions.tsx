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

import React, { ReactNode, FunctionComponent } from 'react';
import Button, { ButtonGroup } from '@atlaskit/button';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';
import { CancelButton } from './CancelButton';

export type MigrationStepState = 'not_started' | 'in_progress' | 'finished';

export type MigrationTransferActionsProps = {
    /**
     * The state of the migration step. If not_started, the action will be a start button. If in_progress, the action
     * will be a refresh button. If finished, the action will be a link to the next step.
     */
    state: MigrationStepState;
    /**
     * A function which will be called when the Refresh button is clicked
     */
    onRefresh: () => Promise<void>;
    /**
     * The text to display on the button that takes the user to the next step of the migration
     */
    nextText: string;
    /**
     * The text to display on the start button that initiates the migration transfer. Defaults to `Start`
     */
    startButtonText: string;
    /**
     * The path to take the user to whent hey click the "next" action button
     */
    nextRoute: string;
    /**
     * A function which will be called when the "start" action button is called.
     */
    startMigrationPhase: () => Promise<void>;
    /**
     * Should be true if any of the above properties are being fetched
     */
    loading: boolean;
};

const PageActionContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
`;

const ButtonRow = styled.div`
    margin: 15px 0px 0px 0px;
`;

const ActionButton: FunctionComponent<{
    loading: boolean;
    onClick?: (e: React.MouseEvent<HTMLElement, MouseEvent>) => void;
    text: string;
    isRefresh?: boolean;
    testId?: string;
}> = ({ loading, onClick, text, isRefresh, testId }) => (
    <Button
        style={{
            padding: '5px',
        }}
        onClick={onClick || ((): void => undefined)}
        isLoading={loading}
        appearance={isRefresh ? 'default' : 'primary'}
        testId={testId || `button-${text.replace(/\W/g, '-').toLowerCase()}`}
    >
        {text}
    </Button>
);

/**
 * A component which renders the actions that can be taken at a given step of the migration.
 * The "Cancel" action is always available and cancels the current step. There are three other
 * possible actions which can be availabl depending on the props:
 *   **Start**: Will be available when the current transfer hasn't started
 *   **Refresh**: Will be available when the current transfer is in progress
 *   **Next**: Will be available when the current transfer is completed. It's text can be overriden
 */
export const MigrationTransferActions: FunctionComponent<MigrationTransferActionsProps> = ({
    state,
    nextText,
    nextRoute,
    startButtonText,
    startMigrationPhase,
    onRefresh,
    loading,
}) => {
    let CurrentStepAction: ReactNode;

    if (state === 'not_started') {
        CurrentStepAction = (
            <ActionButton onClick={startMigrationPhase} loading={loading} text={startButtonText} />
        );
    } else if (state === 'in_progress') {
        CurrentStepAction = (
            <ActionButton
                loading={loading}
                onClick={onRefresh}
                isRefresh
                text={I18n.getText('atlassian.migration.datacenter.generic.refresh')}
            />
        );
    } else {
        CurrentStepAction = (
            <Link to={nextRoute}>
                <ActionButton text={nextText} loading={loading} />
            </Link>
        );
    }

    return (
        <PageActionContainer>
            <ButtonRow>
                <ButtonGroup>
                    {CurrentStepAction}
                    <CancelButton />
                </ButtonGroup>
            </ButtonRow>
        </PageActionContainer>
    );
};
