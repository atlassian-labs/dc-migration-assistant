import React, { FunctionComponent, useState } from 'react';
import SectionMessage from '@atlaskit/section-message';
import { Checkbox } from '@atlaskit/checkbox';
import Button from '@atlaskit/button';
import { Redirect } from 'react-router-dom';
import styled from 'styled-components';
import { dbPath } from '../../utils/RoutePaths';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';
import { CancelButton } from '../shared/CancelButton';

const Container = styled.div`
    max-width: 920px;
`;

const Paragraph = styled.p`
    margin-bottom: '10px';
`;

const CheckboxContainer = styled.div`
    margin: '20px';
`;

const nextButtonStyle = {
    padding: '5px',
    marginRight: '20px',
};

export const WarningStagePage: FunctionComponent = () => {
    const [agreed, setAgreed] = useState<boolean>(false);
    const [shouldRedirect, setShouldRedirect] = useState<boolean>(false);

    const handleConfirmation = (): void => {
        if (agreed) {
            setShouldRedirect(true);
        }
    };

    const agreeOnClick = (event: any): void => {
        setAgreed(event.target.checked);
    };

    if (shouldRedirect) {
        return <Redirect to={dbPath} push />;
    }

    const NextButton = (
        <Button
            isDisabled={!agreed}
            onClick={handleConfirmation}
            appearance="primary"
            style={nextButtonStyle}
        >
            {I18n.getText('atlassian.migration.datacenter.generic.next')}
        </Button>
    );

    return (
        <Container>
            <h1>{I18n.getText('atlassian.migration.datacenter.warning.title')}</h1>
            <Paragraph>
                {I18n.getText('atlassian.migration.datacenter.warning.description')}
            </Paragraph>
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
            {NextButton}
            <CancelButton />
        </Container>
    );
};
