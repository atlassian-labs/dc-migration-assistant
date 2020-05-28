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
import styled from 'styled-components';
import { ButtonGroup } from '@atlaskit/button';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { Redirect } from 'react-router-dom';
import { ExistingASIConfiguration, ASISelector } from './ExistingASIConfiguration';
import { I18n } from '../../../../atlassian/mocks/@atlassian/wrm-react-i18n';
import { CancelButton } from '../../../shared/CancelButton';
import { quickstartPath } from '../../../../utils/RoutePaths';

type ASIConfigurationProps = {
    ASIExists: boolean;
    updateASIPrefix: (prefix: string) => void;
};

const ContentContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 920px;
    margin-right: auto;
    margin-bottom: auto;
    padding-left: 15px;
`;

const Description = styled.p`
    width: 90%;
    margin-bottom: 15px;
`;

const ButtonRow = styled.div`
    margin: 30px 0px 0px 0px;
`;

export const ASIConfiguration: FunctionComponent<ASIConfigurationProps> = ({
    ASIExists,
    updateASIPrefix,
}) => {
    const [prefix, setPrefix] = useState<string>('');
    const [readyToTransition, setReadyToTransition] = useState<boolean>(false);

    const handleSubmit = (): void => {
        updateASIPrefix(prefix);
        setReadyToTransition(true);
    };

    const updatePRefix = (newPrefix: string): void => setPrefix(newPrefix);

    if (readyToTransition) {
        return <Redirect to={quickstartPath} push />;
    }

    return (
        <ContentContainer>
            <h1>{I18n.getText('atlassian.migration.datacenter.provision.aws.asi.title')}</h1>
            <Description>
                {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.description')}{' '}
                <a href="https://aws.amazon.com/quickstart/architecture/atlassian-standard-infrastructure/">
                    {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                </a>
            </Description>

            {ASIExists ? (
                <ExistingASIConfiguration handleASIPrefixSet={updatePRefix} />
            ) : (
                <ASISelector useExisting={false} handlePrefixUpdated={updatePRefix} />
            )}
            <ButtonRow>
                <ButtonGroup>
                    <Button
                        onClick={handleSubmit}
                        type="submit"
                        appearance="primary"
                        isDisabled={prefix?.length === 0}
                        data-test="asi-submit"
                    >
                        {I18n.getText('atlassian.migration.datacenter.generic.next')}
                    </Button>
                    <CancelButton />
                </ButtonGroup>
            </ButtonRow>
        </ContentContainer>
    );
};
