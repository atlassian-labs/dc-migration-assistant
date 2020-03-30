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

package com.atlassian.migration.datacenter.core.util;

import com.atlassian.jira.config.util.JiraHome;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptionManagerTest {

    @Mock
    static JiraHome mockHome;

    EncryptionManager sut;

    @BeforeEach
    void setUp() {
        when(mockHome.getHome()).thenReturn(new File("."));

        sut = new EncryptionManager(mockHome);
    }

    @AfterAll
    static void tearDown() {
        File keyFile = new File(mockHome.getHome().getAbsolutePath().concat("/").concat("keyFile"));
        File saltFile = new File(mockHome.getHome().getAbsolutePath().concat("/").concat("saltFile"));
        if (keyFile.exists()) {
            keyFile.delete();
        }
        if (saltFile.exists()) {
            saltFile.delete();
        }
    }

    @Test
    void testEncryption() {
        final String testString = RandomStringUtils.randomAlphanumeric(new Random().nextInt(50));

        String encrypted = sut.encryptString(testString);
        String decrypted = sut.decryptString(encrypted);

        assertNotNull(encrypted);
        assertNotNull(decrypted);
        assertEquals(decrypted, testString);
    }

}