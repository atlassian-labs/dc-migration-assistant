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
import Button from '@atlaskit/button';
import { I18n } from '@atlassian/wrm-react-i18n';
import { CancelModal } from './CancelModal';

export const cancelButtonStyle = {
    paddingLeft: '5px',
    marginLeft: '5px',
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
