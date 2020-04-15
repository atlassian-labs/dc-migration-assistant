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

import { MigrationTransferProps, MigrationTransferPage } from './MigrationTransferPage';
import moment from 'moment';

const props: MigrationTransferProps = {
    heading: 'heading',
    description: 'description',
    infoTitle: 'infoTitle',
    infoContent: 'infoContent',
    infoActions: [
        {
            key: 'learn',
            href:
                'https://media0.giphy.com/media/a6OnFHzHgCU1O/giphy.gif?cid=ecf05e472ee78099c642a7d2427127e6f1d4d6f0b77551c7&rid=giphy.gif',
            text: 'infotext',
        },
    ],
    nextText: 'nextText',
    started: moment().subtract(20, 'minutes'),
    getProgress: () => {
        return new Promise(resolve => {
            setTimeout(() => {
                resolve({
                    completeness: 0.5,
                    phase: 'uploading files...',
                    progress: '45 020 files copied',
                });
            }, 500);
        });
    },
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
        expect(getByText('infoContent')).toBeTruthy();
    });
});
