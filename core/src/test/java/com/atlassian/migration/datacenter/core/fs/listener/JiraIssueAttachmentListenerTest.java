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

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraIssueAttachmentListenerTest {
    @Mock
    EventPublisher mockPublisher;

    @Mock
    Issue mockIssue;

    @Mock
    Attachment aMockAttachment;

    @Mock
    Attachment anotherMockAttachment;

    private List<Attachment> capturedPaths = new LinkedList<>();

    private JiraIssueAttachmentListener sut;

    @BeforeEach
    void setUp() {
        sut = new JiraIssueAttachmentListener(mockPublisher, this::captureAttachment);
    }

    @Test
    void shouldCaptureAttachmentInIssueCreatedEvent() {
        when(mockIssue.getAttachments()).thenReturn(new ArrayList<Attachment>() {{
            add(aMockAttachment);
            add(anotherMockAttachment);
        }});
        IssueEvent mockEvent = new IssueEvent(mockIssue, null, null, null, null, null, EventType.ISSUE_CREATED_ID);
        sut.onIssueEvent(mockEvent);

        assertThat(capturedPaths, contains(aMockAttachment,anotherMockAttachment));
    }


    @Test
    void shouldCaptureAttachmentInIssueUpdatedEvent() {
        when(mockIssue.getAttachments()).thenReturn(new ArrayList<Attachment>() {{
            add(aMockAttachment);
            add(anotherMockAttachment);
        }});
        IssueEvent mockEvent = new IssueEvent(mockIssue, null, null, null, null, null, EventType.ISSUE_UPDATED_ID);
        sut.onIssueEvent(mockEvent);

        assertThat(capturedPaths, contains(aMockAttachment,anotherMockAttachment));
    }

    @Test
    void shouldNotCaptureAttachmentInIssueCommentEditedEvent() {
        IssueEvent mockEvent = new IssueEvent(mockIssue, null, null, null, null, null, EventType.ISSUE_COMMENT_EDITED_ID);
        sut.onIssueEvent(mockEvent);

        assertThat(capturedPaths.size(), is(0));
        verify(mockIssue, never()).getAttachments();
    }

    private void captureAttachment(Attachment attachment) {
        capturedPaths.add(attachment);
    }
}