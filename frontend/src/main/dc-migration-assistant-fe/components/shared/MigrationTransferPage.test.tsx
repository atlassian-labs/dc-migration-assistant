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

import React, { FunctionComponent } from 'react';
import { render } from '@testing-library/react';
import { BrowserRouter as Router } from 'react-router-dom';

import moment from 'moment';
import { MigrationTransferProps, MigrationTransferPage } from './MigrationTransferPage';
import { MigrationStage } from '../../api/migration';

const mockFetch = jest.fn();
mockFetch.mockReturnValue(Promise.resolve());
window.fetch = mockFetch;

const props: MigrationTransferProps = {
    heading: 'heading',
    description: 'description',
    nextText: 'nextText',
    startMoment: moment().subtract(20, 'minutes'),
    startMigrationPhase: Promise.resolve,
    inProgressStages: [MigrationStage.AUTHENTICATION],
    getProgress: () => {
        return new Promise(resolve => {
            setTimeout(() => {
                resolve([
                    {
                        completeness: 0.5,
                        phase: 'uploading files...',
                    },
                ]);
            }, 500);
        });
    },
    nextRoute: '',
};

describe('Migration Page Component', () => {
    it('should render', () => {
        const Page: FunctionComponent = () => {
            return <MigrationTransferPage {...props} />;
        };
        const { getByText } = render(
            <Router>
                <Page />
            </Router>
        );

        expect(getByText('heading')).toBeTruthy();
        expect(getByText('description')).toBeTruthy();
    });
});
