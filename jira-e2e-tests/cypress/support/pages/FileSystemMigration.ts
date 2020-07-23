export const startFileSystemInitialMigration = (ctx: AppContext) => {
    cy.get('h1').contains('Copy Content');
    cy.get('button').contains('Cancel');

    cy.get('button').contains('Start copying').click();
};
