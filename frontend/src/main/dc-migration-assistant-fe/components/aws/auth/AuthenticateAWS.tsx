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

import React, { FunctionComponent, ReactElement, useState } from 'react';
import Form, { ErrorMessage, Field, FormFooter, HelperMessage } from '@atlaskit/form';
import Button, { ButtonGroup } from '@atlaskit/button';
import TextField from '@atlaskit/textfield';
import { I18n } from '@atlassian/wrm-react-i18n';
import { Redirect } from 'react-router-dom';
import { AsyncSelect, OptionType } from '@atlaskit/select';

import { quickstartPath } from '../../../utils/RoutePaths';
import { ErrorFlag } from '../../shared/ErrorFlag';

export type AWSCreds = {
    accessKeyId: string;
    secretAccessKey: string;
    region: string;
};

/*
Function to submit the AWS credentials. Should return a promise which resolves when the
credentials have been submitted. Should reject with an error message if there was an error submitting the credentials.
*/
export type CredSubmitFun = (creds: AWSCreds) => Promise<string>;

/*
Function to get all AWS regions. Should return a promise which resolves
with the AWS regions
*/
export type QueryRegionFun = () => Promise<Array<string>>;

export type AuthenticateAWSProps = {
    onSubmitCreds: CredSubmitFun;
    getRegions: QueryRegionFun;
};

const RegionSelect: FunctionComponent<{ getRegions: QueryRegionFun }> = (props): ReactElement => {
    const { getRegions } = props;
    const regionListPromiseOptions = (): Promise<Array<OptionType>> => {
        return getRegions().then(regions => {
            return regions.map(region => ({ label: region, value: region, key: region }));
        });
    };

    return (
        <AsyncSelect
            {...props}
            styles={{
                control: (base): React.CSSProperties => ({ ...base, width: '480.4px' }),
            }}
            cacheOptions
            defaultOptions
            isSearchable
            loadOptions={regionListPromiseOptions}
        />
    );
};

export const AuthenticateAWS: FunctionComponent<AuthenticateAWSProps> = ({
    onSubmitCreds,
    getRegions,
}): ReactElement => {
    const [credentialPersistError, setCredentialPersistError] = useState<boolean>(false);
    const [awaitResponseFromApi, setAwaitResponseFromApi] = useState<boolean>(false);
    const [readyForNextStep, setReadyForNextStep] = useState<boolean>(false);

    const submitCreds = (formCreds: {
        accessKeyId: string;
        secretAccessKey: string;
        region: OptionType;
    }): void => {
        const { accessKeyId, secretAccessKey, region } = formCreds;
        const creds: AWSCreds = {
            accessKeyId,
            secretAccessKey,
            region: region.value as string,
        };

        new Promise<void>(resolve => {
            setAwaitResponseFromApi(true);
            resolve();
        })
            .then(() => onSubmitCreds(creds))
            .then(() => {
                setAwaitResponseFromApi(false);
                setReadyForNextStep(true);
            })
            .catch(() => {
                setAwaitResponseFromApi(false);
                setCredentialPersistError(true);
            });
    };

    return (
        <>
            {readyForNextStep && <Redirect to={quickstartPath} push />}
            <h1>{I18n.getText('atlassian.migration.datacenter.step.authenticate.phrase')}</h1>
            <h1>{I18n.getText('atlassian.migration.datacenter.authenticate.aws.title')}</h1>
            <ErrorFlag
                showError={credentialPersistError}
                dismissErrorFunc={(): void => {
                    setCredentialPersistError(false);
                }}
                // FIXME: Internationalisation
                description="You may not have permissions to connect to the AWS account with the supplied credentials. Please try again with a different set of credentials to continue with the migration."
                id="aws-auth-connect-error-flag"
                title="AWS Credentials Error"
            />
            <Form onSubmit={submitCreds}>
                {({ formProps }: any): ReactElement => (
                    <form {...formProps}>
                        <Field
                            isRequired
                            label={I18n.getText(
                                'atlassian.migration.datacenter.authenticate.aws.accessKeyId.label'
                            )}
                            name="accessKeyId"
                            defaultValue=""
                        >
                            {({ fieldProps }: any): ReactElement => (
                                <TextField width="xlarge" {...fieldProps} />
                            )}
                        </Field>
                        <Field
                            isRequired
                            label={I18n.getText(
                                'atlassian.migration.datacenter.authenticate.aws.secretAccessKey.label'
                            )}
                            name="secretAccessKey"
                            defaultValue=""
                        >
                            {({ fieldProps }: any): ReactElement => (
                                <TextField width="xlarge" {...fieldProps} />
                            )}
                        </Field>
                        <Field
                            label={I18n.getText(
                                'atlassian.migration.datacenter.authenticate.aws.region.label'
                            )}
                            name="region"
                            validate={(value: OptionType): string => {
                                return value ? undefined : 'NO_REGION';
                            }}
                        >
                            {({ fieldProps, error }: any): ReactElement => (
                                <>
                                    <HelperMessage>
                                        {I18n.getText(
                                            'atlassian.migration.datacenter.authenticate.aws.region.helper'
                                        )}
                                    </HelperMessage>
                                    <RegionSelect getRegions={getRegions} {...fieldProps} />
                                    {error && (
                                        <ErrorMessage>
                                            {I18n.getText(
                                                'atlassian.migration.datacenter.authenticate.aws.region.error'
                                            )}
                                        </ErrorMessage>
                                    )}
                                </>
                            )}
                        </Field>
                        <FormFooter align="start">
                            <ButtonGroup>
                                <Button
                                    type="submit"
                                    appearance="primary"
                                    testId="awsSecretKeySubmitFormButton"
                                    isLoading={awaitResponseFromApi}
                                >
                                    {I18n.getText(
                                        'atlassian.migration.datacenter.authenticate.aws.submit'
                                    )}
                                </Button>
                                <Button appearance="default">
                                    {I18n.getText(
                                        'atlassian.migration.datacenter.authenticate.aws.cancel'
                                    )}
                                </Button>
                            </ButtonGroup>
                        </FormFooter>
                    </form>
                )}
            </Form>
        </>
    );
};
