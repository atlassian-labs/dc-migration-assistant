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

package com.atlassian.migration.datacenter.core.fs.download.s3sync;

import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.aws.ssm.SuccessfulSSMCommandConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class S3SyncFileSystemDownloader {

    private static final Logger logger = LoggerFactory.getLogger(S3SyncFileSystemDownloader.class);

    // FIXME: Should be loaded from migration stack. Defaults to a document created by a migration stack in us-east-1
    private static final String SSM_PLAYBOOK = System.getProperty("com.atlassian.migration.s3sync.documentName", "bpartridge-12-03t15-42-39-migration-helper-SharedHomeDownloadDocument-1C56C88F671YL");
    // FIXME: Should be loaded from migration stack.
    private static final String STATUS_SSM_PLAYBOOK = System.getProperty("com.atlassian.migration.s3sync.statusDocmentName", "fake-document");
    // FIXME: Should be loaded from migration stack. Defaults to an instance deployed by a migration stack in us-east-1
    private static final String MIGRATION_STACK_INSTANCE = System.getProperty("com.atlassian.migration.instanceId", "i-0353cc9a8ad7dafc2");

    private int maxCommandStatusRetries;

    private final SSMApi ssmApi;

    public S3SyncFileSystemDownloader(SSMApi ssmApi) {
        this(ssmApi, 10);
    }

    S3SyncFileSystemDownloader(SSMApi ssmApi, int maxCommandStatusRetries) {
        this.ssmApi = ssmApi;
        this.maxCommandStatusRetries = maxCommandStatusRetries;
    }

    public void initiateFileSystemDownload() throws CannotLaunchCommandException {
        // FIXME: Reload the migration stack instance ID in case instance has gone down during migration
        String commandID = ssmApi.runSSMDocument(SSM_PLAYBOOK, MIGRATION_STACK_INSTANCE, Collections.emptyMap());

        SuccessfulSSMCommandConsumer consumer = new EnsureSuccessfulSSMCommandConsumer(ssmApi, commandID, MIGRATION_STACK_INSTANCE);

        try {
            consumer.handleCommandOutput(maxCommandStatusRetries);
        } catch (SuccessfulSSMCommandConsumer.UnsuccessfulSSMCommandInvocationException e) {
            logger.error("error launching s3 sync command", e);
            throw new CannotLaunchCommandException("unable to launch file system download command successfully.");
        } catch (SuccessfulSSMCommandConsumer.SSMCommandInvocationProcessingError never) {
        }
    }

    public S3SyncCommandStatus getFileSystemDownloadStatus() throws IndeterminateS3SyncStatusException {
        String statusCommandId = ssmApi.runSSMDocument(STATUS_SSM_PLAYBOOK, MIGRATION_STACK_INSTANCE, Collections.emptyMap());

        SuccessfulSSMCommandConsumer<S3SyncCommandStatus> consumer = new UnmarshalS3SyncStatusSSMCommandConsumer(ssmApi, statusCommandId, MIGRATION_STACK_INSTANCE);

        try {
            return consumer.handleCommandOutput(maxCommandStatusRetries);
        } catch (SuccessfulSSMCommandConsumer.UnsuccessfulSSMCommandInvocationException e) {
            logger.error("Status command did not complete successfully", e);
            return null;
        } catch (SuccessfulSSMCommandConsumer.SSMCommandInvocationProcessingError e) {
            logger.error("Unable to read status of s3 sync command", e);
            return null;
        }
    }

    public static class IndeterminateS3SyncStatusException extends Exception {
        IndeterminateS3SyncStatusException(String message) {
            super(message);
        }
    }

    public static class CannotLaunchCommandException extends Exception {
        CannotLaunchCommandException(String message) {
            super(message);
        }
    }
}
