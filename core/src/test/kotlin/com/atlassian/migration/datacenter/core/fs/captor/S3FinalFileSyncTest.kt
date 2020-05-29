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
import com.atlassian.migration.datacenter.dto.FileSyncRecord
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Path
import java.util.Optional

@ExtendWith(MockKExtension::class)
internal class S3FinalFileSyncTest {

    @MockK
    lateinit var mockSyncManager: AttachmentSyncManager

    @MockK
    lateinit var mockUploader: Uploader

    @MockK
    lateinit var mockFile: FileSyncRecord

    @MockK
    lateinit var anotherMockFile: FileSyncRecord

    lateinit var sut: S3FinalFileSync

    private val uploadedPaths: MutableList<String> = ArrayList()

    @BeforeEach
    internal fun setUp() {
        sut = S3FinalFileSync(mockSyncManager, mockUploader)

        val slot = slot<UploadQueue<Path>>()
        every { mockUploader.upload(capture(slot)) } answers {
            val uploadQueue = slot.captured
            var path = uploadQueue.take()
            while (path != Optional.empty<Path>()) {
                uploadedPaths.add(path.get().toString())
                path = uploadQueue.take()
            }
        }
    }

    @Test
    fun shouldUploadAllFilesReturnedByCaptor() {
        val filePath = "hello/there"
        every { mockFile.filePath } returns filePath
        val anotherFilePath = "general/kenobi"
        every { anotherMockFile.filePath } returns anotherFilePath

        every { mockSyncManager.capturedAttachments } returns setOf(mockFile, anotherMockFile)

        sut.uploadCapturedFiles()

        assertThat(uploadedPaths, containsInAnyOrder(filePath, anotherFilePath))
    }

}
