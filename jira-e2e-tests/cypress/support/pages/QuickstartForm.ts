import { AppContext, CloudFormationFormValues } from '../common';

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
