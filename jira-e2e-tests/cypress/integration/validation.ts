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
import * as common from '../support/common'

describe('Post migration verification', () => {
    const ctx = jira.getContext()

    before(() => {
        // Force reindex after a migration
        jira.reindex(["TEST-17", "TEST-18"], ctx, ctx.baseURL)
    })

    it('Validate issues with inline attachment', () => {
        let req = {
            url: ctx.restBaseURL+"/issue/TEST-17",
            auth: ctx.restAuth,
        }
        let issue = cy.request(req)
            .its('body')
            .then((issue) => {
                expect(issue).property('key').to.equal('TEST-17')
                expect(issue).property('fields').property('summary').to.contain('Test issue with inline image')
                expect(issue).property('fields').property('attachment').to.have.length(1)
                expect(issue.fields.attachment[0]).property('filename').to.equal('vbackground.png')
                let imgURL = issue.fields.attachment[0].content;
                let thumbURL = issue.fields.attachment[0].thumbnail;
                return [imgURL, thumbURL]
            })
            .then(([imgURL, thumbURL]) => {
                cy.request({url: imgURL, auth: ctx.restAuth, encoding: 'binary'})
                    .then((resp) => {
                        expect(resp.headers).property('content-length').to.equal('2215660')
                        common.checksum(resp.body, "3393ce5431bcb31aea66541c3a1c6a56")
                    })
                cy.request({url: thumbURL, auth: ctx.restAuth,encoding: 'binary'})
                    .then((resp) => {
                        expect(resp.headers).property('content-length').to.equal('66466')
                        common.checksum(resp.body, "43ef675cf099a8d5108b1de45e221dac")
                    })
            })
    });

    it('Validate issues with large attachment', () => {
        let req = {
            url: ctx.restBaseURL+"/issue/TEST-18",
            auth: ctx.restAuth,
        }
        let issue = cy.request(req)
            .its('body')
            .then((issue) => {
                expect(issue).property('fields').property('attachment').to.have.length(1)
                expect(issue.fields.attachment[0]).property('filename').to.equal('random.bin')
                return issue.fields.attachment[0].content;
            })
            .then((binURL) => {
                cy.request({url: binURL, auth: ctx.restAuth, encoding: 'binary'})
                    .then((resp) => {
                        expect(resp.headers).property('content-length').to.equal('10485760')
                        common.checksum(resp.body, "cdb8239c10b894beef502af29eaa3cf1")
                    })
            })
    });

});
