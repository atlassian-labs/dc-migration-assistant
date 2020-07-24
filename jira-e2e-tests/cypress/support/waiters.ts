export const waitForStatus = (
    route: string,
    expectedStatus: string,
    iteration: number = 1,
    waitPeriodInMs: number = 20000,
    maxRetries: number = 100
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
            waitForStatus(route, expectedStatus, iteration + 1, waitPeriodInMs, maxRetries);
        }
    });
};

const finishedStatuses = ['CREATE_FAILED', 'DELETE_FAILED', 'DELETE_COMPLETE'];
