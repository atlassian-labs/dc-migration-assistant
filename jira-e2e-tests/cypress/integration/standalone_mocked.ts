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

/// <reference types="Cypress" />

import * as scenarios from '../support/scenarios';
import { getContext } from '../support/jira';

// This test is intended to run against the webpack devserver (AKA
// `yarn start`).

describe('Database Migration page', () => {
    const ctx = getContext();

    beforeEach(() => {
        // Note: We don't need to force a polyfill for the standalone
        // we do that in public/index.html.
        cy.server();
    });

    it('End to end mocked', () => {
        scenarios.mock_end2end(ctx);
    });
});
