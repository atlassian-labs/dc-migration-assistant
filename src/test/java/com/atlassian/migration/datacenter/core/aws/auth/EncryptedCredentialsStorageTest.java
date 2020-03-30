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

package com.atlassian.migration.datacenter.core.aws.auth;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.util.EncryptionManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EncryptedCredentialsStorageTest {

    EncryptedCredentialsStorage encryptedCredentialsStorage;

    @Mock
    static JiraHome jiraHome;
    @Mock
    PluginSettingsFactory pluginSettingsFactory;
    private PluginSettings pluginSettings;

    @AfterAll
    static void tearDown() {
        File keyFile = new File(jiraHome.getHome().getAbsolutePath().concat("/").concat("keyFile"));
        File saltFile = new File(jiraHome.getHome().getAbsolutePath().concat("/").concat("saltFile"));
        if (keyFile.exists()) {
            keyFile.delete();
        }
        if (saltFile.exists()) {
            saltFile.delete();
        }
    }

    @BeforeEach
    void setup() {
        pluginSettings = new PluginSettings() {
            Map<String, Object> settings = new HashMap<>();

            @Override
            public Object get(String s) {
                return this.settings.get(s);
            }

            @Override
            public Object put(String s, Object o) {
                return this.settings.put(s, o);
            }

            @Override
            public Object remove(String s) {
                return this.settings.remove(s);
            }
        };

        when(jiraHome.getHome()).thenReturn(new File("."));
        when(this.pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);

        this.encryptedCredentialsStorage = new EncryptedCredentialsStorage(() -> this.pluginSettingsFactory, new EncryptionManager(jiraHome));
    }

    @Test
    void testSaveAccessKeyId() {
        final String testAccessKeyId = RandomStringUtils.randomAlphanumeric(new Random().nextInt(50));

        this.encryptedCredentialsStorage.setAccessKeyId(testAccessKeyId);

        String retrievedValue = this.encryptedCredentialsStorage.getAccessKeyId();
        assertEquals(testAccessKeyId, retrievedValue);
    }

    @Test
    void testSaveSecretKey() {
        final String testSecretAccessKey = RandomStringUtils.randomAlphanumeric(new Random().nextInt(50));

        this.encryptedCredentialsStorage.setSecretAccessKey(testSecretAccessKey);

        String retrievedValue = this.encryptedCredentialsStorage.getSecretAccessKey();
        assertEquals(testSecretAccessKey, retrievedValue);
    }
}
