package com.atlassian.migration.datacenter.core.fs.captor;

import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultAttachmentCaptorTest {

    private AttachmentCaptor sut;

    @Mock
    private AttachmentStore store;

    @BeforeEach
    void setUp() {
        sut = new DefaultAttachmentCaptor(store);
    }


    @Test
    void shouldCaptureAttachment() {
        Attachment oneAttachment = Mockito.mock(Attachment.class);
        File oneAttachmentFile = Mockito.mock(File.class);
        when(store.getAttachmentFile(oneAttachment)).thenReturn(oneAttachmentFile);

        sut.captureAttachment(oneAttachment);

        Mockito.verify(oneAttachmentFile).toPath();
    }

    @Test
    void shouldCaptureAttachmentThumbnail() {
        Attachment oneAttachment = Mockito.mock(Attachment.class);
        File oneAttachmentFile = Mockito.mock(File.class);
        File oneAttachmentThumbnailFile = Mockito.mock(File.class);

        when(store.getAttachmentFile(oneAttachment)).thenReturn(oneAttachmentFile);
        when(oneAttachment.isThumbnailable()).thenReturn(true);
        when(store.getThumbnailFile(oneAttachment)).thenReturn(oneAttachmentThumbnailFile);

        sut.captureAttachment(oneAttachment);

        Mockito.verify(oneAttachmentFile).toPath();
        Mockito.verify(oneAttachmentThumbnailFile).toPath();
    }
}