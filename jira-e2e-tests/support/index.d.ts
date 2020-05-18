/// <reference types="cypress" />

declare namespace Cypress {

    interface Chainable {
        jira_login(ctx: any, uname: string, passwd: string): Chainable<Element>;
        jira_setup(): Chainable<Element>;
        reset_migration(): Chainable<Element>;
    }

}
