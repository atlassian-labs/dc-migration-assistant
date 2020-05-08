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
import Modal, { ModalTransition } from '@atlaskit/modal-dialog';
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';

const CancelModalContainer = styled.div``;
const CancelModalContentContainer = styled.div``;

const LearnMoreLink =
    'https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html?#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-errors';

export const CancelModal: FunctionComponent = () => {
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

    const closeModal = (): void => {
        setIsModalOpen(false);
    };

    const actions: Array<any> = [
        {
            text: I18n.getText('atlassian.migration.datacenter.generic.nevermind'),
            onClick: closeModal,
            appearance: 'default',
        },
        {
            text: I18n.getText('atlassian.migration.datacenter.generic.cancel_migration.question'),
            onClick: console.log,
            appearance: 'warning',
        },
    ];

    return (
        <CancelModalContainer>
            <ModalTransition>
                {isModalOpen && (
                    <Modal
                        actions={actions}
                        onClose={closeModal}
                        appearance="warning"
                        heading="Cancel Migration?"
                    >
                        <CancelModalContentContainer>
                            <p>
                                {I18n.getText(
                                    'atlassian.migration.datacenter.cancellation.modal.progress.warning'
                                )}
                            </p>
                            <p>
                                {I18n.getText(
                                    'atlassian.migration.datacenter.cancellation.modal.aws.resource.cleanup.warning'
                                )}
                            </p>
                            <p>
                                <a target="_blank" rel="noreferrer noopener" href={LearnMoreLink}>
                                    {I18n.getText(
                                        'atlassian.migration.datacenter.common.learn_more'
                                    )}
                                </a>
                            </p>
                        </CancelModalContentContainer>
                    </Modal>
                )}
            </ModalTransition>
        </CancelModalContainer>
    );
};
