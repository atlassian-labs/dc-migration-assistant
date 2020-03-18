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

import React from 'react';
import { render, waitForElement } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { act } from 'react-dom/test-utils';

import { QuickStartDeploy } from './QuickStartDeploy';

const FORM_HEADER_KEY = 'atlassian.migration.datacenter.provision.aws.form.title';
const FIRST_PARAM_GROUP_NAME = 'Group One';
const SECOND_PARAM_GROUP_NAME = 'Group Two';

const VALIDATION_TEST_PARAMETER_LABEL = 'Param Four';
const VALIDATION_TEST_PARAMETER_ERROR_MESSAGE = 'must be abc';

const VALIDATION_NUMBER_TEST_PARAMETER_LABEL = 'Param Three';
const VALIDATION_NUMBER_TEST_PARAMETER_MIN = 1;
const VALIDATION_NUMBER_TEST_PARAMETER_MAX = 3;
const VALIDATION_NUMBER_TEST_PARAMETER_ERROR_MESSAGE = `must be between ${VALIDATION_NUMBER_TEST_PARAMETER_MIN} and ${VALIDATION_NUMBER_TEST_PARAMETER_MAX}`;

const mockCfnYaml = `
ParameterGroups:
  - Label:
      default: ${FIRST_PARAM_GROUP_NAME}
    Parameters:
      - ParamOne
      - ParamTwo
  - Label:
      default: ${SECOND_PARAM_GROUP_NAME}
    Parameters:
      - ParamThree
      - ParamFour

ParameterLabels:
  ParamOne:
    default: Param One
  ParamTwo:
    default: Param Two
  ParamThree:
    default: ${VALIDATION_NUMBER_TEST_PARAMETER_LABEL}
  ParamFour:
    default: ${VALIDATION_TEST_PARAMETER_LABEL}

Parameters:
  ParamOne:
    Type: String
    Description: First Parameter
  ParamTwo:
    Type: String
    Description: Second Parameter
  ParamThree:
    Type: Number
    Description: Third Parameter
    MaxValue: ${VALIDATION_NUMBER_TEST_PARAMETER_MAX}
    MinValue: ${VALIDATION_NUMBER_TEST_PARAMETER_MIN}
    ConstraintDescription: ${VALIDATION_NUMBER_TEST_PARAMETER_ERROR_MESSAGE}
  ParamFour:
    Type: String
    AllowedPattern: abc
    ConstraintDescription: ${VALIDATION_TEST_PARAMETER_ERROR_MESSAGE}
    Description: Fourth Parameter
`;

const mockFetch = jest.fn();
mockFetch.mockReturnValue(Promise.resolve({ text: () => mockCfnYaml }));
window.fetch = mockFetch;

describe('Quick Start Provisioning Screen', () => {
    it('Should render', async () => {
        await act(async () => {
            const { getByText } = render(<QuickStartDeploy />);

            const formHeader = await waitForElement(() => getByText(FORM_HEADER_KEY));
            expect(formHeader).toBeTruthy();
        });
    });
    it('Should separate quickstart paramaters into their groups', async () => {
        await act(async () => {
            const { getByText } = render(<QuickStartDeploy />);

            const firstGroup = await waitForElement(() => getByText(FIRST_PARAM_GROUP_NAME));
            expect(firstGroup).toBeTruthy();

            const groupContainer = firstGroup.parentElement;
            // 2 parameters plus the group label
            expect(groupContainer.childElementCount).toEqual(3);

            const secondGroup = getByText(SECOND_PARAM_GROUP_NAME);
            expect(secondGroup).toBeTruthy();

            const secondGroupContainer = secondGroup.parentElement;
            // 2 parameters plus the group label
            expect(secondGroupContainer.childElementCount).toEqual(3);
        });
    });
    it('Should enforce validation on string parameters with constraints', async () => {
        await act(async () => {
            const { getByLabelText, getByText } = render(<QuickStartDeploy />);

            const validationTestParameter = await waitForElement(() =>
                getByLabelText(VALIDATION_TEST_PARAMETER_LABEL)
            );
            expect(validationTestParameter).toBeTruthy();

            await userEvent.type(validationTestParameter, 'd');

            const errorMessage = await waitForElement(() =>
                getByText(VALIDATION_TEST_PARAMETER_ERROR_MESSAGE)
            );
            expect(errorMessage).toBeTruthy();
        });
    });
    it('Should enforce validation on number parameters with constraints', async () => {
        await act(async () => {
            const { getByLabelText, getByText } = render(<QuickStartDeploy />);

            const validationNumberTestParameter = await waitForElement(() =>
                getByLabelText(VALIDATION_NUMBER_TEST_PARAMETER_LABEL)
            );
            expect(validationNumberTestParameter).toBeTruthy();

            await userEvent.type(validationNumberTestParameter, '0');

            const errorMessage = await waitForElement(() =>
                getByText(VALIDATION_NUMBER_TEST_PARAMETER_ERROR_MESSAGE)
            );
            expect(errorMessage).toBeTruthy();

            await userEvent.type(validationNumberTestParameter, '4');
            const errorMessageUpper = await waitForElement(() =>
                getByText(VALIDATION_NUMBER_TEST_PARAMETER_ERROR_MESSAGE)
            );
            expect(errorMessageUpper).toBeTruthy();
        });
    });
});
