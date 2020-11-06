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

package com.atlassian.migration.datacenter.core.fs.listener

import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentCreateEvent
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentUpdateEvent
import com.atlassian.confluence.event.events.content.page.PageCopyEvent
import com.atlassian.confluence.pages.Attachment
import com.atlassian.event.api.EventListener
import com.atlassian.event.api.EventPublisher
import org.springframework.beans.factory.DisposableBean

class ConfluenceAttachmentListener(private val eventPublisher: EventPublisher) : AttachmentListener, DisposableBean {
    override fun start() {
        eventPublisher.register(this)
    }

    override fun stop() {
        eventPublisher.unregister(this)
    }

    override fun destroy() {
        eventPublisher.unregister(this)
    }

    @EventListener
    fun onContentEvent(event: AttachmentEvent) {
        when (event) {
            is GeneralAttachmentCreateEvent,
            is GeneralAttachmentUpdateEvent,
            is PageCopyEvent -> saveAttachmentsFromContent(event.attachments)
            else -> Unit
        }
    }

    private fun saveAttachmentsFromContent(attachments: List<Attachment>) {
        attachments.map { attachment -> attachment }
    }
}