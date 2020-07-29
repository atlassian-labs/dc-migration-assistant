export const showsValidationPage = (): string? => {
    let serviceUrl = null

    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.contain('/plugins/servlet/dc-migration-assistant/validation');
    });
    cy.get('#dc-migration-assistant-root h1').contains('Step 7 of 7: Validation');
    cy.get('#dc-migration-assistant-root p > a')
        .contains('http://')
        .then((href) => {
            const hval = href.attr('href');
            if (typeof hval === 'string') {
                serviceUrl = hval
                cy.log(serviceUrl)
            }
        });

    cy.get('#dc-migration-assistant-root section h1').contains(
        'To complete the setup of your new deployment'
    );

    cy.get('#dc-migration-assistant-root ul > li').should(($lis) => {
        expect($lis).to.have.length(5);
    });

    cy.get('button').should('contain.text', 'Close the migration app').and('be.disabled');

    cy.get('input[type=checkbox]')
        .should('not.be.checked')
        .click({ force: true })
        .should('be.checked');

    cy.get('button').should('contain.text', 'Close the migration app').and('be.enabled').click();

    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.contain('/home');
    });

    return serviceUrl
};
