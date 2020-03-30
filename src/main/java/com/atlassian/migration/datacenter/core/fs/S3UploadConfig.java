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

package com.atlassian.migration.datacenter.core.fs;

import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;

/**
 * Contains configuration for S3 upload calls
 */
public class S3UploadConfig {
    private String bucketName;
    private S3AsyncClient s3AsyncClient;
    private Path sharedHome;

    public S3UploadConfig(String bucketName, S3AsyncClient s3AsyncClient, Path sharedHome) {
        this.bucketName = bucketName;
        this.s3AsyncClient = s3AsyncClient;
        this.sharedHome = sharedHome;
    }

    /**
     * Destination S3 bucket name where to upload files
     *
     * @return bucket name
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Asynchronous S3 client to call AWS API
     *
     * @return async S3 client
     */
    public S3AsyncClient getS3AsyncClient() {
        return s3AsyncClient;
    }

    /**
     * Application shared home. In case of Server distribution, it should return the regular home.
     *
     * @return application home (shared for DC, normal for Server distribution)
     */
    public Path getSharedHome() {
        return sharedHome;
    }
}
