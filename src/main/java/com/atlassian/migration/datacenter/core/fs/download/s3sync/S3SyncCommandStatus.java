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

package com.atlassian.migration.datacenter.core.fs.download.s3sync;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Data class for reading output from the s3 sync status SSM command.
 * In the event that values can't be read for any reason i.e. if they are absent
 * number values will be set to -1, the status will be assumed to be unfinished and
 * the download will be assumed to have stopped calculating.
 *
 * NOTE: When {@link S3SyncCommandStatus#isComplete()} is true, there will be no values for
 * progress, total or filesRemaining.
 */
@JsonAutoDetect
public class S3SyncCommandStatus {
    private boolean finished;

    private int code;
    private double progress;
    private double total;
    private int filesRemaining;
    private boolean calculating;
    private List<String> errors;
    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private void unpackStatus(Map<String, Object> status) {
        if (status == null) {
            return;
        }
        progress = (double)status.getOrDefault("progress", -1d);
        total = (double)status.getOrDefault("total", -1d);
        filesRemaining = (int)status.getOrDefault("files_remaining", -1);
        calculating = (boolean)status.getOrDefault("isCalculating", false);
    }

    public boolean isComplete() {
        return finished;
    }

    public int getExitCode() {
        return code;
    }

    public boolean isCalculating() {
        return calculating;
    }

    public double getBytesDownloaded() {
        return progress;
    }

    public double getTotalBytesToDownload() {
        return total;
    }

    public int getFilesRemainingToDownload() {
        return filesRemaining;
    }

    public boolean hasErrors() {
        return errors != null && errors.size() > 0;
    }

    public List<String> getErrors() {
        return ImmutableList.copyOf(errors);
    }

    @Override
    public String toString() {
        return "S3SyncCommandStatus{" +
                "finished=" + finished +
                ", code=" + code +
                ", progress=" + progress +
                ", total=" + total +
                ", filesRemaining=" + filesRemaining +
                ", calculating=" + calculating +
                ", errors=" + Arrays.toString(errors.toArray()) +
                '}';
    }
}
