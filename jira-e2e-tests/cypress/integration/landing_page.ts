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

import { getContext } from '../support/jira';

describe('Landing page', () => {
    const ctx: AppContext = getContext();

    beforeEach(() => {
        cy.server();
        cy.jira_login(ctx);
        cy.reset_migration(ctx);
    });

    it('should display warning and disable button when not ready for migration', () => {
        cy.route({
            method: 'GET',
            url: ctx.context + '/rest/dc-migration/1.0/migration/ready',
            response: {
                dbCompatible: false,
                osCompatible: true,
                pgDumpAvailable: false,
                pgDumpCompatible: true,
            },
        });

        cy.visit(ctx.pluginHomePage);

        cy.get('#dc-migration-assistant-root h1').contains('Jira Data Center Migration App');

        cy.get('#dc-migration-assistant-root ul > li').should(($lis) => {
            expect($lis).to.have.length(5);
            expect($lis.eq(0)).to.contain('PostgreSQL').contain('Incompatible');
            expect($lis.eq(1)).to.contain('Linux').contain('OK');
            expect($lis.eq(2)).to.contain('pg_dump').contain('Incompatible');
            expect($lis.eq(3)).to.contain('pg_dump').contain('version').contain('OK');
        });

        cy.get('button[data-test=start-migration]').should('be.disabled');
    });
});
