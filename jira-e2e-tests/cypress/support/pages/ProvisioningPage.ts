export const waitForProvisioning = (ctx: AppContext) => {
    cy.visit(ctx.pluginFullUrl + '/aws/provision/status');
    cy.server();
    cy.route('/jira/rest/dc-migration/1.0/aws/stack/status').as('provisioningStatus');
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/provision/status');
    });

    cy.get('#dc-migration-assistant-root h1').contains('Step 3 of 7: Deploy on AWS');
    cy.get('#dc-migration-assistant-root h4').contains('Deploying Jira infrastructure', {
        timeout: 20000,
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
        cy.log(`run #${iteration}, status ${provisioningStatus}`);
        if (provisioningStatus === expectedStatus) {
            return;
        }
        if (finishedStatutes.includes(provisioningStatus)) {
            throw Error(`Provisioning finished with unexpected status ${provisioningStatus}`);
        } else if (maxRetries < iteration) {
            throw Error('Maximum amount of retries reached');
        } else {
            cy.wait(waitPeriodInMs);
            waitForStatus(route, expectedStatus, waitPeriodInMs, iteration + 1);
        }
    });
};

const finishedStatutes = ['CREATE_FAILED', 'DELETE_FAILED', 'DELETE_COMPLETE'];
