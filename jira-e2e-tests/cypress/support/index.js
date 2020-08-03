/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/// <reference types="Cypress" />

Cypress.Commands.add('jira_login', (ctx) => {
    cy.visit(ctx.loginURL);

    cy.get('#login-form-username').type(ctx.username);
    cy.get('#login-form-password').type(ctx.password, { log: false });
    cy.get('#login-form-submit').click();
    // Force wait for dashboard to avoid flakiness.
    cy.wait(20000)

    // Ensure we have full admin access before doing anything
    cy.visit(ctx.sudoURL);
    cy.get('#login-form-authenticatePassword').type(ctx.password, { log: false });
    cy.get('#login-form-submit').click();
    cy.wait(20000)
});

Cypress.Commands.add('jira_setup', () => {
    // Language
    cy.get('#next').click();

    // Avatar
    cy.get('avatar-picker-done').click();

    // Create sample project
    cy.get('#sampleData').click();
    cy.get('create-project-dialog-create-button').click();
    cy.get('#next').type('Test');
    cy.get('add-project-dialog-create-button').click();
});

Cypress.Commands.add('reset_migration', (ctx) => {
    cy.visit(ctx.pluginHomePage);
    cy.get('#dc-migration-assistant-root').should('exist');
    cy.window().then((window) => {
        window.AtlassianMigration.resetMigration();
    });
});
