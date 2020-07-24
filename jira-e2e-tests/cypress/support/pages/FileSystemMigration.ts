import { waitForStatus } from '../waiters';

const header = 'Step 4 of 7: Copy Content';

export const startFileSystemInitialMigration = (ctx: AppContext) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/fs');
    });
    cy.get('h1').contains(header);
    cy.get('button').contains('Cancel');

    cy.get('button').contains('Start copying').click();
};

export const monitorFileSystemMigration = (ctx: AppContext) => {
    cy.visit(ctx.pluginHomePage);
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/fs');
    });
    cy.get('h1').contains(header);
    cy.get('button').contains('Refresh');

    cy.get('#dc-migration-assistant-root h4').contains('Counting and uploading your files to AWS');

    waitForStatus(ctx.context + '/rest/dc-migration/1.0/migration/fs/report', 'DONE');
};
