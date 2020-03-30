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

import com.atlassian.migration.datacenter.core.fs.S3UploadJobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MigrationRunnerTest
{
    @Mock SchedulerService schedulerService;
    @Mock MigrationJobRunner runner;
    @InjectMocks MigrationRunner migrationRunner;

    @BeforeEach
    public void setup() {
        when(runner.getKey()).thenReturn("test");
    }

    @Test
    void shouldUnsetScheduledJobWhenSchedulerExceptionIsRaised() throws Exception {

        Mockito.doThrow(SchedulerServiceException.class)
            .when(schedulerService)
            .scheduleJob(any(), any());
        JobId id = JobId.of(runner.getKey()+"42");

        Boolean isScheduled = migrationRunner.runMigration(id, runner);
        assertFalse(isScheduled);

        verify(schedulerService).unscheduleJob(argThat(jobId -> jobId.compareTo(jobId) == 0));
    }


}