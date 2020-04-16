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
import styled from 'styled-components';
import TextField from '@atlaskit/textfield';
import { I18n } from '@atlassian/wrm-react-i18n';
import { AsyncSelect, OptionType } from '@atlaskit/select';
import Flag from '@atlaskit/flag';
import ErrorIcon from '@atlaskit/icon/glyph/error';
import { colors } from '@atlaskit/theme';
import { useHistory } from 'react-router-dom';
import { quickstartPath } from '../../../utils/RoutePaths';

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

type AuthenticationErrorProps = {
    showError: boolean;
    dismissErrorFunc: () => void;
};

const AwsAuthErrorContainer = styled.div`
    position: fixed;
    top: 70px;
    left: 75%;
    right: 1%;
    overflow: inherit;
`;

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

const AuthenticationErrorFlag: FunctionComponent<AuthenticationErrorProps> = (
    props
): ReactElement => {
    const { showError, dismissErrorFunc } = props;

    if (showError) {
        return (
            <AwsAuthErrorContainer>
                <Flag
                    actions={[
                        {
                            content: 'Dismiss',
                            onClick: dismissErrorFunc,
                        },
                    ]}
                    icon={<ErrorIcon primaryColor={colors.R400} label="Info" />}
                    description="You may not have permissions to connect to the AWS account with the supplied credentials. Please try again with a different set of credentials to continue with the migration."
                    id="aws-auth-connect-error-flag"
                    key="connect-error"
                    title="AWS Credentials Error"
                />
            </AwsAuthErrorContainer>
        );
    }
    return null;
};

export const AuthenticateAWS: FunctionComponent<AuthenticateAWSProps> = ({
    onSubmitCreds,
    getRegions,
}): ReactElement => {
    const [credentialPersistError, setCredentialPersistError] = useState(false);
    const [awaitResponseFromApi, setAwaitResponseFromApi] = useState(false);
    const history = useHistory();
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
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            .then(_value => {
                setAwaitResponseFromApi(false);
                history.push(quickstartPath);
            })
            .catch(() => {
                setAwaitResponseFromApi(false);
                setCredentialPersistError(true);
            });
    };

    return (
        <>
            <h1>{I18n.getText('atlassian.migration.datacenter.step.authenticate.phrase')}</h1>
            <h1>{I18n.getText('atlassian.migration.datacenter.authenticate.aws.title')}</h1>
            <AuthenticationErrorFlag
                showError={credentialPersistError}
                dismissErrorFunc={(): void => {
                    setCredentialPersistError(false);
                }}
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
