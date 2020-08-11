export const startMigration = (ctx: AppContext) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.context + '/plugins/servlet/dc-migration-assistant/home');
    });
    cy.get('[data-test=start-migration]').should('be.enabled').click({ timeout: 60000 });
};
