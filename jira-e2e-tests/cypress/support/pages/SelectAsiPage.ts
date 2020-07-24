export const selectPrefixOnASIPage = (ctx: AppContext, prefix: string = 'ATL-') => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/asi');
    });

    cy.get('section').contains(
        'Were scanning your AWS account for existing ASIs. This may take a while.'
    );

    cy.get('section').contains('We found an existing ASI', { timeout: 20000 });
    cy.get('[name=deploymentMode]').check('existing');
    cy.get('.asi-select')
        .click()
        .then(() => {
            cy.get(`[id^=react-select]:contains(${prefix})`).click();
        });

    cy.get('[data-test=asi-submit]').contains('Next').click();
};
