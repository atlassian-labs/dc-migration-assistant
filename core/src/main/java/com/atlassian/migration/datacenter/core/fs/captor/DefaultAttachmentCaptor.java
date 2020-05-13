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

package com.atlassian.migration.datacenter.core.fs.captor;

import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class DefaultAttachmentCaptor implements AttachmentCaptor {

    private final AttachmentStore attachmentStore;

    public DefaultAttachmentCaptor(AttachmentStore attachmentStore) {
        this.attachmentStore = attachmentStore;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultAttachmentCaptor.class);

    @Override
    public void captureAttachment(Attachment attachment) {
        Path path = attachmentStore.getAttachmentFile(attachment).toPath();
        logger.debug("captured attachment for final sync: {} at path {}", attachment, path);
    }
}
