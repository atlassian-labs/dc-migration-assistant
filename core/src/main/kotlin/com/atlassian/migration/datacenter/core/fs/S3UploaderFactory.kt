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

package com.atlassian.migration.datacenter.core.fs

import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Supplier

class S3UploaderFactory(private val deploymentService: AWSMigrationHelperDeploymentService,
                        private val clientSupplier: Supplier<S3AsyncClient>,
                        private val home: Path)
    : UploaderFactory
{
    private val OVERRIDE_UPLOAD_DIRECTORY = System
            .getProperty("com.atlassian.migration.datacenter.fs.overrideJiraHome", "")

    override fun newUploader(report: FileSystemMigrationReport): Uploader {
        val s3Bucket = deploymentService.getMigrationS3BucketName()

        val s3UploadConfig = S3UploadConfig(s3Bucket, clientSupplier.get(), getSharedHomeDir())
        val s3Uploader: Uploader = S3Uploader(s3UploadConfig, report)

        return s3Uploader
    }

    private fun getSharedHomeDir(): Path? {
        return if (OVERRIDE_UPLOAD_DIRECTORY != "") {
            Paths.get(OVERRIDE_UPLOAD_DIRECTORY)
        } else
            home
    }

}