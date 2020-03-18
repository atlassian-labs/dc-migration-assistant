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
import { MemoryRouter } from 'react-router-dom';

import { Home } from './Home';

describe('Home', () => {
    it('Should render', () => {
        const { queryByText } = render(
            <MemoryRouter>
                <Home
                    title="Test Title"
                    synopsis="Test synopsis"
                    exploreMigrationButtonText="Test button"
                />
            </MemoryRouter>
        );

        expect(queryByText('Test Title')).toBeTruthy();
        expect(queryByText('Test synopsis')).toBeTruthy();
        expect(queryByText('Test button')).toBeTruthy();
    });
});
