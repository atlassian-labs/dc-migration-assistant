/// <reference types="Cypress" />
import * as jira from '../support';
import * as scenarios from '../support/scenarios';

describe('Plugin installation smoke tests', () => {
    const ctx = jira.compose_context;

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

    it('Test AWS Auth', () => {
        cy.reset_migration(ctx);
        cy.visit(ctx.migrationHome);

        cy.get('[data-test=start-migration]')
            .should('exist')
            .click();

        // AWS auth page.
        cy.location().should((loc: Location) => {
            expect(loc.pathname).to.eq(ctx.context+'/plugins/servlet/dc-migration-assistant/aws/auth')
        });

        cy.get('[data-test=aws-auth-key]').type(Cypress.env('AWS_ACCESS_KEY_ID'));
        cy.get('[data-test=aws-auth-secret]').type(Cypress.env('AWS_SECRET_ACCESS_KEY'));
        // FIXME: This may be flaky; the AtlasKit AsyncSelect
        // component is hard to instrument.
        cy.get('#region-uid3').click();
        cy.get(`[id^=react-select]:contains(ap-southeast-2)`).click();

        cy.get('[data-test=aws-auth-submit]').click();

        // ASI config page.
        cy.location().should((loc: Location) => {
            expect(loc.pathname).to.eq(ctx.context+'/plugins/servlet/dc-migration-assistant/aws/asi')
        });
    });

});
