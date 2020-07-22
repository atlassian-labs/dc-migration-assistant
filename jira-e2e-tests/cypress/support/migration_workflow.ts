export const startMigration = (ctx: AppContext) => {
    cy.visit(ctx.pluginHomePage);
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.context + '/plugins/servlet/dc-migration-assistant/home');
    });
    cy.get('[data-test=start-migration]').should('exist').click();
};

export const fillCrendetialsOnAuthPage = (
    ctx: AppContext,
    region: string,
    credentials: AWSCredentials
) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/auth');
    });

    cy.get('[data-test=aws-auth-key]').type(credentials.keyId);
    cy.get('[data-test=aws-auth-secret]').type(credentials.secretKey);
    // FIXME: This may be flaky; the AtlasKit AsyncSelect
    // component is hard to instrument.
    cy.get('#region-uid3').click();
    cy.get(`[id^=react-select]:contains(${region})`).click();

    cy.get('[data-test=aws-auth-submit]').should('exist').click();
};

export const selectPrefixOnASIPage = (ctx: AppContext, prefix: string = 'ATL-') => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/asi');
    });

    cy.get('section').contains("We're scanning your AWS account for existing ASIs.");

    cy.get('section').contains('We found an existing ASI', { timeout: 20000 });
    cy.get('[name=deploymentMode]').check('existing');
    cy.get('.asi-select')
        .click()
        .then(() => {
            cy.get(`[id^=react-select]:contains(${prefix})`).click();
        });

    cy.get('[data-test=asi-submit]').contains('Next').click();
};

export const configureQuickStartFormWithoutVPC = (
    ctx: AppContext,
    values: CloudFormationFormValues
) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/provision');
    });
    cy.get('[name=stackName]').type(values.stackName);
    cy.get('[name=DBMasterUserPassword]').type(values.dbMasterPassword);
    cy.get('[name=DBPassword]').type(values.dbPassword);
    cy.get('[name=DBMultiAZ]').type(String(values.dbMultiAz || false), { force: true });
    cy.get('[name=CidrBlock]').type(values.cidrBlock || '0.0.0.0/0');
};

export const submitQuickstartForm = () => {
    cy.get('[data-test=qs-submit]').contains('Deploy').click();
};

export const waitForDeployment = (ctx: AppContext) => {
    cy.visit(ctx.pluginFullUrl + '/aws/provision/status');
    cy.server();
    cy.route('/jira/rest/dc-migration/1.0/aws/stack/status').as('provisioningStatus');
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/provision/status');
    });

    cy.get('#dc-migration-assistant-root h1').contains('Step 3 of 7: Deploy on AWS');
    cy.get('#dc-migration-assistant-root h4').contains('Deploying Jira infrastructure', {
        timeout: 10000,
    });
    cy.get('#dc-migration-assistant-root button').contains('Refresh').should('not.be.disabled');
    cy.get('#dc-migration-assistant-root button').contains('Cancel').should('not.be.disabled');

    const waitBetweenRetry = 20 * 1000;
    const retries = 100;
    waitForStatus('@provisioningStatus', 'CREATE_COMPLETE', waitBetweenRetry, retries);

    cy.get('#dc-migration-assistant-root button').contains('Next');
    cy.get('#dc-migration-assistant-root h4').contains('Deployment Complete');
};

const waitForStatus = (
    route: string,
    expectedStatus: string,
    waitPeriodInMs: number,
    maxRetries: number = 100,
    iteration: number = 1
) => {
    cy.wait(route).should((xhr) => {
        const provisioningStatus = (xhr.response.body as Cypress.ObjectLike)['status'];
        if (provisioningStatus === expectedStatus) {
            return;
        } else if (maxRetries < iteration) {
            throw Error('Maximum amount of retries reached');
        } else {
            cy.wait(waitPeriodInMs);
            waitForStatus(route, expectedStatus, waitPeriodInMs, iteration + 1);
        }
    });
};
