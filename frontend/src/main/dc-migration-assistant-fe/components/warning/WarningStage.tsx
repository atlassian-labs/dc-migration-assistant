import React, { FunctionComponent, useState } from 'react';
import SectionMessage from '@atlaskit/section-message';
import { Checkbox } from '@atlaskit/checkbox';
import Button from '@atlaskit/button';
import { Redirect } from 'react-router-dom';
import { dbPath } from '../../utils/RoutePaths';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';

export const WarningStagePage: FunctionComponent = () => {
    const [agreed, setAgreed] = useState<boolean>(false);
    const [shouldRedirect, setShouldRedirect] = useState<boolean>(false);

    const heading = I18n.getText('atlassian.migration.datacenter.db.title');
    const description = I18n.getText('atlassian.migration.datacenter.db.description');

    const handleClick = (): void => {
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

    return (
        <div>
            <h1>{heading}</h1>
            <p>{description}</p>
            <SectionMessage appearance="info" title="To take your Jira instance offline:">
                <ol>
                    <li>Make sure that users are logged out</li>
                    <li>Redirect the DNS to a maintenance page</li>
                </ol>
            </SectionMessage>
            <Checkbox
                value="agree"
                label="I'm ready for the next step"
                onChange={agreeOnClick}
                name="agree"
            />
            <Button isDisabled={!agreed} onClick={handleClick}>
                Continue
            </Button>
        </div>
    );
};
