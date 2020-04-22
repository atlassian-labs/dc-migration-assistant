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

export type QuickStartParameterYamlNode = {
    Type: string;
    Default?: string | number | boolean;
    Description: string;
    AllowedValues?: Array<string | boolean>;
    ConstraintDescription?: string;
    AllowedPattern?: string;
    MaxLength?: number;
    MinLength?: number;
    MaxValue?: number;
    MinValue?: number;
    NoEcho?: boolean;
};

export type QuickstartParamLabelYamlNode = {
    default: string;
};

export type QuickstartParamGroupYamlNode = {
    Label: QuickstartParamLabelYamlNode;
    Parameters: Array<string>;
};

export type QuickstartParameterGroup = {
    groupLabel: string;
    shouldExpandGroupOnLoad: boolean;
    parameters: Array<QuickstartParameter>;
};

export type QuickstartParameterProperties = QuickStartParameterYamlNode;

export type QuickstartParameter = {
    paramKey: string;
    paramLabel: string;
    paramProperties: QuickstartParameterProperties;
};
