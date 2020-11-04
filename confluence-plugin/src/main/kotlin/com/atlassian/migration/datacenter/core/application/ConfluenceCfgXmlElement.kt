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

package com.atlassian.migration.datacenter.core.application

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

@JacksonXmlRootElement(localName = "confluence-configuration")
class ConfluenceCfgXmlElement {

    @JacksonXmlProperty(localName = "properties")
    lateinit var properties: List<ConfluenceCfgPropertyXmlElement>

    class ConfluenceCfgPropertyXmlElement {
        @JacksonXmlProperty(isAttribute = true, localName = "name")
        lateinit var name: String

        @JacksonXmlText
        lateinit var value: String
    }

}