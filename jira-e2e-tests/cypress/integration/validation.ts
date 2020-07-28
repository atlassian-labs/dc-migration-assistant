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

// NOTE: This a self-test run against the source server and not
// intended to be run normally. `validate_issue` should be called
// against the migrated server.
describe('Post migration verification', () => {
    const ctx = jira.getContext()


    it('Validate issues with inline attachment', () => {
        jira.validate_issue("TEST-17", ctx, ctx.baseURL, "vbackground.png", "3393ce5431bcb31aea66541c3a1c6a56", "43ef675cf099a8d5108b1de45e221dac")
    });

    it('Validate issues with large attachment', () => {
        jira.validate_issue("TEST-18", ctx, ctx.baseURL, "random.bin", "cdb8239c10b894beef502af29eaa3cf1", null)
    });

});
