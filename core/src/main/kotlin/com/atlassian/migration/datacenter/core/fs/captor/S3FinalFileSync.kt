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

package com.atlassian.migration.datacenter.core.fs.captor

import com.atlassian.migration.datacenter.core.fs.Uploader
import com.atlassian.migration.datacenter.core.util.UploadQueue
import java.nio.file.Path
import java.nio.file.Paths

class S3FinalFileSync(private val attachmentSyncManager: AttachmentSyncManager, private val uploader: Uploader) {

    fun uploadCapturedFiles() {
        val capturedAttachments = attachmentSyncManager.capturedAttachments
        val uploadQueue = UploadQueue<Path>(capturedAttachments.size + 1)

        capturedAttachments.forEach { uploadQueue.put(Paths.get(it.filePath)) }
        uploadQueue.finish()

        uploader.upload(uploadQueue)
    }

}