import { waitForStatus } from '../waiters';
export const runFinalSync = () => {
    cy.get('#dc-migration-assistant-root h1').contains('Step 6 of 7: Final Sync');
    cy.get('#dc-migration-assistant-root p').contains(
        'Now that youve blocked user access to the instance, we can copy its database and sync any new content changes. You can close this page and return anytime to check its progress.'
    );

    cy.get('button[data-testid=button-cancel').should('be.visible');
    cy.get('button[data-testid=button-sync-now-').contains('Sync now!').click();
};

export const monitorFinalSync = (ctx: AppContext) => {
    cy.get('#dc-migration-assistant-root h1').contains('Step 6 of 7: Final Sync');
    cy.get('#dc-migration-assistant-root h4').contains('Database import');
    cy.get('#dc-migration-assistant-root h4').contains('Copying new files to new deployment');

    cy.get('button[data-testid=button-cancel').should('be.visible');
    cy.get('button[data-testid=button-refresh').should('be.visible');

    waitForStatus(`${ctx.context}/rest/dc-migration/1.0/migration/final-sync/status`, 'DONE');

    cy.get('button[data-testid=button-next').should('be.visible').click();
};
