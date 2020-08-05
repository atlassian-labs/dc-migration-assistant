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

import type { AppContext } from './common'
import * as common from './common'

export const createAppContext = (
    base: string,
    contextPath: string,
    username: string = 'admin'
): AppContext => {
    const baseURL = base + contextPath;
    const pluginPathWithContext = contextPath + '/plugins/servlet/dc-migration-assistant';
    const pluginFullUrl = base + pluginPathWithContext;
    const password = Cypress.env('ADMIN_PASSWORD')
    assert.isDefined(
        password,
        'You need to define admin password via `CYPRESS_ADMIN_PASSWORD env variable`'
    );

    const jiraContext: AppContext = {
        username: username,
        password: password,
        base: base,
        context: contextPath,
        baseURL: baseURL,
        welcomeURL: baseURL + '/secure/WelcomeToJIRA.jspa',
        loginURL: baseURL + '/login.jsp',
        sudoURL: baseURL + '/secure/admin/WebSudoAuthenticate!default.jspa',
        upmURL: baseURL + '/plugins/servlet/upm',
        restBaseURL: baseURL + '/rest/api/2',
        restAuth: {
            user: username,
            pass: password,
            sendImmediately: true,
        },
        pluginPath: pluginPathWithContext,
        pluginFullUrl: pluginFullUrl,
        pluginHomePage: pluginFullUrl + '/home',
    };
    return jiraContext;
};

export const ampsContext = createAppContext('http://localhost:2990', '/jira');
export const fsDevServerContext = createAppContext('http://localhost:3333', '');
export const dockerComposeContext = createAppContext('http://jira:8080', '/jira');
export const dockerLocalContext = createAppContext('http://localhost:2990', '/jira');

/**
 *  Returns application context to access product and plugin URLs
 */
export const getContext = () => {
    const context = Cypress.env('CONTEXT');
    console.log(context);
    switch (context) {
        case 'amps': {
            return ampsContext;
        }
        case 'fsdev': {
            return fsDevServerContext;
        }
        case 'local': {
            return dockerLocalContext;
        }
        case 'compose':
        default:
            return dockerComposeContext;
    }
};


export const reindex = (issues: string[], ctx: AppContext, targetURL: string) => {
    cy.request({
        url: targetURL+`/rest/api/2/reindex/issue?issueID=${issues.join(",")}`,
        method: "POST",
        headers: {"Origin": targetURL},
        auth: ctx.restAuth,
    })
}

export const validate_issue = (issueKey: string, ctx: AppContext, targetURL: string, attName: string?, attHash: string?, attThumbHash: string?) => {
    cy.log(targetURL)
    reindex([issueKey], ctx, targetURL);

    let req = {
        url: `${ctx.restBaseURL}/issue/${issueKey}`,
        auth: ctx.restAuth,
    }
    cy.request(req)
        .its('body')
        .then((issue) => {
            expect(issue).property('key').to.equal(issueKey)
            expect(issue).property('fields').property('attachment').to.have.length(1)
            if (attName != null && attName != undefined) {
                expect(issue.fields.attachment[0]).property('filename').to.equal(attName)
                let imgURL = issue.fields.attachment[0].content;
                let thumbURL = issue.fields.attachment[0].thumbnail;
                return [imgURL, thumbURL]
            } else {
                return [null, null]
            }
        })
        .then(([imgURL, thumbURL]) => {
            if (imgURL != null) {
                cy.request({url: imgURL, auth: ctx.restAuth, encoding: 'binary'})
                    .then((resp) => {
                        common.checksum(resp.body, attHash)
                    })
            }
            if (thumbURL != null) {
                cy.request({url: thumbURL, auth: ctx.restAuth,encoding: 'binary'})
                    .then((resp) => {
                        common.checksum(resp.body, attThumbHash)
                    })
            }
        })
}
