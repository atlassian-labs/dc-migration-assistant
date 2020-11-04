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

package com.atlassian.migration.datacenter.core.application;

import com.atlassian.migration.datacenter.spi.exceptions.UnsupportedPasswordEncodingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Base64;

class JiraDbConfigXmlElement {
    private static final String BASE64_CLASS = "com.atlassian.db.config.password.ciphers.base64.Base64Cipher";

    private String url;
    @JacksonXmlProperty(localName = "username")
    private String userName;
    @JacksonXmlProperty(localName = "atlassian-password-cipher-provider")
    private String cipher;
    private String password;

    public JiraDbConfigXmlElement() {
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() throws UnsupportedPasswordEncodingException
    {
        if (cipher != null) {
            if (!cipher.equals(BASE64_CLASS)) {
                throw new UnsupportedPasswordEncodingException("Unsupported database password encryption in dbconfig.xml; see documentation for detail: " + cipher);
            }
            return new String(Base64.getDecoder().decode(password));
        }
        return password;
    }
}
