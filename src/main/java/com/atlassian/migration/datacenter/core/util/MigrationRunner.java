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

import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MigrationRunner
{
    private static final Logger logger = LoggerFactory.getLogger(MigrationRunner.class);

    private final SchedulerService schedulerService;

    public MigrationRunner(SchedulerService schedulerService)
    {
        this.schedulerService = schedulerService;
    }

    public boolean runMigration(JobId jobId, MigrationJobRunner runner) {
        final JobRunnerKey runnerKey = JobRunnerKey.of(runner.getKey());
        logger.info("Starting filesystem migration");

        if (schedulerService.getJobDetails(jobId) != null) {
            logger.warn("Tried to schedule file system migration while job already exists: "+jobId);
            return false;
        }

        schedulerService.registerJobRunner(runnerKey, runner);
        logger.info("Registered new job runner for "+runnerKey);

        JobConfig jobConfig = JobConfig.forJobRunnerKey(runnerKey)
            .withSchedule(null) // run now
            .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
        try {
            logger.info("Scheduling new job for runner "+runner.getKey());
            schedulerService.scheduleJob(jobId, jobConfig);
            return true;

        } catch (SchedulerServiceException e) {
            logger.error("Exception when scheduling job for "+runner.getKey(), e);
            this.schedulerService.unscheduleJob(jobId);
            return false;
        }
    }

    public boolean abortJobIfPresesnt(JobId jobId) {
        if (schedulerService.getJobDetails(jobId) == null) {
            return false;
        }
        schedulerService.unscheduleJob(jobId);
        logger.info("Removed scheduled filesystem migration job "+jobId);
        return true;
    }

}
