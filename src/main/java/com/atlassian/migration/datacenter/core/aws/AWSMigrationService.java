package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.migration.datacenter.core.exceptions.InfrastructureProvisioningError;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.core.fs.S3UploadJobRunner;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Manages a migration from on-premise to self-hosted AWS.
 */
@Component
public class AWSMigrationService implements MigrationService {
    private static final Logger log = LoggerFactory.getLogger(AWSMigrationService.class);
    private final FilesystemMigrationService fsService;
    private final SchedulerService schedulerService;
    private final CfnApi cfnApi;
    private ActiveObjects ao;
    private Migration migration;

    /**
     * Creates a new, unstarted AWS Migration
     */
    public AWSMigrationService(@ComponentImport ActiveObjects ao,
                               FilesystemMigrationService fileService,
                               CfnApi cfnApi,
                               @ComponentImport SchedulerService schedulerService) {
        this.ao = requireNonNull(ao);
        this.fsService = fileService;
        this.cfnApi = cfnApi;
        this.schedulerService = schedulerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean startMigration() {
        if (getMigrationStage() != MigrationStage.NOT_STARTED) {
            return false;
        }

        updateMigrationStage(MigrationStage.STARTED);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MigrationStage getMigrationStage() {
        return getMigration().getStage();
    }

    private Migration getMigration() {
        if (migration == null) {
            Migration[] migrations = ao.find(Migration.class);
            if (migrations.length == 1) {
                // In case we have interrupted migration (e.g. the node went down), we want to pick up where we've
                // left off.
                migration = migrations[0];
            } else {
                // We didn't start the migration, so we need to create record in the db
                migration = ao.create(Migration.class);
                migration.setStage(MigrationStage.NOT_STARTED);
                migration.save();
            }
        }
        return migration;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String provisionInfrastructure(ProvisioningConfig config) throws InvalidMigrationStageError, InfrastructureProvisioningError {
        //TODO: Refactor this to a state machine as part of https://aws-partner.atlassian.net/browse/CHET-101. This will be extracted to a different class then
        MigrationStage currentMigrationStage = getMigrationStage();
        if (currentMigrationStage != MigrationStage.STARTED) {
            throw new InvalidMigrationStageError(String.format("Expected migration stage was %s, but found %s", MigrationStage.READY_TO_PROVISION, currentMigrationStage));
        }

        Optional<String> stackIdentifier = this.cfnApi.provisionStack(config.getTemplateUrl(), config.getStackName(), config.getParams());
        if (stackIdentifier.isPresent()) {
            updateMigrationStage(MigrationStage.PROVISIONING_IN_PROGRESS);
            return stackIdentifier.get();
        } else {
            updateMigrationStage(MigrationStage.PROVISIONING_ERROR);
            throw new InfrastructureProvisioningError(String.format("Unable to provision stack (URL - %s) with name - %s", config.getTemplateUrl(), config.getStackName()));
        }
    }

    @Override
    public Optional<String> getInfrastructureProvisioningStatus(String stackId) {
        try {
            StackStatus status = this.cfnApi.getStatus(stackId);
            return Optional.of(status.toString());
        } catch (StackInstanceNotFoundException e) {
            return Optional.empty();
        }
    }

    public void updateMigrationStage(MigrationStage stage) {
        migration.setStage(stage);
        migration.save();
    }

    public boolean startFilesystemMigration() {
        final Migration migration = getMigration();
        if (migration.getStage() == MigrationStage.NOT_STARTED) {
            return false;
        }
        final JobRunnerKey runnerKey = JobRunnerKey.of(S3UploadJobRunner.KEY);
        JobId jobId = JobId.of(S3UploadJobRunner.KEY + migration.getID());
        log.info("Starting filesystem migration");

        schedulerService.registerJobRunner(runnerKey, new S3UploadJobRunner(fsService));
        log.info("Registered new job runner for S3");

        // TODO don't schedule when the job is running, there are few edge cases we need to cover
//        JobDetails jobDetails = schedulerService.getJobDetails(jobId);
//        if (jobDetails != null) {
//            final Date nextRunTime = jobDetails.getNextRunTime();
//            if (nextRunTime != null && nextRunTime.before(new Date())) {
//                return false;
//            }
//        }

        JobConfig jobConfig = JobConfig.forJobRunnerKey(runnerKey)
                .withSchedule(null) // run now
                .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
        try {
            log.info("Scheduling new job for S3 upload runner");
            schedulerService.scheduleJob(jobId, jobConfig);
        } catch (SchedulerServiceException e) {
            log.error("Exception when scheduling S3 upload job", e);
            return false;
        }
        return true;
    }
}
