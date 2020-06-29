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
import React, { FunctionComponent, useState, useEffect, ReactNode } from 'react';
import styled from 'styled-components';
import { ButtonGroup } from '@atlaskit/button';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { Redirect } from 'react-router-dom';
import SectionMessage from '@atlaskit/section-message';
import { ExistingASIConfiguration, ASIDescription } from './ExistingASIConfiguration';
import { I18n } from '../../../../atlassian/mocks/@atlassian/wrm-react-i18n';
import { CancelButton } from '../../../shared/CancelButton';
import { quickstartPath } from '../../../../utils/RoutePaths';
import { provisioning } from '../../../../api/provisioning';
import { DeploymentMode } from '../QuickstartRoutes';

type ASIConfigurationProps = {
    updateASIPrefix: (prefix: string) => void;
    onSelectDeploymentMode: (mode: DeploymentMode) => void;
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

const SectionMessageContainer = styled.div`
    margin-top: 20px;
    width: 70%;
`;

export const ASIConfiguration: FunctionComponent<ASIConfigurationProps> = ({
    updateASIPrefix,
    onSelectDeploymentMode,
}) => {
    const [prefix, setPrefix] = useState<string>('');
    const [readyToTransition, setReadyToTransition] = useState<boolean>(false);
    const [existingASIPrefixes, setExistingASIPrefixes] = useState<Array<ASIDescription>>([]);
    const [loadingPrefixes, setLoadingPrefixes] = useState<boolean>(false);

    useEffect(() => {
        setLoadingPrefixes(true);
        provisioning
            .getASIs()
            .then(asis => {
                setExistingASIPrefixes(asis);
            })
            .finally(() => setLoadingPrefixes(false));
    }, []);

    const handleSubmit = (): void => {
        updateASIPrefix(prefix);
        setReadyToTransition(true);
    };

    const updatePRefix = (newPrefix: string): void => setPrefix(newPrefix);

    if (readyToTransition) {
        return <Redirect to={quickstartPath} push />;
    }

    const renderASISelector = (): ReactNode => {
        return existingASIPrefixes.length !== 0 ? (
            <ExistingASIConfiguration
                handlePrefixUpdated={updatePRefix}
                existingASIs={existingASIPrefixes}
                onSelectDeploymentMode={onSelectDeploymentMode}
            />
        ) : (
            <SectionMessage>
                <p>{I18n.getText('atlassian.migration.datacenter.provision.aws.asi.none')}</p>
            </SectionMessage>
        );
    };

    return (
        <ContentContainer>
            <h1>{I18n.getText('atlassian.migration.datacenter.provision.aws.asi.title')}</h1>
            <Description>
                {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.description')}{' '}
                <a href="https://aws.amazon.com/quickstart/architecture/atlassian-standard-infrastructure/">
                    {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                </a>
            </Description>

            {loadingPrefixes ? (
                <SectionMessageContainer>
                    <SectionMessage appearance="info">
                        <p>
                            {I18n.getText(
                                'atlassian.migration.datacenter.provision.aws.asi.scanning'
                            )}
                        </p>
                    </SectionMessage>
                </SectionMessageContainer>
            ) : (
                renderASISelector()
            )}

            <ButtonRow>
                <ButtonGroup>
                    <Button
                        onClick={handleSubmit}
                        type="submit"
                        appearance="primary"
                        isDisabled={existingASIPrefixes?.length !== 0 && prefix?.length === 0}
                        data-test="asi-submit"
                        isLoading={loadingPrefixes}
                    >
                        {I18n.getText('atlassian.migration.datacenter.generic.next')}
                    </Button>
                    <CancelButton />
                </ButtonGroup>
            </ButtonRow>
        </ContentContainer>
    );
};
