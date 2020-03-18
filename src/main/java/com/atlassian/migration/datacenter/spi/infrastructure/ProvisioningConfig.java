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

package com.atlassian.migration.datacenter.spi.infrastructure;


import java.util.Map;

public class ProvisioningConfig {

    private String templateUrl;
    private String stackName;
    private Map<String, String> params;

    public ProvisioningConfig() {
    }

    public ProvisioningConfig(String templateUrl, String stackName, Map<String, String> params) {
        this.templateUrl = templateUrl;
        this.stackName = stackName;
        this.params = params;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public String getStackName() {
        return stackName;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
