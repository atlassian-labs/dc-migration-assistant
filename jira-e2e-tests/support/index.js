/// <reference types="Cypress" />


export const baseURL = 'http://localhost:2990/jira';
export const welcomeURL = baseURL+'/secure/WelcomeToJIRA.jspa'
export const loginURL = baseURL+'/login.jsp';
export const sudoURL = baseURL+'/secure/admin/WebSudoAuthenticate!default.jspa';
export const migrationBase = baseURL+'/plugins/servlet/dc-migration-assistant';
export const migrationHome = migrationBase;

Cypress.Commands.add('jira_login', (uname, passwd) => {
    cy.visit(loginURL);

    cy.get('#login-form-username').type('admin');
    cy.get('#login-form-password').type('admin');
    cy.get('#login-form-submit').click();
    // Force wait for dashboard to avoid flakiness.
    cy.get('[class=g-intro]').should('exist');

    // Ensure we have full admin access before doing anything
    cy.visit(sudoURL);
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
