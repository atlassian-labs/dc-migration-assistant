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

package com.atlassian.migration.datacenter.core.fs.jira.captor;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import com.atlassian.migration.datacenter.dto.FileSyncRecord;
import com.atlassian.migration.datacenter.spi.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class DefaultAttachmentCaptor implements AttachmentCaptor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAttachmentCaptor.class);
    private final ActiveObjects ao;
    private final MigrationService migrationService;
    private AttachmentStore attachmentStore;

    public DefaultAttachmentCaptor(ActiveObjects ao, MigrationService migrationService, AttachmentStore
            attachmentStore) {
        this.ao = ao;
        this.migrationService = migrationService;
        this.attachmentStore = attachmentStore;
    }

    @Override
    public void captureAttachment(Attachment attachment) {
        File attachmentFile = this.attachmentStore.getAttachmentFile(attachment);

        captureAttachmentFile(attachmentFile);

        //Thumbnails may not be present. However, attachment.isThumbnailable isn't very predictable, so we check if the thumbnail file exists for all attachments
        File thumbnailFile = this.attachmentStore.getThumbnailFile(attachment);
        captureAttachmentFile(thumbnailFile);
    }

    private void captureAttachmentFile(File attachmentFile) {
        if (attachmentFile != null && attachmentFile.exists()) {
            Path attachmentPath = attachmentFile.toPath();
            logger.debug("Recording file path - {} ", attachmentPath);
            this.captureAttachmentPath(attachmentPath);
        }
    }

    private void captureAttachmentPath(Path attachmentPath) {
        logger.debug("captured attachment for final sync: {}", attachmentPath.toString());

        FileSyncRecord record = ao.create(FileSyncRecord.class);

        record.setFilePath(attachmentPath.toString());
        record.setMigration(migrationService.getCurrentMigration());

        record.save();
    }
}
