import * as crypto from 'crypto'


type AppContext = {
    username: string;
    password: string;
    base: string;
    context: string;
    baseURL: string;
    welcomeURL: string;
    loginURL: string;
    sudoURL: string;
    upmURL: string;
    restBaseURL: string;
    pluginPath: string;
    pluginFullUrl: string;
    pluginHomePage: string;
};

type CloudFormationFormValues = {
    stackName: string;
    dbMasterPassword: string;
    dbPassword: string;
    dbMultiAz?: boolean;
    cidrBlock?: string;
};

type AWSCredentials = {
    keyId: string;
    secretKey: string;
};


export const checksum = (data: Uint8Array, sum: string) => {
    let fname = 'temp-'+crypto.randomBytes(4).readUInt32LE(0)+'.bin';
    cy.writeFile(fname, data, 'binary')
    cy.exec(`md5sum ${fname} ; rm ${fname}`)
        .its('stdout')
        .then((out) => {
            expect(out.split(' ')[0]).to.equal(sum)
        })
}
