export const startMigration = (ctx: AppContext) => {
    cy.visit(ctx.pluginHomePage);
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.context + '/plugins/servlet/dc-migration-assistant/home');
    });
    cy.get('[data-test=start-migration]').should('exist').click();
};
