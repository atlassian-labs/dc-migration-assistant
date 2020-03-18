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
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.util.concurrent.Supplier;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Class for managing the storage and retrieval of AWS Credentials. Should not be used for direct access to credentials
 * except for in a CredentialsProvider implementation. This class stores credentials encrypted with a key generated by
 * the Spring Security Crypto library using its default AES encryption.
 */
public class EncryptedCredentialsStorage implements ReadCredentialsService, WriteCredentialsService {

    private static final String AWS_CREDS_PLUGIN_STORAGE_KEY = "com.atlassian.migration.datacenter.core.aws.auth";
    private static final String ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX = ".accessKeyId";
    private static final String SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX = ".secretAccessKey";
    private static final String ENCRYPTION_KEY_FILE_NAME = "keyFile";
    private static final String ENCRYPTION_SALT_FILE_NAME = "saltFile";
    private static final Logger logger = Logger.getLogger(EncryptedCredentialsStorage.class);


    private final Supplier<PluginSettingsFactory> pluginSettingsFactorySupplier;
    private final JiraHome jiraHome;
    private TextEncryptor textEncryptor;
    private PluginSettings pluginSettings;

    public EncryptedCredentialsStorage(Supplier<PluginSettingsFactory> pluginSettingsFactorySupplier,
                                       JiraHome jiraHome) {
        this.pluginSettingsFactorySupplier = pluginSettingsFactorySupplier;
        this.jiraHome = jiraHome;
        assert this.jiraHome != null;
        String keyFilePath = this.jiraHome.getHome().getPath().concat("/").concat(ENCRYPTION_KEY_FILE_NAME);
        String saltFilePath = this.jiraHome.getHome().getPath().concat("/").concat(ENCRYPTION_SALT_FILE_NAME);
        String password = getEncryptionData(keyFilePath);
        String salt = getEncryptionData(saltFilePath);
        this.textEncryptor = Encryptors.text(password, salt);
    }

    @PostConstruct
    // FIXME: I do not work
    public void postConstruct() {
        this.pluginSettings = this.pluginSettingsFactorySupplier.get().createGlobalSettings();
        assert this.jiraHome != null;
        String keyFilePath = this.jiraHome.getHome().getPath().concat("/").concat(ENCRYPTION_KEY_FILE_NAME);
        String saltFilePath = this.jiraHome.getHome().getPath().concat("/").concat(ENCRYPTION_SALT_FILE_NAME);
        String password = getEncryptionData(keyFilePath);
        String salt = getEncryptionData(saltFilePath);
        this.textEncryptor = Encryptors.text(password, salt);
    }

    @Override
    public String getAccessKeyId() {
        // FIXME: Need to find a way to inject without calling the supplier every time
        PluginSettings pluginSettings = this.pluginSettingsFactorySupplier.get().createGlobalSettings();
        String raw = (String) pluginSettings.get(AWS_CREDS_PLUGIN_STORAGE_KEY + ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX);
        return this.decryptString(raw);
    }

    public void setAccessKeyId(String accessKeyId) {
        // FIXME: Need to find a way to inject without calling the supplier every time
        PluginSettings pluginSettings = this.pluginSettingsFactorySupplier.get().createGlobalSettings();
        pluginSettings.put(AWS_CREDS_PLUGIN_STORAGE_KEY + ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX, this.encryptString(accessKeyId));
    }

    @Override
    public void storeAccessKeyId(String accessKeyId) {
        this.setAccessKeyId(accessKeyId);
    }

    @Override
    public void storeSecretAccessKey(String secretAccessKey) {
        this.setSecretAccessKey(secretAccessKey);
    }

    @Override
    public String getSecretAccessKey() {
        // FIXME: Need to find a way to inject without calling the supplier every time
        PluginSettings pluginSettings = this.pluginSettingsFactorySupplier.get().createGlobalSettings();
        String raw = (String) pluginSettings.get(AWS_CREDS_PLUGIN_STORAGE_KEY + SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX);
        return this.decryptString(raw);
    }

    public void setSecretAccessKey(String secretAccessKey) {
        // FIXME: Need to find a way to inject without calling the supplier every time
        PluginSettings pluginSettings = this.pluginSettingsFactorySupplier.get().createGlobalSettings();
        pluginSettings.put(AWS_CREDS_PLUGIN_STORAGE_KEY + SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX, this.encryptString(secretAccessKey));
    }

    /**
     * The string encryption function
     *
     * @param raw the string to be encrypted
     * @return the encrypted string
     */
    private String encryptString(final String raw) {
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
    private String decryptString(final String encrypted) {
        try {
            return this.textEncryptor.decrypt(encrypted);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    private static String getEncryptionData(String fileName) {
        File dataFile = new File(fileName);
        if (dataFile.exists()) {
            return readFileData(dataFile);
        } else {
            return generateAndWriteKey(dataFile);
        }
    }

    private static String readFileData(File sourceFile) {
        StringBuilder dataBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(sourceFile.getPath()), StandardCharsets.UTF_8)) {
            stream.forEach(dataBuilder::append);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return dataBuilder.toString();
    }

    private static String generateAndWriteKey(File file) {
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
