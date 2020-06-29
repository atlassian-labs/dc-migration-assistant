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

import React, {FunctionComponent, ReactElement, ReactFragment, useEffect, useState} from 'react';
import Form, {ErrorMessage, Field, FormHeader, FormSection, HelperMessage} from '@atlaskit/form';
import TextField from '@atlaskit/textfield';
import Button, {ButtonGroup} from '@atlaskit/button';
import Spinner from '@atlaskit/spinner';
import {OptionType} from '@atlaskit/select';
import {I18n} from '@atlassian/wrm-react-i18n';
import styled from 'styled-components';
import Panel from '@atlaskit/panel';
import {Redirect} from 'react-router-dom';

import {yamlParse} from 'yaml-cfn';
import {createQuickstartFormField} from './quickstartToAtlaskit';
import {QuickstartParameterGroup, QuickstartTemplateForm} from './QuickStartTypes';
import {quickstartStatusPath} from '../../../utils/RoutePaths';
import {CancelButton} from '../../shared/CancelButton';
import {DeploymentMode} from './QuickstartRoutes';
import {provisioning} from '../../../api/provisioning';
import {ErrorFlag} from '../../shared/ErrorFlag';

const STACK_NAME_FIELD_NAME = 'stackName';

const isOptionType = (obj: any): obj is OptionType => {
    return obj.label && obj.value;
};

const isArrayOfOptionType = (obj: any): obj is Array<OptionType> => {
    return obj.length > 0 && isOptionType(obj[0]);
};

const QuickstartFormContainer = styled.form`
    width: 60%;
`;

const PanelContainer = styled.div`
    & span {
        font-size: 1.4em;
        font-weight: 400;
    }
`;

const ButtonRow = styled.div`
    margin: 15px 0px 0px 10px;
`;

const StackNameField = (): ReactElement => {
    const fieldNameValidator = (stackName: string): string => {
        // NOTE: This gets converted to an S3 bucket name (with '-migration'
        // appended), so must also conform with bucket naming rules.
        const regExpMatch = stackName.match('^[a-z][a-z0-9-.]{1,53}$');
        return regExpMatch != null
            ? undefined
            : I18n.getText(
                  'atlassian.migration.datacenter.provision.aws.form.stackName.validationMessage'
              );
    };

    return (
        <Field
            validate={fieldNameValidator}
            defaultValue=""
            label={I18n.getText(
                'atlassian.migration.datacenter.provision.aws.form.stackName.label'
            )}
            name={STACK_NAME_FIELD_NAME}
            data-test="stack-name"
        >
            {({ fieldProps, error }: any): ReactElement => (
                <>
                    <TextField width="medium" {...fieldProps} />
                    <HelperMessage>
                        {I18n.getText(
                            'atlassian.migration.datacenter.provision.aws.form.stackName.helper'
                        )}
                    </HelperMessage>
                    {error && <ErrorMessage>{error}</ErrorMessage>}
                </>
            )}
        </Field>
    );
};

type QuickstartFormProps = {
    paramGroups: Array<QuickstartParameterGroup>;
    onSubmit: (data: Record<string, any>) => Promise<void>;
    ASIPrefixOverride?: string;
};

const QuickstartForm: FunctionComponent<QuickstartFormProps> = ({
    paramGroups,
    onSubmit,
    ASIPrefixOverride,
}) => {
    const renderFormSection = (group: QuickstartParameterGroup): ReactFragment => {
        const getFormSectionFragment = (props = {}): ReactFragment => {
            return (
                <FormSection key={group.groupLabel} {...props}>
                    {group.parameters.map(parameter => {
                        const param = parameter;
                        if (ASIPrefixOverride && parameter.paramKey === 'ExportPrefix') {
                            param.paramProperties.Default = ASIPrefixOverride;
                        } else if (parameter.paramKey === 'JiraVersion') {
                            param.paramProperties.Default = '8.5.3';
                        }
                        return createQuickstartFormField(param);
                    })}
                </FormSection>
            );
        };

        if (group.shouldExpandGroupOnLoad) {
            return getFormSectionFragment({ title: group.groupLabel });
        }
        return (
            <PanelContainer key={`${group.groupLabel}-panelContainer`}>
                <Panel
                    header={group.groupLabel}
                    key={`${group.groupLabel}-panel`}
                    isDefaultExpanded={group.shouldExpandGroupOnLoad}
                >
                    {getFormSectionFragment()}
                </Panel>
            </PanelContainer>
        );
    };
    const [submitLoading, setSubmitLoading] = useState<boolean>(false);

    const doSubmit = (data: Record<string, any>): void => {
        setSubmitLoading(true);
        onSubmit(data).finally(() => setSubmitLoading(false));
    };

    return (
        <Form onSubmit={doSubmit}>
            {({ formProps }: any): ReactElement => (
                <QuickstartFormContainer {...formProps}>
                    <FormHeader
                        title={I18n.getText('atlassian.migration.datacenter.provision.aws.title')}
                    />
                    <p>
                        {I18n.getText('atlassian.migration.datacenter.provision.aws.description')}
                    </p>
                    <StackNameField />
                    {paramGroups.map(group => {
                        return renderFormSection(group);
                    })}
                    <ButtonRow>
                        <ButtonGroup>
                            <Button
                                isLoading={submitLoading}
                                type="submit"
                                appearance="primary"
                                data-test="qs-submit"
                            >
                                {I18n.getText(
                                    'atlassian.migration.datacenter.provision.aws.form.deploy'
                                )}
                            </Button>
                            <CancelButton />
                        </ButtonGroup>
                    </ButtonRow>
                </QuickstartFormContainer>
            )}
        </Form>
    );
};

