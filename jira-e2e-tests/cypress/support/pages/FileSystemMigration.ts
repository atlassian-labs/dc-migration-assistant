import { waitForStatus, EndpointType } from '../waiters';
import { AppContext } from '../common';

const header = 'Step 4 of 7: Copy Content';

export const startFileSystemInitialMigration = (ctx: AppContext) => {
    cy.location({ timeout: 20000 }).should((loc: Location) => {
        expect(loc.pathname).to.eq(`${ctx.pluginPath}/fs`);
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

    waitForStatus(
        ctx.context + '/rest/dc-migration/1.0/migration/fs/report',
        'DONE',
        EndpointType.FILESYSTEM_REPORT
    );

    cy.get('#dc-migration-assistant-root').then(($section) => {
        cy.log($section.text());
        if ($section.text().includes('Ignore and continue')) {
            cy.log('We had failing files, we will ignore and continue');
            cy.get('a', { timeout: 20000 }).contains('Ignore and continue').click();
        } else {
            cy.log('All green, lets continue');
            cy.get('button[data-testid=button-next]', { timeout: 20000 }).contains('Next');
        }
    });
};
