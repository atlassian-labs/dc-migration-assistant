export const fillCrendetialsOnAuthPage = (
    ctx: AppContext,
    region: string,
    credentials: AWSCredentials
) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/auth');
    });

    cy.get('[data-test=aws-auth-key]').type(credentials.keyId, { log: false });
    cy.get('[data-test=aws-auth-secret]').type(credentials.secretKey, { log: false });
    // FIXME: This may be flaky; the AtlasKit AsyncSelect
    // component is hard to instrument.
    cy.get('#region-uid3').click();
    cy.get(`[id^=react-select]:contains(${region})`).click();

    cy.get('[data-test=aws-auth-submit]').should('exist').click();
};
