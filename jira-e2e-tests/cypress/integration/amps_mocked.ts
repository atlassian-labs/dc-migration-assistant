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

describe('Database Migration page', () => {
    const ctx = getContext();

    beforeEach(() => {
        cy.on('uncaught:exception', (err, runnable) => false);

        // Delete `fetch()` to force use of XHR for
        // Cypress. Atlaskit/Jira will polyfill this.
        Cypress.on('window:before:load', (win) => {
            delete win.fetch;
        });
        cy.server();

        cy.jira_login(ctx, 'admin', 'admin');
    });

    it('End to end mocked', () => {
        scenarios.mock_end2end(ctx);
    });
});
