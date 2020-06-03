/// <reference types="Cypress" />
import * as jira from '../support';
import * as scenarios from '../support/scenarios';

// This test is intended to run against the webpack devserver (AKA
// `yarn start`).

describe('Database Migration page', () => {
    const ctx = jira.devserver_context;

    beforeEach(() => {
        // Note: We don't need to force a polyfill for the standalone
        // we do that in public/index.html.
        cy.server();
    });

    it('End to end mocked', () => {

        scenarios.mock_end2end(ctx);

    });

});
