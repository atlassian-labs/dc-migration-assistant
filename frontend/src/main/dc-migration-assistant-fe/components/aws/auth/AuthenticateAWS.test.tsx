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

import React from 'react';
import { fireEvent, render } from '@testing-library/react';
import selectEvent from 'react-select-event';
import { act } from 'react-dom/test-utils';
import { MemoryRouter } from 'react-router-dom';

import { AuthenticateAWS, AuthenticateAWSProps, AWSCreds } from './AuthenticateAWS';

const NO_OP_AUTHENTICATION_PAGE_PROPS: AuthenticateAWSProps = {
    onSubmitCreds: () => Promise.resolve('submitted'),
    getRegions: () => Promise.resolve(['my-fake-region-1', 'my-fake-region-2']),
};

const AWS_REGION_SELECT_LABEL = 'AWS_REGION_SELECT_LABEL';

describe('AWS Authentication page', () => {
    it('should render', () => {
        const { getByText } = render(<AuthenticateAWS {...NO_OP_AUTHENTICATION_PAGE_PROPS} />, {
            wrapper: MemoryRouter,
        });

        expect(getByText('atlassian.migration.datacenter.step.authenticate.phrase')).toBeTruthy();
        expect(
            getByText('atlassian.migration.datacenter.authenticate.aws.accessKeyId.label')
        ).toBeTruthy();
        expect(
            getByText('atlassian.migration.datacenter.authenticate.aws.secretAccessKey.label')
        ).toBeTruthy();
        expect(getByText(AWS_REGION_SELECT_LABEL)).toBeTruthy();
    });

    it('should use the getRegions function to query AWS regions', () => {
        let regionFunCalled = false;
        const testGetRegions = (): Promise<Array<string>> => {
            regionFunCalled = true;
            return Promise.resolve([]);
        };

        render(
            <AuthenticateAWS {...NO_OP_AUTHENTICATION_PAGE_PROPS} getRegions={testGetRegions} />,
            { wrapper: MemoryRouter }
        );

        expect(regionFunCalled).toBeTruthy();
    });

    it('should not submit credentials when form is empty', async () => {
        let credentialsSubmitted = false;
        const submitCredentialsCallback = (): Promise<string> => {
            credentialsSubmitted = true;
            return Promise.resolve('credentials stored');
        };
        await act(async () => {
            const { getByTestId, getByText } = render(
                <AuthenticateAWS
                    {...NO_OP_AUTHENTICATION_PAGE_PROPS}
                    onSubmitCreds={submitCredentialsCallback}
                />,
                { wrapper: MemoryRouter }
            );

            const submitButton = getByTestId('awsSecretKeySubmitFormButton');
            await fireEvent.submit(submitButton);

            expect(credentialsSubmitted).toBeFalsy();
            expect(
                getByText('atlassian.migration.datacenter.authenticate.aws.region.error')
            ).toBeTruthy();
        });
    });

    it('should submit credentials when form is complete', async () => {
        let credentialsSubmitted = false;
        const submitCredentialsCallback = (awsCreds: AWSCreds): Promise<string> => {
            expect(awsCreds.accessKeyId).toBe('akia');
            expect(awsCreds.secretAccessKey).toBe('asak');
            expect(awsCreds.region).toBe('my-fake-region-1');
            credentialsSubmitted = true;
            return Promise.resolve('credentials stored');
        };

        await act(async () => {
            const { getByTestId, container, getByLabelText } = render(
                <AuthenticateAWS
                    {...NO_OP_AUTHENTICATION_PAGE_PROPS}
                    onSubmitCreds={submitCredentialsCallback}
                />,
                { wrapper: MemoryRouter }
            );

            const secretKeyInput = container.querySelector('[name="secretAccessKey"]');
            // TODO: Unsure why this is required; but not adding this will only register the change event for the last fireevent invocation only.
            await fireEvent.reset(secretKeyInput);
            await fireEvent.change(secretKeyInput, { target: { value: 'asak' } });

            const regionInput = getByLabelText(AWS_REGION_SELECT_LABEL, { selector: 'input' });
            await selectEvent.select(regionInput, 'my-fake-region-1');

            const accessKeyInput = container.querySelector('[name="accessKeyId"]');
            await fireEvent.change(accessKeyInput, { target: { value: 'akia' } });

            const submitButton = getByTestId('awsSecretKeySubmitFormButton');
            await fireEvent.submit(submitButton);
            expect(credentialsSubmitted).toBeTruthy();
        });
    });
});
