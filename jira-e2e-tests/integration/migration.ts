/// <reference types="Cypress" />

import * as jira from '../support';

const reset = jira.baseURL+'/rest/dc-migration/1.0/develop/migration/reset';

describe('Database Migration page', () => {
    beforeEach(() => {
        cy.jira_login('admin', 'admin');
        cy.reset_migration();
    });

    it('Can provision a cloudformation template', () => {
        cy.visit(jira.migrationHome);

        cy.get('[data-test=start-migration]')
            .should('exist')
            .click();

        // TODO: This should load automatically once the start button
        // progresses to the AWS auth page automatically.
        //cy.visit(awsAuth);

    });

});
