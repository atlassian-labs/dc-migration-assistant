import { waitForStatus } from '../waiters';

export const waitForProvisioning = (ctx: AppContext) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/provision/status');
    });

    cy.get('#dc-migration-assistant-root h1').contains('Step 3 of 7: Deploy on AWS');
    cy.get('#dc-migration-assistant-root h4').contains('Deploying', {
        timeout: 20000,
    });
    cy.get('button[data-testid=button-refresh]').should('not.be.disabled');
    cy.get('button[data-testid=button-cancel]').should('not.be.disabled');

    waitForStatus(ctx.context + '/rest/dc-migration/1.0/aws/stack/status', 'CREATE_COMPLETE');

    // we need to wait for the button to switch to Next as have different interval to fetch
    // provisioning status via Cypress comapring to the frontend
    cy.get('button[data-testid=button-next]', {
        timeout: 20000,
    }).contains('Next');
    cy.get('#dc-migration-assistant-root h4').contains('Deployment Complete');
};
