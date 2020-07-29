/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "as is" basis,
 * without warranties or conditions of any kind, either express or implied.
 * see the license for the specific language governing permissions and
 * limitations under the license.
 */

import * as crypto from 'crypto'

export type AppContext = {
    username: string;
    password: string;
    base: string;
    context: string;
    baseurl: string;
    welcomeurl: string;
    loginurl: string;
    sudoURL: string;
    upmURL: string;
    restBaseURL: string;
    restAuth: any;
    pluginPath: string;
    pluginFullUrl: string;
    pluginHomePage: string;
};

export type CloudFormationFormValues = {
    stackName: string;
    dbMasterPassword: string;
    dbPassword: string;
    dbMultiAz?: boolean;
    cidrBlock?: string;
};

export type AWSCredentials = {
    keyId: string;
    secretKey: string;
};


/**
 * Simple MD5 checksum of data; uses the system md5sum util and simple
 * tempfile generator to avoid adding additional dependencies.
 */
export const checksum = (data: Uint8Array, sum: string?) => {
    if (sum == null) return

    let fname = 'temp-'+crypto.randomBytes(4).readUInt32LE(0)+'.bin';
    cy.writeFile(fname, data, 'binary')
    cy.exec(`md5sum ${fname} ; rm ${fname}`)
        .its('stdout')
        .then((out) => {
            expect(out.split(' ')[0]).to.equal(sum)
        })
}
