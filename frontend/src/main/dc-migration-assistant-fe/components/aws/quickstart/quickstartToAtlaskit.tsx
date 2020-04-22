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

import React, { ReactElement } from 'react';
import Select, { AsyncSelect, OptionType } from '@atlaskit/select';
import Toggle from '@atlaskit/toggle';
import TextField from '@atlaskit/textfield';
import { ErrorMessage, Field, HelperMessage } from '@atlaskit/form';
import { I18n } from '@atlassian/wrm-react-i18n';
// eslint-disable-next-line import/extensions
import { QuickstartParameter } from './QuickStartTypes';
import { callAppRest, RestApiPathConstants } from '../../../utils/api';

type FormElementGenerator = (
    defaultProps: Record<string, string>,
    param: QuickstartParameter
) => ReactElement;
type InputProps = Record<string, boolean | number | string | Function>;

const availabilityZonesLoadOptions = (): Promise<Array<OptionType>> =>
    callAppRest('GET', RestApiPathConstants.awsAzListForRegion)
        .then(r => r.json())
        .then(azs => (azs as Array<string>).map(az => ({ label: az, value: az, key: az })));

const createAZSelection: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramProperties: { Description },
    } = param;

    const validate = (value: Array<OptionType>): string => {
        if (value.length !== 2) {
            return 'INCORRECT_NUM_AZ';
        }
        return undefined;
    };

    return (
        <Field validate={validate} {...defaultFieldProps}>
            {({ fieldProps, error }: any): ReactElement => (
                <>
                    <AsyncSelect
                        className="az-select"
                        cacheOptions
                        defaultOptions
                        isMulti
                        isSearchable={false}
                        loadOptions={availabilityZonesLoadOptions}
                        {...fieldProps}
                    />
                    <HelperMessage>{Description}</HelperMessage>
                    {error && (
                        <ErrorMessage>
                            {I18n.getText(
                                'atlassian.migration.datacenter.provision.aws.form.wrongNumberAZError'
                            )}
                        </ErrorMessage>
                    )}
                </>
            )}
        </Field>
    );
};

const createNumberInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramProperties: { Default, ConstraintDescription, Description, MaxValue, MinValue },
    } = param;

    const overrideInputProps: InputProps = {
        type: 'number',
    };

    let overrideFieldProps: InputProps = {
        defaultValue: (Default as number) || '',
    };

    if (MaxValue) {
        overrideFieldProps = {
            ...overrideFieldProps,
            validate: (value: number): string => {
                if (value <= MaxValue) {
                    return undefined;
                }
                return ConstraintDescription;
            },
        };
    }

    if (MinValue) {
        const minValueValidateFunc = (value: number): string => {
            if (value >= MinValue) {
                return undefined;
            }
            return ConstraintDescription;
        };
        let validate = minValueValidateFunc;
        if (MaxValue) {
            const oldValidate = overrideFieldProps.validate as Function;
            validate = (value: number): string => {
                return oldValidate(value) || minValueValidateFunc(value);
            };
        }

        overrideFieldProps = {
            ...overrideFieldProps,
            validate,
        };
    }
    return (
        <Field {...defaultFieldProps} {...overrideFieldProps}>
            {({ fieldProps, error }: any): ReactElement => {
                return (
                    <>
                        <TextField width="medium" {...fieldProps} {...overrideInputProps} />
                        <HelperMessage>{Description}</HelperMessage>
                        {error && <ErrorMessage>{error}</ErrorMessage>}
                    </>
                );
            }}
        </Field>
    );
};

const createStringInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramProperties: {
            AllowedPattern,
            ConstraintDescription,
            Description,
            Default,
            MaxLength,
            NoEcho,
        },
    } = param;

    let overrideInputProps: Record<string, string | number | boolean | Function> = {
        type: NoEcho ? 'password' : 'text',
    };

    let overrideFieldProps: Record<string, string | number | boolean | Function> = {
        defaultValue: (Default as string) || '',
    };

    if (MaxLength) {
        overrideInputProps = {
            maxLength: MaxLength,
            ...overrideInputProps,
        };
    }

    if (AllowedPattern) {
        overrideInputProps = {
            ...overrideInputProps,
        };
        overrideFieldProps = {
            ...overrideFieldProps,
            validate: (value: string): string => {
                const regex = new RegExp(AllowedPattern);
                const testSuccess = regex.test(value);
                if (testSuccess) {
                    return undefined;
                }
                return (
                    ConstraintDescription ||
                    `${param.paramLabel} ${I18n.getText(
                        'atlassian.migration.datacenter.provision.aws.form.defaultError'
                    )} ${AllowedPattern}`
                );
            },
        };
    }

    return (
        <Field {...defaultFieldProps} {...overrideFieldProps}>
            {({ fieldProps, error }: any): ReactElement => (
                <>
                    <TextField width="xlarge" {...fieldProps} {...overrideInputProps} />
                    <HelperMessage>{Description}</HelperMessage>
                    {error && <ErrorMessage>{error}</ErrorMessage>}
                </>
            )}
        </Field>
    );
};

const createInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramKey,
        paramProperties: { Type },
    } = param;
    if (Type === 'Number') {
        return createNumberInputFromQuickstartParam(defaultFieldProps, param);
    }
    if (Type === 'String') {
        return createStringInputFromQuickstartParam(defaultFieldProps, param);
    }

    return <div key={paramKey}>UNRECOGNISED PARAM TYPE</div>;
};

const createSelectFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const { paramProperties } = param;
    const { AllowedValues, Default, Description } = paramProperties;
    if (AllowedValues.length === 2 && typeof AllowedValues[0] === 'boolean') {
        return (
            <Field {...defaultFieldProps}>
                {({ fieldProps }: any): ReactElement => (
                    <>
                        <Toggle
                            {...fieldProps}
                            size="large"
                            isDefaultChecked={Default as boolean}
                        />
                        <HelperMessage>{Description}</HelperMessage>
                    </>
                )}
            </Field>
        );
    }

    const options = AllowedValues.map(val => ({ label: val as string, value: val, key: val }));
    const defaultOption = options.find(option => option.value === Default);

    const overrideFieldProps = {
        options,
    };

    return (
        <Field {...defaultFieldProps} defaultValue={defaultOption}>
            {({ fieldProps }: any): ReactElement => (
                <>
                    <Select {...fieldProps} {...overrideFieldProps} />
                    <HelperMessage>{Description}</HelperMessage>
                </>
            )}
        </Field>
    );
};

const quickstartParamToAtlaskitFormElement: FormElementGenerator = (defaultFieldProps, param) => {
    const { paramProperties } = param;
    if (paramProperties.AllowedValues) {
        return createSelectFromQuickstartParam(defaultFieldProps, param);
    }
    if (paramProperties.Type === 'List<AWS::EC2::AvailabilityZone::Name>') {
        return createAZSelection(defaultFieldProps, param);
    }
    return createInputFromQuickstartParam(defaultFieldProps, param);
};

export const createQuickstartFormField = (param: QuickstartParameter): ReactElement => {
    const { paramKey, paramLabel } = param;

    const defaultFieldProps = {
        key: paramKey,
        label: paramLabel,
        name: paramKey,
        defaultValue: '',
    };

    return quickstartParamToAtlaskitFormElement(defaultFieldProps, param);
};
