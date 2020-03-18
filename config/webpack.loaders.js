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

const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const PostCssIcssValuesPlugin = require('postcss-icss-values');

const { MY_I18N_FILES } = require('./webpack.constants');

function getLoaders({ isProductionEnv = false }) {
    return [
        {
            test: /\.(tsx|ts)?$/,
            exclude: /node_modules/,
            use: [
                {
                    loader: '@atlassian/i18n-properties-loader',
                    options: {
                        i18nFiles: MY_I18N_FILES,
                        disabled: isProductionEnv,
                    },
                },
            ],
        },
        {
            test: /\.less$/,
            use: [
                {
                    loader: isProductionEnv ? MiniCssExtractPlugin.loader : 'style-loader',
                    options: {
                        sourceMap: true,
                    },
                },
                {
                    loader: 'css-loader',
                    options: {
                        sourceMap: true,
                    },
                },
                {
                    loader: 'postcss-loader',
                    options: {
                        sourceMap: true,
                        plugins: [new PostCssIcssValuesPlugin()],
                    },
                },
                {
                    loader: 'less-loader',
                    options: {
                        sourceMap: true,
                    },
                },
            ],
        },
        {
            test: /\.(png|jpg|gif|svg)$/,
            use: [
                {
                    loader: 'file-loader',
                    options: {},
                },
            ],
        },
        {
            test: /\.soy$/,
            use: [
                ...((!isProductionEnv && [
                    {
                        loader: '@atlassian/i18n-properties-loader',
                        options: {
                            MY_I18N_FILES,
                        },
                    },
                ]) ||
                    []),
                {
                    loader: '@atlassian/soy-loader',
                    options: {
                        dontExpose: true,
                    },
                },
            ],
        },
        { test: /\.tsx?$/, loader: 'awesome-typescript-loader' },
    ];
}

module.exports.loaders = isProduction => getLoaders(isProduction);
