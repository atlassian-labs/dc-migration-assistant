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

package com.atlassian.migration.datacenter.core.fs.listener;


import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import com.atlassian.migration.datacenter.core.fs.captor.AttachmentPathCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class JiraIssueAttachmentListener implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JiraIssueAttachmentListener.class);
    private static final List<Long> ISSUE_EVENT_TYPES_TO_LISTEN = Arrays.asList(EventType.ISSUE_CREATED_ID, EventType.ISSUE_UPDATED_ID);

    private final EventPublisher eventPublisher;
    private boolean started = false;
    private final AttachmentPathCaptor attachmentPathCaptor;
    private final AttachmentStore attachmentStore;

    public JiraIssueAttachmentListener(EventPublisher eventPublisher, AttachmentPathCaptor attachmentPathCaptor, AttachmentStore attachmentStore) {
        this.eventPublisher = eventPublisher;
        this.attachmentPathCaptor = attachmentPathCaptor;
        this.attachmentStore = attachmentStore;
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        logger.trace("received jira event with type {}", issueEvent.getEventTypeId());
        if (ISSUE_EVENT_TYPES_TO_LISTEN.contains(issueEvent.getEventTypeId())) {

            Collection<Attachment> attachments = issueEvent
                    .getIssue()
                    .getAttachments();

            attachments
                    .stream()
                    .map(this.attachmentStore::getAttachmentFile)
                    .map(File::toPath)
                    .forEach(this.attachmentPathCaptor::captureAttachmentPath);

            attachments
                    .stream()
                    // FIXME: I lie about thumbnailable
                    //.filter(Attachment::isThumbnailable)
                    .map(attachmentStore::getThumbnailFile)
                    .filter(Objects::nonNull)
                    .map(File::toPath)
                    .forEach(this.attachmentPathCaptor::captureAttachmentPath);
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Destroying migration assistant plugin. De-registering with event publisher");
        eventPublisher.unregister(this);
        started = false;
    }

    public void start() {
        if (!started) {
            started = true;
            eventPublisher.register(this);
        }
    }

    public boolean isStarted() {
        return started;
    }
}
