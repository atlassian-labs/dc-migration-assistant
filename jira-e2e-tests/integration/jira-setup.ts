/// <reference types="Cypress" />

import * as jira from '../lib/jira';

// Skipped as we only want to run on a clean install. Remove 'skip' if
// you want to run it.
it.skip('Login and setup', () => {
    cy.jira_login('admin', 'admin');
    cy.jira_setup();
})
