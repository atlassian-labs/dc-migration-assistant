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

const path = require('path');

const rootDir = path.join(__dirname, '..');
const srcDir = path.join(rootDir, 'src', 'main');

const I18N_SRC_DIR = path.join(srcDir, 'resources');
const FRONTEND_SRC_DIR = path.join(srcDir, 'dc-migration-assistant-fe');
const FRONTEND_OUTPUT_DIR = path.join(rootDir, 'target', 'classes');
const PLUGIN_KEY = 'com.atlassian.migration.datacenter.dc-migration-assistant';
const ENTRY_POINT = { 'dc-migration-assistant': path.join(FRONTEND_SRC_DIR, 'index.tsx') };

const MY_I18N_FILES = ['dc-migration-assistant.properties'].map(file => path.join(I18N_SRC_DIR, 'i18n', file));
const WRM_OUTPUT = path.resolve(
    './',
    'target',
    FRONTEND_OUTPUT_DIR,
    'META-INF',
    'plugin-descriptors',
    'wr-webpack-bundles.xml'
);

const JQUERY_EXTERNAL = {
    jquery: {
        commonjs: 'jquery',
        commonjs2: 'jquery',
        amd: 'jquery',
        root: 'jQuery',
        global: 'jQuery',
        var: 'jQuery',
    },
};

const DEV_SERVER_PORT = 3333;
const DEV_SERVER_HOST = 'localhost';

module.exports = {
    srcDir,
    I18N_SRC_DIR,
    FRONTEND_SRC_DIR,
    FRONTEND_OUTPUT_DIR,
    MY_I18N_FILES,
    WRM_OUTPUT,
    JQUERY_EXTERNAL,
    PLUGIN_KEY,
    ENTRY_POINT,
    DEV_SERVER_PORT,
    DEV_SERVER_HOST,
};
