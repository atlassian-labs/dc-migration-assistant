/// <reference types="Cypress" />

export const gen_context = (base, context) => {
    const baseURL = base+context;
    const migrationBase = baseURL+'/plugins/servlet/dc-migration-assistant';

    return {
        base: base,
        context: context,
        baseURL: baseURL,
        welcomeURL: baseURL+'/secure/WelcomeToJIRA.jspa',
        loginURL: baseURL+'/login.jsp',
        sudoURL: baseURL+'/secure/admin/WebSudoAuthenticate!default.jspa',
        upmURL: baseURL+'/plugins/servlet/upm',
        migrationBase: migrationBase,
        migrationHome: migrationBase+'/home';
    };
};

export const amps_context = gen_context('http://localhost:2990', '/jira');
export const devserver_context = gen_context('http://localhost:3333', '');
export const compose_context = gen_context('http://jira:8080', '/jira');


Cypress.Commands.add('jira_login', (ctx, uname, passwd) => {
    cy.visit(ctx.loginURL);

    cy.get('#login-form-username').type('admin');
    cy.get('#login-form-password').type('admin');
    cy.get('#login-form-submit').click();
    // Force wait for dashboard to avoid flakiness.
    //cy.get('[class=g-intro]').should('exist');

    // Ensure we have full admin access before doing anything
    cy.visit(ctx.sudoURL);
    cy.get('#login-form-authenticatePassword').type('admin');
    cy.get('#login-form-submit').click();
})


Cypress.Commands.add('jira_setup', () => {
    // Language
    cy.get("#next").click();

    // Avatar
    cy.get("avatar-picker-done").click();

    // Create sample project
    cy.get("#sampleData").click();
    cy.get("create-project-dialog-create-button").click();
    cy.get("#next").type("Test");
    cy.get("add-project-dialog-create-button").click();
});


Cypress.Commands.add('reset_migration', () => {
    cy.visit(migrationHome);
    cy.get('#dc-migration-assistant-root').should('exist');
    cy.window().then((window: Window) => {
        window.AtlassianMigration.resetMigration();
    });
});
