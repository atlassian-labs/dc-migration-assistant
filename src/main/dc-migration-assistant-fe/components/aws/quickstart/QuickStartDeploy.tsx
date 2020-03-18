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
import yaml from 'yaml';
import Form, { ErrorMessage, Field, FormHeader, FormSection } from '@atlaskit/form';
import TextField from '@atlaskit/textfield';
import Button from '@atlaskit/button';
import Spinner from '@atlaskit/spinner';
import { OptionType } from '@atlaskit/select';
import { I18n } from '@atlassian/wrm-react-i18n';
import styled from 'styled-components';

import { createQuickstartFormField } from './quickstartToAtlaskit';
import {
    QuickstartParameterGroup,
    QuickStartParameterYamlNode,
    QuickstartParamGroupYamlNode,
    QuickstartParamLabelYamlNode,
} from './QuickStartTypes';

import { callAppRest, RestApiPathConstants } from '../../../utils/api';

const QUICKSTART_PARAMETERS_URL =
    'https://dcd-slinghost-templates.s3-ap-southeast-2.amazonaws.com/mothra/quickstart-jira-dc-with-vpc.template.parameters.yaml';
const stackProvisioningTemplateUrl =
    'https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml';

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

const QuickstartSubmitButton = styled(Button)`
    margin-top: 10px;
`;

const StackNameField = (): ReactElement => {
    const fieldNameValidator = (stackName: string): string => {
        const regExpMatch = stackName.match('^[a-zA-Z][a-zA-Z0-9-.]{1,126}$');
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
        >
            {({ fieldProps, error }: any): ReactElement => (
                <>
                    <TextField width="medium" {...fieldProps} />
                    {error && <ErrorMessage>{error}</ErrorMessage>}
                </>
            )}
        </Field>
    );
};

const QuickstartForm = ({
    quickstartParamGroups,
}: Record<string, Array<QuickstartParameterGroup>>): ReactElement => (
    <Form
        onSubmit={(data: Record<string, any>): void => {
            const transformedCfnParams = data;
            const stackNameValue = transformedCfnParams[STACK_NAME_FIELD_NAME];
            delete transformedCfnParams[STACK_NAME_FIELD_NAME];

            Object.entries(data).forEach(entry => {
                // Hoist value from Select/Multiselect inputs to root of form value
                const [key, value] = entry;
                if (isOptionType(value)) {
                    transformedCfnParams[key] = value.value;
                } else if (isArrayOfOptionType(value)) {
                    transformedCfnParams[key] = JSON.stringify(value.map(option => option.value));
                }
            });

            callAppRest('POST', RestApiPathConstants.awsStackCreateRestPath, {
                templateUrl: stackProvisioningTemplateUrl,
                stackName: stackNameValue,
                params: transformedCfnParams,
            })
                .then(response => {
                    if (response.status !== 202) {
                        throw Error('Stack provisioning failed');
                    }
                    return response;
                })
                .then(r => r.text())
                .catch(err => {
                    console.error(err);
                });
        }}
    >
        {({ formProps }: any): ReactElement => (
            <QuickstartFormContainer {...formProps}>
                <FormHeader
                    title={I18n.getText('atlassian.migration.datacenter.provision.aws.form.title')}
                />
                <StackNameField />
                {quickstartParamGroups.map(group => {
                    return (
                        <FormSection key={group.groupLabel} title={group.groupLabel}>
                            {group.parameters.map(parameter => {
                                return createQuickstartFormField(parameter);
                            })}
                        </FormSection>
                    );
                })}
                <QuickstartSubmitButton type="submit" appearance="primary">
                    {I18n.getText('atlassian.migration.datacenter.generic.submit')}
                </QuickstartSubmitButton>
            </QuickstartFormContainer>
        )}
    </Form>
);

const buildQuickstartParams = (quickstartParamDoc: any): Array<QuickstartParameterGroup> => {
    const params: Record<string, QuickStartParameterYamlNode> = quickstartParamDoc.Parameters;
    const paramLabels: Record<string, QuickstartParamLabelYamlNode> =
        quickstartParamDoc.ParameterLabels;
    const paramGroups: Array<QuickstartParamGroupYamlNode> = quickstartParamDoc.ParameterGroups;

    return paramGroups.map(group => {
        const { Label, Parameters } = group;
        const paramGroupLabel = Label;
        return {
            groupLabel: paramGroupLabel.default,
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

export const QuickStartDeploy: FunctionComponent = (): ReactElement => {
    const [params, setParams]: [Array<QuickstartParameterGroup>, Function] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        fetch(QUICKSTART_PARAMETERS_URL, {
            method: 'GET',
        })
            .then(resp => resp.text())
            .then(text => {
                const paramDoc = yaml.parse(text);

                const groupedParameters = buildQuickstartParams(paramDoc);

                setParams(groupedParameters);
                setLoading(false);
            });
    }, []);

    return (
        <QuickStartDeployContainer>
            {loading ? <Spinner /> : <QuickstartForm quickstartParamGroups={params} />}
        </QuickStartDeployContainer>
    );
};
