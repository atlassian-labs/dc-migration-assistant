export const showsBlockUserWarning = () => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.contain('/warning');
    });
    cy.get('#dc-migration-assistant-root h1').contains('Step 5 of 7: Block user access');
    cy.get('#dc-migration-assistant-root section h1').contains('To block user access:');

    cy.get('#dc-migration-assistant-root ol > li').should(($lis) => {
        expect($lis).to.have.length(2);
        expect($lis.eq(0)).to.contain('Make sure that users are logged out');
        expect($lis.eq(1)).to.contain('Redirect the DNS to a maintenance page');
    });

    cy.get('#dc-migration-assistant-root input[type=checkbox]').should('not.be.checked');
    cy.get(`#dc-migration-assistant-root button[data-testid=button-next]`).contains('Next');
    cy.get('#dc-migration-assistant-root button[data-testid=button-cancel').contains('Cancel');

    cy.get('#dc-migration-assistant-root input[type=checkbox]')
        .check({ force: true })
        .should('be.checked');
    cy.get(`#dc-migration-assistant-root button[data-testid=button-next]`).should(
        'not.be.disabled'
    );
};

export const continueWithMigration = () => {
    cy.get(`#dc-migration-assistant-root button[data-testid=button-next]`)
        .should('not.be.disabled')
        .click();
};
