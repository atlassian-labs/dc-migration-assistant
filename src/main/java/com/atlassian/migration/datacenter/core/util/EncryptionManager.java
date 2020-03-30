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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class EncryptionManager {

    private static final String ENCRYPTION_KEY_FILE_NAME = "keyFile";
    private static final String ENCRYPTION_SALT_FILE_NAME = "saltFile";

    private static final Logger logger = LoggerFactory.getLogger(EncryptionManager.class);

    private TextEncryptor textEncryptor;
    private final JiraHome jiraHome;

    public EncryptionManager(JiraHome jiraHome) {
        this.jiraHome = jiraHome;
        assert this.jiraHome != null;
        String keyFilePath = this.jiraHome.getHome().getPath().concat("/").concat(ENCRYPTION_KEY_FILE_NAME);
        String saltFilePath = this.jiraHome.getHome().getPath().concat("/").concat(ENCRYPTION_SALT_FILE_NAME);
        String password = getEncryptionData(keyFilePath);
        String salt = getEncryptionData(saltFilePath);
        this.textEncryptor = Encryptors.text(password, salt);
    }

    /**
     * The string encryption function
     *
     * @param raw the string to be encrypted
     * @return the encrypted string
     */
    public String encryptString(final String raw) {
        try {
            return this.textEncryptor.encrypt(raw);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * The string decryption function
     *
     * @param encrypted string to be decrypted
     * @return the decrypted plaintext string
     */
    public String decryptString(final String encrypted) {
        try {
            return this.textEncryptor.decrypt(encrypted);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    private String getEncryptionData(String fileName) {
        File dataFile = new File(fileName);
        if (dataFile.exists()) {
            return readFileData(dataFile);
        } else {
            return generateAndWriteKey(dataFile);
        }
    }

    private String readFileData(File sourceFile) {
        StringBuilder dataBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(sourceFile.getPath()), StandardCharsets.UTF_8)) {
            stream.forEach(dataBuilder::append);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return dataBuilder.toString();
    }

    private String generateAndWriteKey(File file) {
        String keyString = KeyGenerators.string().generateKey();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(keyString.getBytes());
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        file.setWritable(false, true);
        return keyString;
    }
}
