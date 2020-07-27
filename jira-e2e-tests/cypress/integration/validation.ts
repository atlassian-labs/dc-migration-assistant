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


describe('Post migration verification', () => {
    const ctx = jira.getContext()

    before(() => {
        // Force reindex
        const issues = ["TEST-17", "TEST-18"].join(',')
        cy.request({
            url: ctx.restBaseURL+`/reindex/issue?issueID=${issues}`,
            method: "POST",
            headers: {"Origin": ctx.baseURL},
            auth: {
                user: 'admin',
                pass: ctx.password,
                sendImmediately: true,
            },
        })
    })

    it('Validate issues with inline attachment', () => {
        let req = {
            url: ctx.restBaseURL+"/issue/TEST-17",
            auth: {
                user: 'admin',
                pass: ctx.password,
                sendImmediately: true,
            },
        }
        cy.request(req)
            .its('body')
            .then((issue) => {
                console.log(issue)
                expect(issue).property('key').to.equal('TEST-17')
                expect(issue).property('fields').property('summary').to.contain('Test issue with inline image')
                expect(issue).property('fields').property('attachment').to.have.length(1)
                expect(issue.fields.attachment[0]).property('filename').to.equal('vbackground.png')
            })

    });
});
