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

import React, { FunctionComponent, ReactElement, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import styled from 'styled-components';
import SuccessIcon from '@atlaskit/icon/glyph/check-circle';
import ErrorIcon from '@atlaskit/icon/glyph/error';
import Spinner from '@atlaskit/spinner';
import Flag from '@atlaskit/flag';
import { callAppRest, RestApiPathConstants } from '../../../utils/api';

const QuickStartStatusContainer = styled.div`
    display: flex;
`;

const stageStatusFlag = (currentProvisioningStatus: string): ReactElement => {
    const renderProvisioningStatus = (status: string): ReactElement => {
        if (status === 'CREATE_COMPLETE') {
            return <SuccessIcon primaryColor="#36B37E" label="Success" />;
        }
        if (status === 'CREATE_IN_PROGRESS') {
            return <SuccessIcon primaryColor="#FFC400" label="InProgress" />;
        }
        return <ErrorIcon primaryColor="#FF5630" label="Failure" />;
    };

    return (
        <Flag
            actions={[
                {
                    content: 'CloudFormation Console',
                },
            ]}
            icon={renderProvisioningStatus(currentProvisioningStatus)}
            description="All good things take time. Like your next Uber Eats delivery!"
            id="1"
            key="1"
            title="Provisioning Status"
        />
    );
};

export const QuickStartStatus: FunctionComponent = (): ReactElement => {
    const { stackId } = useParams();
    const [inProgress, setInProgress]: [boolean, Function] = useState(true);
    const [provisioningStatus, setProvisioningStatus]: [string, Function] = useState('UNKNOWN');

    const getStackStatusFromApi = (stackIdentifier: string): Promise<any> => {
        return callAppRest(
            'GET',
            RestApiPathConstants.awsStackStatusRestPath.replace(':stackId:', stackIdentifier)
        )
            .then(response => {
                if (response.status !== 200) {
                    throw Error('FAILED');
                }
                return response;
            })
            .then(r => r.text())
            .then(status => {
                if (status === 'CREATE_COMPLETE') {
                    setInProgress(false);
                    setProvisioningStatus(status);
                } else {
                    setProvisioningStatus(status);
                    setInProgress(true);
                }
            })
            .catch(err => {
                setProvisioningStatus(err.toString());
                setInProgress(false);
            });
    };

    useEffect(() => {
        const intervalId = setInterval(() => {
            getStackStatusFromApi(stackId).finally(() => clearInterval(intervalId));
        }, 5000);

        return (): void => {
            clearInterval(intervalId);
        };
    }, []);

    return (
        <QuickStartStatusContainer>
            {inProgress ? <Spinner /> : stageStatusFlag(provisioningStatus)}
        </QuickStartStatusContainer>
    );
};
