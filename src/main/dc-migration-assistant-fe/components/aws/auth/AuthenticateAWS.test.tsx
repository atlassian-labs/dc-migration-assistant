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
import { render } from '@testing-library/react';

import { AuthenticateAWS, AuthenticateAWSProps } from './AuthenticateAWS';

const NO_OP_AUTHENTICATION_PAGE_PROPS: AuthenticateAWSProps = {
    onSubmitCreds: () => Promise.resolve('submitted'),
    getRegions: () => Promise.resolve(['my-fake-region-1', 'my-fake-region-2']),
};

describe('AWS Authentication page', () => {
    it('should render', () => {
        const { getByText } = render(<AuthenticateAWS {...NO_OP_AUTHENTICATION_PAGE_PROPS} />);

        expect(
            getByText('atlassian.migration.datacenter.authenticate.aws.accessKeyId.label')
        ).toBeTruthy();
        expect(
            getByText('atlassian.migration.datacenter.authenticate.aws.secretAccessKey.label')
        ).toBeTruthy();
        expect(
            getByText('atlassian.migration.datacenter.authenticate.aws.region.label')
        ).toBeTruthy();
    });

    it('should use the getRegions function to query AWS regions', () => {
        let regionFunCalled = false;
        const testGetRegions = (): Promise<Array<string>> => {
            regionFunCalled = true;
            return Promise.resolve([]);
        };

        render(
            <AuthenticateAWS {...NO_OP_AUTHENTICATION_PAGE_PROPS} getRegions={testGetRegions} />
        );

        expect(regionFunCalled).toBeTruthy();
    });
});
