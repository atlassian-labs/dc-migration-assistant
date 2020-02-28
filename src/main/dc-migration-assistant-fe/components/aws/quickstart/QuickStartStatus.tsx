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
    const renderProvisioningStatus = (status: string) => {
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

        return () => {
            clearInterval(intervalId);
        };
    }, []);

    return (
        <QuickStartStatusContainer>
            {inProgress ? <Spinner /> : stageStatusFlag(provisioningStatus)}
        </QuickStartStatusContainer>
    );
};
