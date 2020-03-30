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

package com.atlassian.migration.datacenter.core.util;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UploadQueue<T> {
    private BlockingQueue<Optional<T>> queue;

    public UploadQueue(Integer max) {
        this.queue = new LinkedBlockingQueue<>(max);
    }

    /**
     * Put a value on the queue to be consumed; blocks if the consumer is saturated.
     *
     * @param v
     * @throws InterruptedException
     */
    public void put(T v) throws InterruptedException {
        queue.put(Optional.of(v));
    }

    /**
     * Similar to BlockingQueue.take(), except returns Optional.empty() if the producer is finished.
     *
     * @return Optional of value, or Optional.empty() if the producer is finished.
     * @throws InterruptedException
     */
    public Optional<T> take() throws InterruptedException {
        return queue.take();
    }

    /**
     * Signal to the consumer that the producer is finished.
     *
     * @throws InterruptedException
     */
    public void finish() throws InterruptedException {
        queue.put(Optional.empty());
    }

    public boolean contains(T o) {
        return queue.contains(Optional.of(o));
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}
