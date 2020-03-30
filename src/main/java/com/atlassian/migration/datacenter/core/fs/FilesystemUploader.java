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

import com.atlassian.migration.datacenter.core.exceptions.FileSystemMigrationFailure;
import com.atlassian.migration.datacenter.core.util.UploadQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FilesystemUploader {
    private static final Logger logger = LoggerFactory.getLogger(FilesystemUploader.class);

    private Uploader uploader;
    private Crawler crawler;
    private final ExecutorService pool;

    public FilesystemUploader(Crawler crawler, Uploader uploader) {
        this.uploader = uploader;
        this.crawler = crawler;
        this.pool = Executors.newFixedThreadPool(2);
    }

    public void uploadDirectory(Path dir) throws FileUploadException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        UploadQueue<Path> queue = new UploadQueue<>(uploader.maxConcurrent());

        Future<Boolean> crawlFuture = pool.submit(() -> {
            crawler.crawlDirectory(dir, queue);
            return true;
        });
        Future<Boolean> uploadFuture = pool.submit(() -> {
            uploader.upload(queue);
            return true;
        });

        try {
            crawlFuture.get();
            uploadFuture.get();
        } catch (InterruptedException e) {
            throw new FileUploadException("Failed to traverse/upload filesystem: " + dir, e);
        } catch (ExecutionException e) {
            throw new FileUploadException("Failed to traverse/upload filesystem: " + dir, e.getCause());
        }

        pool.shutdown();
    }

    /**
     * Abort the migration process and shuts down all executor services in the pool
     */
    public void abort() {
        final List<Runnable> runnables = pool.shutdownNow();
        logger.warn("Shut down executors, list of task not commenced: {}", runnables);
    }

    public static class FileUploadException extends FileSystemMigrationFailure {
        public FileUploadException(String message) {
            super(message);
        }

        public FileUploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
