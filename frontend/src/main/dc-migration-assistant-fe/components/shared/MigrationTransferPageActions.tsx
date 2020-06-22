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

import React, { FunctionComponent, ReactFragment, useState } from 'react';
import Button, { ButtonGroup } from '@atlaskit/button';
import { Link, Redirect } from 'react-router-dom';
import styled from 'styled-components';
import { Checkbox } from '@atlaskit/checkbox';
import { CancelButton } from './CancelButton';
import { RetryCallback } from './MigrationTransferPage';
import { I18n } from '@atlassian/wrm-react-i18n';

export type MigrationTransferActionsProps = {
    /**
     * A function which will be called when the Refresh button is clicked
     */
    onRefresh: () => Promise<void>;
    /**
     * Whether the current migration transfer has completed or not
     */
    finished: boolean;
    /**
     * The text to display on the button that takes the user to the next step of the migration
     */
    nextText: string;
    /**
     * The text to display on the start button that initiates the migration transfer. Defaults to `Start`
     */
    startButtonText?: string;
    /**
     * The path to take the user to whent hey click the "next" action button
     */
    nextRoute: string;
    /**
     * A function which will be called when the "start" action button is called.
     */
    startMigrationPhase: () => Promise<void>;
    /**
     * Whether the current migration transfer has already started or not
     */
    started: boolean;
    /**
     * Should be true if any of the above properties are being fetched
     */
    loading: boolean;

    failed?: boolean;
    retryText?: string;
    onRetry?: RetryCallback;
    onRetryRoute?: string;
};

const CheckboxContainer = styled.div`
    display: flex;
    justify-content: flex-start;
    margin-top: 10px;
`;

const PageActionContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
`;

const ButtonRow = styled.div`
    margin: 15px 0px 0px 0px;
`;

/**
 * A component which renders the actions that can be taken at a given step of the migration.
 * The "Cancel" action is always available and cancels the current step. There are three other
 * possible actions which can be availabl depending on the props:
 *   **Start**: Will be available when the current transfer hasn't started
 *   **Refresh**: Will be available when the current transfer is in progress
 *   **Next**: Will be available when the current transfer is completed. It's text can be overriden
 */
export const MigrationTransferActions: FunctionComponent<MigrationTransferActionsProps> = ({
    finished,
    nextText,
    nextRoute,
    startButtonText,
    startMigrationPhase,
    onRefresh: updateTransferProgress,
    started,
    loading,
    failed,
    onRetry,
    onRetryRoute,
    retryText,
}) => {
    const [shouldRedirectToPhaseStart, setShouldRedirectToPhaseStart] = useState<boolean>(false);
    const [retryable, setRetryable] = useState<boolean>(false);

    const defaultButtonStyle = {
        padding: '5px',
        display: 'flex',
    };

    const marginButtonStyle = {
        ...defaultButtonStyle,
        marginRight: '5px',
    };

    const StartButton = (
        <Button
            style={marginButtonStyle}
            isLoading={loading}
            appearance="primary"
            onClick={startMigrationPhase}
        >
            {startButtonText ?? 'Start'}
        </Button>
    );

    let ActionButton = StartButton;

    if (finished) {
        const NextButton = (
            <Link to={nextRoute}>
                <Button style={defaultButtonStyle} appearance="primary">
                    {nextText}
                </Button>
            </Link>
        );
        ActionButton = NextButton;
    } else if (failed && retryText) {
        ActionButton = (
            <Button
                style={marginButtonStyle}
                isLoading={loading}
                isDisabled={!retryable}
                onClick={(): void => {
                    onRetry().then(() => setShouldRedirectToPhaseStart(true));
                }}
            >
                {retryText}
            </Button>
        );
    } else if (started) {
        const RefreshButton = (
            <Button style={marginButtonStyle} isLoading={loading} onClick={updateTransferProgress}>
                Refresh
            </Button>
        );
        ActionButton = RefreshButton;
    }

    const PageActionButtonGroup: ReactFragment = (
        <PageActionContainer>
            {retryText && failed && (
                <CheckboxContainer>
                    <Checkbox
                        value="true"
                        label={I18n.getText(
                            'atlassian.migration.datacenter.common.aws.retry.checkbox.text'
                        )}
                        onChange={(event: any): void => {
                            setRetryable(event.target.checked);
                        }}
                        name="retryAgree"
                    />
                </CheckboxContainer>
            )}
            <ButtonRow>
                <ButtonGroup>
                    {ActionButton}
                    <CancelButton />
                </ButtonGroup>
            </ButtonRow>
        </PageActionContainer>
    );

    return (
        <>
            {shouldRedirectToPhaseStart && <Redirect to={onRetryRoute} push />}
            {PageActionButtonGroup}
        </>
    );
};
