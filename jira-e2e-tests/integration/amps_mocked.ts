/// <reference types="Cypress" />
import * as jira from '../support';
import * as scenarios from '../support/scenarios';

describe('Database Migration page', () => {
    const ctx = jira.amps_context;

    beforeEach(() => {
        cy.on('uncaught:exception', (err, runnable) => false);

        // Delete `fetch()` to force use of XHR for
        // Cypress. Atlaskit/Jira will polyfill this.
        Cypress.on('window:before:load', (win) => {
            delete win.fetch
        })
        cy.server();

        cy.jira_login(ctx, 'admin', 'admin');
    });

    it('End to end mocked', () => {

        scenarios.mock_end2end(ctx);

    });

});
