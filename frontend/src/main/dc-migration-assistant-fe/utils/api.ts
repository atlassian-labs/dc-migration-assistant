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

import contextPathWrapper from './context-path-wrapper';

type HTTPMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

type FetchHeader = Record<string, string>;
type FetchOption = string | FetchHeader;
type FetchOptions = Record<string, FetchOption>;

const addToOptionsIfExists = (
    newOption: FetchOption,
    currentOptions: FetchOptions
): FetchOptions => {
    if (newOption) {
        return {
            ...currentOptions,
            newOption,
        };
    }
    return currentOptions;
};

export const callAppRest = (
    method: HTTPMethod,
    path: string,
    body?: Record<string, any>,
    headers?: Record<string, string>,
    queryParams?: string
): Promise<Response> => {
    let options: FetchOptions = {
        method,
        headers: { 'Content-Type': 'application/json', ...headers },
    };

    if (body) {
        options = { ...options, body: JSON.stringify(body) };
    }

    options = addToOptionsIfExists(headers, options);

    const basePath = `${contextPathWrapper()}/rest/dc-migration/1.0/${path}`;
    const callPath = queryParams ? `${basePath}?${queryParams}` : basePath;

    return fetch(callPath, options).then(response => {
        if (response.status === 401) {
            window.location.reload();
        }
        return response;
    });
};

export enum RestApiPathConstants {
    awsCredentialsStorePath = `aws/configure`,
    awsRegionListPath = `aws/global-infrastructure/regions`,
    awsAzListForRegion = `aws/availabilityZones`,
}
