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

package com.atlassian.migration.datacenter.analytics;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

public class AnalyticsWhitelistTest
{

    @Test
    void validateWhitelist() throws Exception{
        File file = new File(getClass().getResource("/whitelist/analytics_whitelist.json").getFile());
        String whitelist = FileUtils.readFileToString(file, "UTF-8");
        JsonParser parser = new JsonParser();

        // GSon is somewhat strict, and will fail to parse anything that wouldn't pass the whitelist plugin.
        JsonElement json = parser.parse(whitelist);

        assert(json != null);
        assert(json.isJsonObject());
        assert(json.getAsJsonObject().get("atl.dc.migration.created").isJsonArray());
    }
}
