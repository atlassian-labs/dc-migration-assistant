import React, { FunctionComponent, useState } from 'react';
import Button from '@atlaskit/button';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';
import { CancelModal } from './CancelModal';

export const cancelButtonStyle = {
    paddingLeft: '5px',
    marginLeft: '20px',
};

export const CancelButton: FunctionComponent = () => {
    const [showCancelMigrationModal, setShowCancelMigrationModal] = useState<boolean>(false);

    return (
        <>
            <CancelModal
                modalState={showCancelMigrationModal}
                toggleModalDisplay={setShowCancelMigrationModal}
            />
            <Button
                style={cancelButtonStyle}
                appearance="default"
                onClick={(): void => {
                    setShowCancelMigrationModal(true);
                }}
            >
                {I18n.getText('atlassian.migration.datacenter.generic.cancel')}
            </Button>
        </>
    );
};
