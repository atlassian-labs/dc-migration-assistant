/// <reference types="Cypress" />
import * as jira from '../support';
import * as scenarios from '../support/scenarios';

describe('Plugin installation smoke tests', () => {
    const ctx = jira.amps_context;

    beforeEach(() => {
        cy.on('uncaught:exception', (err, runnable) => false);

        cy.jira_login(ctx, 'admin', 'admin');
    });

    it('Ensure plugin loaded', () => {
        cy.visit(ctx.upmURL)

        cy.get('[data-key="com.atlassian.migration.datacenter.jira-plugin"]',
               {timeout: 60*1000})
            .should('exist')
            .click()

        cy.get('.upm-count-enabled')
            .should((el) => {
                expect(el.first()).to.contain('8 of 8 modules enabled')
            })
    });

});

