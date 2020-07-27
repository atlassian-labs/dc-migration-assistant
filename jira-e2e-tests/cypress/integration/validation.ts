/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/// <reference types="Cypress" />
import * as jira from '../support/jira';
import * as scenarios from '../support/scenarios';
import * as crypto from 'crypto'
import fs from 'fs'

const checksum = (data: Uint8Array, sum: string) => {
    let fname = 'temp-'+crypto.randomBytes(4).readUInt32LE(0)+'.bin';
    cy.writeFile(fname, data, 'binary')
    cy.exec(`md5sum ${fname} ; rm ${fname}`)
        .its('stdout')
        .then((out) => {
            expect(out.split(' ')[0]).to.equal(sum)
        })
}

describe('Post migration verification', () => {
    const ctx = jira.getContext()
    const auth = {
        user: 'admin',
        pass: ctx.password,
        sendImmediately: true,
    }

    before(() => {
        // Force reindex
        const issues = ["TEST-17", "TEST-18"].join(',')
        cy.request({
            url: ctx.restBaseURL+`/reindex/issue?issueID=${issues}`,
            method: "POST",
            headers: {"Origin": ctx.baseURL},
            auth: auth,
        })
    })

    it('Validate issues with inline attachment', () => {
        let req = {
            url: ctx.restBaseURL+"/issue/TEST-17",
            auth: auth,
        }
        let issue = cy.request(req)
            .its('body')
            .then((issue) => {
                console.log(issue)
                expect(issue).property('key').to.equal('TEST-17')
                expect(issue).property('fields').property('summary').to.contain('Test issue with inline image')
                expect(issue).property('fields').property('attachment').to.have.length(1)
                expect(issue.fields.attachment[0]).property('filename').to.equal('vbackground.png')
                let imgURL = issue.fields.attachment[0].content;
                let thumbURL = issue.fields.attachment[0].thumbnail;
                return [imgURL, thumbURL]
            })
            .then(([imgURL, thumbURL]) => {
                cy.request({url: imgURL, auth: auth, encoding: 'binary'})
                    .then((resp) => {
                        expect(resp.headers).property('content-length').to.equal('2215660')
                        checksum(resp.body, "3393ce5431bcb31aea66541c3a1c6a56")
                    })
                cy.request({url: thumbURL, auth: auth,encoding: 'binary'})
                    .then((resp) => {
                        expect(resp.headers).property('content-length').to.equal('66466')
                        checksum(resp.body, "43ef675cf099a8d5108b1de45e221dac")
                    })
            })
    });

    it('Validate issues with large attachment', () => {
        let req = {
            url: ctx.restBaseURL+"/issue/TEST-18",
            auth: auth,
        }
        let issue = cy.request(req)
            .its('body')
            .then((issue) => {
                console.log(issue)
                expect(issue).property('fields').property('attachment').to.have.length(1)
                expect(issue.fields.attachment[0]).property('filename').to.equal('random.bin')
                return issue.fields.attachment[0].content;
            })
            .then((binURL) => {
                cy.request({url: binURL, auth: auth, encoding: 'binary'})
                    .then((resp) => {
                        expect(resp.headers).property('content-length').to.equal('10485760')
                        checksum(resp.body, "cdb8239c10b894beef502af29eaa3cf1")
                    })
            })
    });

});
