import { waitForStatus } from '../waiters';

const header = 'Step 4 of 7: Copy Content';

export const startFileSystemInitialMigration = (ctx: AppContext) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/fs');
    });
    cy.get('#dc-migration-assistant-root h1').contains(header);
    cy.get('button[data-testid=button-cancel]').should('be.visible').and('not.be.disabled');

    cy.get('button[data-testid=button-start-copying]').click();
};

export const monitorFileSystemMigration = (ctx: AppContext) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/fs');
    });
    cy.get('#dc-migration-assistant-root h1').contains(header);
    cy.get('button[data-testid=button-refresh]').should('be.visible');

    cy.get('#dc-migration-assistant-root h4').contains('Counting and uploading your files to AWS');

    waitForStatus(ctx.context + '/rest/dc-migration/1.0/migration/fs/report', 'DONE');

    cy.get('button[data-testid=button-next]', { timeout: 20000 })
        .contains('Next')
        .should('be.visible')
        .click();
};
