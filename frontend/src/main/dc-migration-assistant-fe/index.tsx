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
import ReactDOM from 'react-dom';
import whenDomReady from 'when-dom-ready';
import { hot } from 'react-hot-loader';

import { MigrationAssistant } from './components/MigrationAssistant';

whenDomReady().then(function example() {
    const container = document.getElementById('dc-migration-assistant-root');
    // eslint-disable-next-line no-undef
    const AwsMigrationPlugin = hot(module)(() => <MigrationAssistant />);
    ReactDOM.render(<AwsMigrationPlugin />, container);
});
