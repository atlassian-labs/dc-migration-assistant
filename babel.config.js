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

module.exports = api => {
    const isTest = api.env('test');
    const testOnly = (entry, defaultValue = {}) => (isTest ? entry : defaultValue);
    return {
        presets: [
            ['@babel/preset-env', { modules: testOnly('commonjs', false) }],
            '@babel/preset-react',
        ],
        plugins: [
            '@babel/plugin-syntax-dynamic-import',
            '@babel/plugin-proposal-class-properties',
            '@babel/plugin-transform-runtime',
            'babel-plugin-styled-components',
        ],
    };
};
