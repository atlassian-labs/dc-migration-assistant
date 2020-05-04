import React, { FunctionComponent } from 'react';
import { Link } from 'react-router-dom';
import Button from '@atlaskit/button';
import { homePath } from '../../utils/RoutePaths';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';

export const cancelButtonStyle = {
    paddingLeft: '5px',
    marginLeft: '20px',
};

export const CancelButton: FunctionComponent = () => (
    <Link to={homePath}>
        <Button style={cancelButtonStyle}>
            {I18n.getText('atlassian.migration.datacenter.generic.cancel')}
        </Button>
    </Link>
);
