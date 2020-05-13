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
import com.atlassian.migration.datacenter.core.fs.captor.AttachmentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;
import java.util.List;

public class JiraIssueAttachmentListener implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JiraIssueAttachmentListener.class);
    private static final List<Long> ISSUE_EVENT_TYPES_TO_LISTEN = Arrays.asList(EventType.ISSUE_CREATED_ID, EventType.ISSUE_UPDATED_ID);

    private final AttachmentCaptor attachmentCaptor;
    private final EventPublisher eventPublisher;

    public JiraIssueAttachmentListener(EventPublisher eventPublisher, AttachmentCaptor attachmentCaptor) {
        this.eventPublisher = eventPublisher;
        this.attachmentCaptor = attachmentCaptor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("registering with event publisher");
        eventPublisher.register(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        logger.trace("received jira event with type {}", issueEvent.getEventTypeId());
        if (ISSUE_EVENT_TYPES_TO_LISTEN.contains(issueEvent.getEventTypeId())) {
            logger.trace("got issue created event");
            issueEvent
                .getIssue()
                .getAttachments()
                .forEach(this.attachmentCaptor::captureAttachment);
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Destroying migration assistant plugin. De-registering with event publisher");
        eventPublisher.unregister(this);
    }
}
