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
import com.atlassian.jira.issue.attachment.AttachmentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraIssueAttachmentListenerTest {
    private static final String A_MOCK_ATTACHMENT_PATH = "a/mock/attachment";
    private static final String ANOTHER_MOCK_ATTACHMENT_PATH = "another/mock/attachment";

    private static final String A_MOCK_ATTACHMENT_THUMBNAIL_PATH = "a/mock/thumb/attachment";
    private static final String ANOTHER_MOCK_ATTACHMENT_THUMBNAIL_PATH = "another/mock/thumb/attachment";

    @Mock
    EventPublisher mockPublisher;

    @Mock
    AttachmentStore attachmentStore;

    JiraIssueAttachmentListener sut;

    @Mock
    Issue mockIssue;

    @Mock
    Attachment aMockAttachment;

    @Mock
    Attachment anotherMockAttachment;

    private List<Path> capturedPaths = new LinkedList<>();

    @BeforeEach
    void setUp() {
        sut = new JiraIssueAttachmentListener(mockPublisher, this::captureAttachment, attachmentStore);
    }

    @Test
    void shouldCaptureAttachmentInIssueCreatedEvent() {
        setupAttachmentMocks(false);
        IssueEvent mockEvent = new IssueEvent(mockIssue, null, null, null, null, null, EventType.ISSUE_CREATED_ID);
        sut.onIssueEvent(mockEvent);

        assertThat(capturedPaths, contains(Paths.get(A_MOCK_ATTACHMENT_PATH), Paths.get((ANOTHER_MOCK_ATTACHMENT_PATH))));
    }

    @Test
    void shouldCaptureAttachmentInIssueUpdatedEvent() {
        setupAttachmentMocks(false);
        IssueEvent mockEvent = new IssueEvent(mockIssue, null, null, null, null, null, EventType.ISSUE_UPDATED_ID);
        sut.onIssueEvent(mockEvent);

        assertThat(capturedPaths, contains(Paths.get(A_MOCK_ATTACHMENT_PATH), Paths.get((ANOTHER_MOCK_ATTACHMENT_PATH))));
    }

    @Test
    void shouldNotCaptureAttachmentInIssueCommentEditedEvent() {
        IssueEvent mockEvent = new IssueEvent(mockIssue, null, null, null, null, null, EventType.ISSUE_COMMENT_EDITED_ID);
        sut.onIssueEvent(mockEvent);

        assertThat(capturedPaths.size(), is(0));
        verify(mockIssue, never()).getAttachments();
    }

    @Test
    void shouldCaptureAttachmentAndThumbnailsInIssueCreatedEvent() {
        setupAttachmentMocks(true);
        IssueEvent mockEvent = new IssueEvent(mockIssue, null, null, null, null, null, EventType.ISSUE_CREATED_ID);

        sut.onIssueEvent(mockEvent);

        assertThat(capturedPaths, contains(
                Paths.get(A_MOCK_ATTACHMENT_PATH),
                Paths.get(ANOTHER_MOCK_ATTACHMENT_PATH),
                Paths.get(A_MOCK_ATTACHMENT_THUMBNAIL_PATH),
                Paths.get(ANOTHER_MOCK_ATTACHMENT_THUMBNAIL_PATH)
        ));
    }

    @Test
    void shouldNotRegisterWithListenerIfNotStarted() {
        verify(mockPublisher, never()).register(sut);
    }

    @Test
    void shouldRegisterAfterStarting() {
        sut.start();

        assertTrue(sut.isStarted(), "expected listener to be started after starting");
    }

    @Test
    void shouldRegisterOnce() {
        sut.start();
        sut.start();

        verify(mockPublisher, times(1)).register(sut);
    }

    private void captureAttachment(Path path) {
        capturedPaths.add(path);
    }

    private void setupAttachmentMocks(boolean includeThumbnails) {
        when(mockIssue.getAttachments()).thenReturn(new ArrayList<Attachment>() {{
            add(aMockAttachment);
            add(anotherMockAttachment);
        }});

        when(this.attachmentStore.getAttachmentFile(aMockAttachment)).thenReturn(new File(A_MOCK_ATTACHMENT_PATH));
        when(this.attachmentStore.getAttachmentFile(anotherMockAttachment)).thenReturn(new File(ANOTHER_MOCK_ATTACHMENT_PATH));

//        when(aMockAttachment.isThumbnailable()).thenReturn(includeThumbnails);
//        when(anotherMockAttachment.isThumbnailable()).thenReturn(includeThumbnails);

        if (includeThumbnails) {
            when(this.attachmentStore.getThumbnailFile(aMockAttachment)).thenReturn(new File(A_MOCK_ATTACHMENT_THUMBNAIL_PATH));
            when(this.attachmentStore.getThumbnailFile(anotherMockAttachment)).thenReturn(new File(ANOTHER_MOCK_ATTACHMENT_THUMBNAIL_PATH));
        }
    }
}