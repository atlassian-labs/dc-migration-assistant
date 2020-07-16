import React, { FunctionComponent, useState } from 'react';
import styled from 'styled-components';
import { Checkbox } from '@atlaskit/checkbox';
import { I18n } from '@atlassian/wrm-react-i18n';
import Button from '@atlaskit/button';
import { Redirect } from 'react-router-dom';

import { warningPath } from '../../utils/RoutePaths';
import { RetryProperties } from './MigrationProcess';

const CheckboxContainer = styled.div`
    display: flex;
    justify-content: flex-start;
    margin-top: 10px;
`;

export const RetryMenu: FunctionComponent<RetryProperties> = ({
    canContinueOnFailure,
    onRetry,
    onRetryRoute,
    retryText,
}) => {
    const [retryEnabled, setRetryEnabled] = useState<boolean>(false);
    const [shouldRedirectToStartToRetry, setShouldRedirectToStartToRetry] = useState<boolean>(
        false
    );

    if (shouldRedirectToStartToRetry) {
        return <Redirect to={onRetryRoute} push />;
    }

    return (
        <>
            <CheckboxContainer>
                <Checkbox
                    value="true"
                    label={I18n.getText(
                        'atlassian.migration.datacenter.common.aws.retry.checkbox.text'
                    )}
                    onChange={(event: any): void => {
                        setRetryEnabled(event.target.checked);
                    }}
                    name="retryAgree"
                />
            </CheckboxContainer>
            <Button
                style={{ marginTop: '10px' }}
                isDisabled={!retryEnabled}
                onClick={(): void => {
                    onRetry().then(() => setShouldRedirectToStartToRetry(onRetryRoute && true));
                }}
            >
                {retryText || 'retry'}
            </Button>
            {canContinueOnFailure && (
                <Button
                    appearance="subtle-link"
                    href={warningPath}
                    style={{
                        marginTop: '10px',
                        marginLeft: '10px',
                    }}
                >
                    {I18n.getText('atlassian.migration.datacenter.fs.continue')}
                </Button>
            )}
        </>
    );
};