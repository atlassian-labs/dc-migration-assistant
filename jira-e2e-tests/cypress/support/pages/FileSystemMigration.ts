export const startFileSystemInitialMigration = (ctx: AppContext) => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/fs');
    });
    cy.get('h1').contains('Copy Content');
    cy.get('button').contains('Cancel');

    cy.get('button').contains('Start copying').click();
};

export const monitorFileSystemMigration = (ctx: AppContext) => {
    cy.visit(ctx.pluginHomePage);
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.pluginPath + '/fs');
    });
    cy.get('h1').contains('Step 4 of 7: Copy Content');
    cy.get('button').contains('Refresh');

    cy.get('#dc-migration-assistant-root h4').contains('Counting and uploading your files to AWS');
};

/*
Step 5 of 7: Block user access
We're about to copy your database and sync all remaining content. That means you'll need to prevent users from accessing your current instance. User access during the next phase could prevent us from migrating all your data.

Learn more

To block user access:
Make sure that users are logged out
Redirect the DNS to a maintenance page

I'm ready for the next step
*/


Step 4 of 7: Copy Content
Copy your instance's content to the AWS infrastructure you just deployed. This might take several hours (no downtime), depending on how much content you have. You can close this page and return anytime to check its progress. Meanwhile, we recommend you start preparing for the next phase which will involve blocking user access to your instance.

Loading files into target application
Started at 23/Jul/20 5:00 PM

0 hours, 0 minutes, 51 seconds elapsed