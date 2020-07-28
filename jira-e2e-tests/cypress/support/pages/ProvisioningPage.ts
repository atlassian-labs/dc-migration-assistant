import { waitForStatus, EndpointType } from '../waiters';
import { startFileSystemInitialMigration } from './FileSystemMigration';

export const waitForProvisioning = (ctx: AppContext) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).contain('/aws/provision/status');
    });

    cy.get('#dc-migration-assistant-root h1').contains('Step 3 of 7: Deploy on AWS');
    cy.get('#dc-migration-assistant-root h4').contains('Deploying', {
        timeout: 20000,
    });
    cy.get('button[data-testid=button-refresh]').should('not.be.disabled');
    cy.get('button[data-testid=button-cancel]').should('not.be.disabled');

    waitForStatus(
        ctx.context + '/rest/dc-migration/1.0/migration',
        'provision_migration_stack_wait',
        EndpointType.STAGE
    );
    cy.log('Provisioned application stack');

    // verify completion of the stack (by verifying previous migration stage we know there is application stack)
    waitForStatus(
        ctx.context + '/rest/dc-migration/1.0/aws/stack/status',
        'CREATE_COMPLETE',
        EndpointType.PROVISONING
    );
    cy.log('Provisioned migration stack stack');

    cy.get('#dc-migration-assistant-root h4').contains('Deployment Complete', { timeout: 60000 });

    cy.get('button[data-testid=button-next]').contains('Next').click();

    cy.relogin(ctx);
};
