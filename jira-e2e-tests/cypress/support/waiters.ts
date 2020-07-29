export const waitForStatus = (
    route: string,
    expectedStatus: string,
    type: EndpointType,
    iteration: number = 1,
    waitPeriodInMs: number = 20000,
    maxRetries: number = 100
) => {
    cy.request({
        url: route,
        auth: { user: 'admin', password: Cypress.env('ADMIN_PASSWORD') },
    }).then((response) => {
        const provisioningStatus: string = getStatus(type, response);
        cy.log(`run #${iteration}, status ${provisioningStatus}`);
        if (provisioningStatus === expectedStatus) {
            return;
        } else if (finishedStatuses.includes(provisioningStatus)) {
            throw Error(`Provisioning finished with unexpected status ${provisioningStatus}`);
        } else if (maxRetries < iteration) {
            throw Error(`Maximum amount of retries reached (${maxRetries})`);
        } else {
            cy.wait(waitPeriodInMs);
            waitForStatus(route, expectedStatus, type, iteration + 1, waitPeriodInMs, maxRetries);
        }
    });
};

export enum EndpointType {
    FINAL_SYNC,
    FILESYSTEM_REPORT,
    PROVISONING,
    STAGE,
}

const getStatus = (type: EndpointType, response: Cypress.Response) => {
    const responseBody = response.body as Cypress.ObjectLike;
    switch (type) {
        case EndpointType.FINAL_SYNC:
            return responseBody['db']['status'];
        case EndpointType.PROVISONING:
            return responseBody['status'];
        case EndpointType.FILESYSTEM_REPORT:
            return responseBody['status'];
        case EndpointType.STAGE:
            return responseBody['stage'];
    }
};

const finishedStatuses = ['CREATE_FAILED', 'DELETE_FAILED', 'DELETE_COMPLETE'];
