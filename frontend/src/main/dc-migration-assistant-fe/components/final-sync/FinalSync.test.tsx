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

/* eslint-disable */
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();

import React from 'react';
import { render, act } from '@testing-library/react';
import { BrowserRouter as Router } from 'react-router-dom';

import { FinalSyncPage } from './FinalSync';

describe('Database Migration page', () => {
    beforeEach(() => {
        fetchMock.resetMocks();
    });

    it('should render', () => {
        fetchMock.mockResponseOnce(
            JSON.stringify({ status: 'NOT_STARTED', elapsedTime: { seconds: 1, nanos: 2 } })
        );

        act(async () => {
            const { getByText } = render(
                <Router>
                    <FinalSyncPage />
                </Router>
            );

            expect(getByText('atlassian.migration.datacenter.finalSync.title')).toBeTruthy();
            expect(getByText('atlassian.migration.datacenter.finalSync.description')).toBeTruthy();
        });
    });
});
