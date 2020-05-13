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
    void captureAttachment() {
        Attachment oneAttachment = Mockito.mock(Attachment.class);
        File oneAttachmentFile = Mockito.mock(File.class);
        Mockito.when(store.getAttachmentFile(oneAttachment)).thenReturn(oneAttachmentFile);

        sut.captureAttachment(oneAttachment);

        Mockito.verify(oneAttachmentFile).toPath();

    }
}