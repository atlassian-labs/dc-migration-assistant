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
import { RadioGroup } from '@atlaskit/radio';
import { HelperMessage } from '@atlaskit/form';
import SectionMessage from '@atlaskit/section-message';
import Select, { OptionType } from '@atlaskit/select';
import TextField from '@atlaskit/textfield';
import React, { FunctionComponent, useState, useEffect } from 'react';
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';
import { DeploymentMode } from '../QuickstartRoutes';

const EXISTING_VPC_OPTION = 'existing';
const NEW_VPC_OPTION = 'new';
const radioValues = [
    {
        name: 'deploymentMode',
        value: EXISTING_VPC_OPTION,
        label: I18n.getText('atlassian.migration.datacenter.provision.aws.asi.option.existing'),
    },
    {
        name: 'deploymentMode',
        value: NEW_VPC_OPTION,
        label: I18n.getText('atlassian.migration.datacenter.provision.aws.asi.option.new'),
    },
];

const RequiredStar = styled.span`
    color: #de350b;
`;

const ASISelectorContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%
    max-width: 500px;
    margin-right: auto;
    margin-bottom: auto;
    margin-top: 15px;
`;

type ExistingASIPrefixManagementProps = {
    handlePrefixUpdated: (prefix: string) => void;
    existingASIs: Array<ASIDescription>;
};

type ExistingASIConfigurationProps = {
    onSelectDeploymentMode: (mode: DeploymentMode) => void;
} & ExistingASIPrefixManagementProps;

export type ASIDescription = {
    prefix: string;
    stackName: string;
};

type ASISelectorProps = {
    useExisting: boolean;
} & ExistingASIPrefixManagementProps;

const ASIPrefixOptionsFromList = (ASIs: Array<ASIDescription>): Array<OptionType> => {
    if (ASIs.length === 0) return [];

    const asiToOptionValue = (asi: ASIDescription): string =>
        `${asi.prefix}   (${I18n.getText(
            'atlassian.migration.datacenter.provision.aws.asi.belongsTo'
        )}: ${asi.stackName})`;
    return ASIs.map(asi => ({
        label: asiToOptionValue(asi),
        value: asi.prefix,
        key: asi.prefix,
    }));
};

export const ASISelector: FunctionComponent<ASISelectorProps> = ({
    useExisting,
    handlePrefixUpdated,
    existingASIs,
}) => {
    return (
        <ASISelectorContainer>
            <h5>
                {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.prefix')}
                <RequiredStar>*</RequiredStar>
            </h5>
            {useExisting ? (
                <Select
                    className="asi-select"
                    cacheOptions
                    defaultOptions
                    options={ASIPrefixOptionsFromList(existingASIs)}
                    data-test="asi-select"
                    onChange={(event: OptionType): void =>
                        handlePrefixUpdated(event.value.toString())
                    }
                />
            ) : (
                <TextField
                    placeholder="ATL-"
                    width="xlarge"
                    onChange={(event): void => handlePrefixUpdated(event.currentTarget.value)}
                />
            )}
            <HelperMessage>
                {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.details')}
            </HelperMessage>
        </ASISelectorContainer>
    );
};

const defaultDeploymentOptionIndex = 0;
const getDefaultDeploymentMode = (): DeploymentMode => {
    return radioValues[defaultDeploymentOptionIndex].value === EXISTING_VPC_OPTION
        ? 'standalone'
        : 'with-vpc';
};

export const ExistingASIConfiguration: FunctionComponent<ExistingASIConfigurationProps> = ({
    handlePrefixUpdated,
    existingASIs,
    onSelectDeploymentMode,
}) => {
    const [useExisting, setUseExisting] = useState<boolean>(true);

    useEffect(() => {
        // The default value of the radio button is existing VPC so we need to update the deployment mode based on that when first rendered
        onSelectDeploymentMode(getDefaultDeploymentMode());
    }, []);

    return (
        <>
            <SectionMessage appearance="info">
                <p>{I18n.getText('atlassian.migration.datacenter.provision.aws.asi.found')}</p>
            </SectionMessage>
            <h5>
                {I18n.getText(
                    'atlassian.migration.datacenter.provision.aws.asi.chooseDeploymentMethod.label'
                )}
                <RequiredStar>*</RequiredStar>
            </h5>
            <RadioGroup
                options={radioValues}
                defaultValue={radioValues[0].value}
                onChange={(event): void => {
                    handlePrefixUpdated('');
                    const selectedExisting = event.currentTarget.value === EXISTING_VPC_OPTION;
                    setUseExisting(selectedExisting);
                    onSelectDeploymentMode(selectedExisting ? 'standalone' : 'with-vpc');
                }}
            />
            <ASISelector
                useExisting={useExisting}
                handlePrefixUpdated={handlePrefixUpdated}
                existingASIs={existingASIs}
            />
        </>
    );
};
