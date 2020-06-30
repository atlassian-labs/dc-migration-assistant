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

import styled from 'styled-components';
import React, { FunctionComponent, ReactElement } from 'react';
import Flag from '@atlaskit/flag';
import ErrorIcon from '@atlaskit/icon/glyph/error';
import { colors } from '@atlaskit/theme';

type ErrorFlagProps = {
    /**
     * whether the flag should be rendered or not
     */
    showError: boolean;
    /**
     * callback that will be invoked when the "dimiss" button on the flag is clicked
     */
    dismissErrorFunc: () => void;

    /**
     * Title for the flag. Should describe the error
     */
    title: string;
    /**
     * Text that appears below the flag. Should be a call to action on how to resolve the error
     */
    description: string;
    /**
     * Unique identifier for the error
     */
    id: string;
};

const ErrorContainer = styled.div`
    position: fixed;
    top: 70px;
    left: 75%;
    right: 1%;
    overflow: inherit;
    z-index: 1;
`;

export const ErrorFlag: FunctionComponent<ErrorFlagProps> = ({
    showError,
    dismissErrorFunc,
    title,
    description,
    id,
}): ReactElement => {
    if (showError) {
        return (
            <ErrorContainer>
                <Flag
                    actions={[
                        {
                            content: 'Dismiss',
                            onClick: dismissErrorFunc,
                        },
                    ]}
                    icon={<ErrorIcon primaryColor={colors.R400} label="Info" />}
                    description={description}
                    id={id}
                    key={id}
                    title={title}
                />
            </ErrorContainer>
        );
    }
    return null;
};
