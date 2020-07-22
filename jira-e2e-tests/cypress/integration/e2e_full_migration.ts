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

import { waitForDeployment } from '../support/migration_workflow';
import { getContext } from '../support/jira';
import {
    configureQuickStartFormWithoutVPC,
    submitQuickstartForm,
    selectPrefixOnASIPage,
    fillCrendetialsOnAuthPage,
    startMigration,
} from '../support/migration_workflow';

const getAwsTokens = (): AWSCredentials => {
    return {
        keyId: Cypress.env('AWS_ACCESS_KEY_ID'),
        secretKey: Cypress.env('AWS_SECRET_ACCESS_KEY'),
    };
};

describe('Migration plugin', () => {
    const ctx = getContext();
    const region = 'ap-southeast-2';
    const testId = Math.random().toString(36).substring(2, 8);
    const credentials = getAwsTokens();

    before(() => {
        cy.on('uncaught:exception', (err, runnable) => false);
        expect(credentials.keyId, 'Set AWS_ACCESS_KEY_ID, see README.md').to.not.be.undefined;

        cy.jira_login(ctx, 'admin', 'admin');
        cy.reset_migration(ctx);
    });

    it('Run full migration', () => {
        startMigration(ctx);

        fillCrendetialsOnAuthPage(ctx, region, credentials);

        selectPrefixOnASIPage(ctx);

        configureQuickStartFormWithoutVPC(ctx, {
            stackName: `teststack-${testId}`,
            dbPassword: `XadD54^${testId}`,
            dbMasterPassword: `YadD54^${testId}`,
        });

        submitQuickstartForm();

        waitForDeployment(ctx);
    });
});