const buildQuickstartParams = (
    quickstartParamDoc: QuickstartTemplateForm
): Array<QuickstartParameterGroup> => {
    const params = quickstartParamDoc.Parameters;
    const paramLabels = quickstartParamDoc.ParameterLabels;
    const paramGroups = quickstartParamDoc.ParameterGroups;

    return paramGroups.map(group => {
        const { Label, Parameters } = group;
        const paramGroupLabel = Label;
        return {
            groupLabel: paramGroupLabel.default,
            shouldExpandGroupOnLoad: !/optional/i.test(paramGroupLabel.default),
            parameters: Parameters.map(parameter => {
                return {
                    paramKey: parameter,
                    paramLabel: paramLabels[parameter].default,
                    paramProperties: params[parameter],
                };
            }),
        };
    });
};

const QuickStartDeployContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-items: center;
`;

const quickstartWithVPCParametersTemplateLocation = (): string => {
    const parametersUrlFromEnv = process.env.DEFAULT_QUICKSTART_WITH_VPC_PARAMETER_URL;
    if (parametersUrlFromEnv === undefined) {
        throw Error('QuickStart Jira with VPC template location not set in .env');
    }
    return parametersUrlFromEnv;
};

const quickstartStandaloneParametersTemplateLocation = (): string => {
    const parametersUrlFromEnv = process.env.DEFAULT_QUICKSTART_STANDALONE_PARAMETER_URL;
    if (parametersUrlFromEnv === undefined) {
        throw Error('QuickStart Jira template location not set in .env');
    }
    return parametersUrlFromEnv;
};

const templateForDeploymentMode = (mode: DeploymentMode): string => {
    switch (mode) {
        case 'standalone':
            return quickstartStandaloneParametersTemplateLocation();
        case 'with-vpc':
        default:
            return quickstartWithVPCParametersTemplateLocation();
    }
};

type QuickStartDeployProps = {
    ASIPrefix?: string;
    deploymentMode: DeploymentMode;
};

const buildQuickstartForm = (text: string): Promise<QuickstartTemplateForm> => {
    const templateYaml = yamlParse(text);
    const cfnMetadata = templateYaml.Metadata['AWS::CloudFormation::Interface'];
    return new Promise<QuickstartTemplateForm>(resolve => {
        return resolve({
            Parameters: templateYaml.Parameters,
            ParameterGroups: cfnMetadata.ParameterGroups,
            ParameterLabels: cfnMetadata.ParameterLabels,
        });
    });
};

export const QuickStartDeploy: FunctionComponent<QuickStartDeployProps> = ({
    ASIPrefix,
    deploymentMode,
}): ReactElement => {
    const [params, setParams] = useState<Array<QuickstartParameterGroup>>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [readyForNextStep, setReadyForNextStep] = useState<boolean>(false);
    const [error, setError] = useState<string>();

    useEffect(() => {
        setLoading(true);
        fetch(templateForDeploymentMode(deploymentMode), {
            method: 'GET',
        })
            .then(resp => resp.text())
            .then(buildQuickstartForm)
            .then(quickstartForm => {
                const groupedParameters = buildQuickstartParams(quickstartForm);
                setParams(groupedParameters);
                setLoading(false);
            });
    }, []);

    const onSubmitQuickstartForm = async (data: Record<string, any>): Promise<void> => {
        // Deep copy structure so form state is not modified
        const transformedCfnParams = {
            ...data,
        };
        const stackNameValue = transformedCfnParams[STACK_NAME_FIELD_NAME];
        delete transformedCfnParams[STACK_NAME_FIELD_NAME];

        Object.entries(data).forEach(entry => {
            // Hoist value from Select/Multiselect inputs to root of form value
            const [key, value] = entry;
            if (isOptionType(value)) {
                transformedCfnParams[key] = value.value;
            } else if (isArrayOfOptionType(value)) {
                transformedCfnParams[key] = value.map(option => option.value).join(',');
            }
        });

        const request =
            deploymentMode === 'standalone'
                ? provisioning.deployApplication(stackNameValue, transformedCfnParams)
                : provisioning.deployApplicationWithNetwork(stackNameValue, transformedCfnParams);

        return request
            .then(async response => {
                if (response.status !== 202) {
                    const json = await response.json();
                    throw new Error(json.error);
                }
                setReadyForNextStep(true);
            })
            .catch(err => {
                setError(err.message);
            });
    };

    return (
        <QuickStartDeployContainer>
            {readyForNextStep && <Redirect to={quickstartStatusPath} push />}
            {loading ? (
                <Spinner />
            ) : (
                <>
                    <ErrorFlag
                        showError={error && error !== ''}
                        dismissErrorFunc={(): void => setError('')}
                        title={I18n.getText(
                            'atlassian.migration.datacenter.provision.aws.quickstart.form.error'
                        )}
                        description={error}
                        id={error}
                    />
                    <QuickstartForm
                        paramGroups={params}
                        onSubmit={onSubmitQuickstartForm}
                        ASIPrefixOverride={ASIPrefix}
                    />
                </>
            )}
        </QuickStartDeployContainer>
    );
};
