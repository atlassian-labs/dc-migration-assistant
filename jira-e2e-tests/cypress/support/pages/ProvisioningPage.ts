export const waitForProvisioning = (ctx: AppContext) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/aws/provision/status');
    });

    cy.get('#dc-migration-assistant-root h1').contains('Step 3 of 7: Deploy on AWS');
    cy.get('#dc-migration-assistant-root h4').contains('Deploying', {
        timeout: 20000,
    });
    cy.get('#dc-migration-assistant-root button').contains('Refresh').should('not.be.disabled');
    cy.get('#dc-migration-assistant-root button').contains('Cancel').should('not.be.disabled');

    const waitBetweenRetry = 20 * 1000;
    const retries = 100;
    waitForStatus(
        ctx.context + '/rest/dc-migration/1.0/aws/stack/status',
        'CREATE_COMPLETE',
        waitBetweenRetry,
        retries
    );

    // we need to wait for the button to switch to Next as have different interval to fetch
    // provisioning status via Cypress comapring to the frontend
    cy.get('#dc-migration-assistant-root button', { timeout: 10000 }).contains('Next');
    cy.get('#dc-migration-assistant-root h4').contains('Deployment Complete');
};

const waitForStatus = (
    route: string,
    expectedStatus: string,
    waitPeriodInMs: number,
    maxRetries: number = 100,
    iteration: number = 1
) => {
    cy.request(route).then((response) => {
        const provisioningStatus = (response.body as Cypress.ObjectLike)['status'];
        cy.log(`run #${iteration}, status ${provisioningStatus}`);
        if (provisioningStatus === expectedStatus) {
            return;
        } else if (finishedStatuses.includes(provisioningStatus)) {
            throw Error(`Provisioning finished with unexpected status ${provisioningStatus}`);
        } else if (maxRetries < iteration) {
            throw Error(`Maximum amount of retries reached (${maxRetries})`);
        } else {
            cy.wait(waitPeriodInMs);
            waitForStatus(route, expectedStatus, waitPeriodInMs, iteration + 1);
        }
    });
};

const finishedStatuses = ['CREATE_FAILED', 'DELETE_FAILED', 'DELETE_COMPLETE'];
