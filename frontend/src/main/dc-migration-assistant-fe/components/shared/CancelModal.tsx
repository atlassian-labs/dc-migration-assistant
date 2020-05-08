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
import { Redirect } from 'react-router-dom';
import { ButtonProps } from '@atlaskit/button';
import { migration } from '../../api/migration';
import { homePath } from '../../utils/RoutePaths';
import { ErrorFlag } from './ErrorFlag';

const CancelModalContainer = styled.div``;
const CancelModalContentContainer = styled.div``;

const LearnMoreLink =
    'https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html?#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-errors';

type CancelModalProps = {
    toggleModalDisplay: (modalState: boolean) => void;
    modalState: boolean;
};

export const CancelModal: FunctionComponent<CancelModalProps> = ({
    modalState,
    toggleModalDisplay,
}) => {
    const [redirectToNewMigration, setRedirectToNewMigration] = useState<boolean>(false);
    const [resetMigrationError, setResetMigrationError] = useState<string>('');

    const closeModal = (): void => {
        toggleModalDisplay(!modalState);
    };

    const resetMigration = (): void => {
        migration
            .resetMigration()
            .then(() => {
                setRedirectToNewMigration(true);
            })
            .catch(reason => {
                closeModal();
                setResetMigrationError(reason);
            });
    };

    const actions: Array<ButtonProps & { text: string }> = [
        {
            text: I18n.getText('atlassian.migration.datacenter.generic.nevermind'),
            onClick: closeModal,
            appearance: 'subtle',
        },
        {
            text: I18n.getText('atlassian.migration.datacenter.generic.cancel_migration'),
            onClick: resetMigration,
            appearance: 'primary',
        },
    ];

    if (redirectToNewMigration) {
        return <Redirect to={homePath} push />;
    }

    return (
        <CancelModalContainer>
            <ErrorFlag
                showError={resetMigrationError && resetMigrationError !== ''}
                dismissErrorFunc={(): void => setResetMigrationError('')}
                title={I18n.getText(
                    'atlassian.migration.datacenter.error.cancellation.failed.error'
                )}
                description={resetMigrationError}
                id="migration-reset-error"
            />
            <ModalTransition>
                {modalState && (
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
