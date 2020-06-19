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

import * as jira from '../support';

// Set these externally via e.g:
//
//     export CYPRESS_AWS_ACCESS_KEY_ID=xxxx
//     export CYPRESS_AWS_SECRET_ACCESS_KEY='yyyyyy'
//
const getAwsTokens = (): [string, string] => {
    return [Cypress.env('AWS_ACCESS_KEY_ID'),
            Cypress.env('AWS_SECRET_ACCESS_KEY')];
};

describe('Database Migration page', () => {
    beforeEach(() => {
        cy.on('uncaught:exception', (err, runnable) => false);

        cy.jira_login('admin', 'admin');
        cy.reset_migration();
    });

    it('Can provision a cloudformation template', () => {
        let testid =  Math.random().toString(36).substring(2, 8);
        let region = 'ap-southeast-2';

        // Home; should be no migration; start one
        cy.visit(jira.migrationHome);
        cy.get('[data-test=start-migration]')
            .should('exist')
            .click();

        // AWS auth page.
        cy.location().should((loc: Location) => {
            expect(loc.pathname).to.eq("/jira/plugins/servlet/dc-migration-assistant/aws/auth")
        });
        let [key, secret] = getAwsTokens();
        cy.get('[data-test=aws-auth-key]').type(key);
        cy.get('[data-test=aws-auth-secret]').type(secret);
        // FIXME: This may be flaky; the AtlasKit AsyncSelect
        // component is hard to instrument.
        cy.get('#region-uid3').click();
        cy.get(`[id^=react-select]:contains(${region})`).click();
        cy.get('[data-test=aws-auth-submit]').click();

        // Quickstart page; bare minimum config
        // Note: These names are generated from the QS yaml
        cy.get('[name=stackName]').type('teststack-'+testid);
        cy.get('[name=DBMasterUserPassword]').type('LKJLKJLlkjlkjl7987987#');
        cy.get('[name=DBPassword]').type('LKJLKJLlkjlkjl7987987#');
        cy.get('[name=DBMultiAZ]').type('false', {force: true});
        cy.get('[name=AccessCIDR]').type('0.0.0.0/0');
        cy.get('[name=KeyPairName]').type('taskcat-ci-key');
        cy.get('#AvailabilityZones-uid28').click();
        cy.get('#react-select-11-option-0').click();
        cy.get('#AvailabilityZones-uid28').click();
        cy.get('#react-select-11-option-1').click();
        cy.get('[name=ExportPrefix]').type('TEST-VPC-'+testid+'-');
        cy.get('[data-test=qs-submit]').click();

    });

});
