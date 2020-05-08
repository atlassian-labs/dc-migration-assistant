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

const CancelModalContainer = styled.div``;

export const CancelModal: FunctionComponent = () => {
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

    const actions = [
        { text: 'Cancel Migration', onClick: console.log },
        { text: 'Close', onClick: console.log },
    ];
    const closeModal = () => {
        setIsModalOpen(false);
    };

    return (
        <CancelModalContainer>
            <ModalTransition>
                {isModalOpen && (
                    <Modal actions={actions} onClose={closeModal} heading="Modal Title" />
                )}
            </ModalTransition>
        </CancelModalContainer>
    );
};
