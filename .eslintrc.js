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

module.exports = {
    root: true,
    extends: [
        'airbnb',
        'plugin:prettier/recommended',
        'prettier/react',
        'plugin:@typescript-eslint/recommended',
        'plugin:react/recommended',
    ],
    plugins: ['react-hooks'],
    env: {
        es6: true,
        node: true,
    },
    parser: '@typescript-eslint/parser',
    parserOptions: {
        ecmaVersion: 7,
        ecmaFeatures: {
            jsx: true,
        },
        sourceType: 'module',
    },
    rules: {
        '@typescript-eslint/no-explicit-any': 'off',
        'import/no-webpack-loader-syntax': 'off',
        'import/prefer-default-export': 'off',
        'import/extensions': ['error', 'never'],
        'react/prop-types': 'off',
        'prettier/prettier': 'error',
        'react/jsx-boolean-value': 0,
        'react/jsx-filename-extension': 0,
        'react/jsx-fragments': ['error', 'syntax'],
        'react/jsx-max-depth': [1, { max: 4 }],
        'react/jsx-no-useless-fragment': 'error',
        'react/jsx-props-no-spreading': 0,
        'no-shadow': 'warn',
        'react/require-default-props': 'warn',
        'no-undef': 'warn',
        'no-param-reassign': 'warn',
        'react/destructuring-assignment': 'warn',
        'import/no-extraneous-dependencies': ["error", {"devDependencies": ["**/*.test.tsx", "**/*.test.ts"]}],
        'jsx-a11y/label-has-associated-control': 'warn',
        'no-return-assign': 'warn',
        'react/static-property-placement': 'warn',
        'react/sort-comp': 'warn',
        'class-methods-use-this': 'warn',
        'react/state-in-constructor': 'warn',
        'consistent-return': 'warn',
        'react/no-access-state-in-setstate': 'warn',
        radix: 'warn',
        'react/no-did-update-set-state': 'warn',
        'import/no-cycle': 'warn',
        'import/named': 'warn',
        'no-prototype-builtins': 'warn',
        'import/order': 'warn',
        'no-unused-expressions': 'warn',
        'import/no-unresolved': 'warn',
        'react/forbid-component-props': 'warn',
    },
    settings: {
        'import/resolver': {
            webpack: {
                config: 'config/webpack.config.js',
            },
        },
        react: {
            version: 'detect',
        },
    },
    overrides: [
        {
            files: ['*test.js*'],
            env: {
                jest: true,
            },
        },
    ],
};

