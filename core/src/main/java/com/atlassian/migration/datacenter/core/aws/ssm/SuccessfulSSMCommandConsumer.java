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

package com.atlassian.migration.datacenter.core.aws.ssm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.model.CommandInvocationStatus;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;
import software.amazon.awssdk.services.ssm.model.InvocationDoesNotExistException;

public abstract class SuccessfulSSMCommandConsumer<T> {

    private static final Logger logger = LoggerFactory.getLogger(SuccessfulSSMCommandConsumer.class);


    private final SSMApi ssmApi;
    private final String commandId;
    private final String instanceId;

    protected SuccessfulSSMCommandConsumer(SSMApi ssmApi, String commandId, String instanceId) {
        this.ssmApi = ssmApi;
        this.commandId = commandId;
        this.instanceId = instanceId;
    }

    public T handleCommandOutput(int maxCommandStatusRetries) throws UnsuccessfulSSMCommandInvocationException, SSMCommandInvocationProcessingError {
        GetCommandInvocationResponse command = null;
        for (int i = 0; i < maxCommandStatusRetries; i++) {
            logger.debug("Checking delivery of s3 sync ssm command. Attempt {}", i);

            try {
                command = ssmApi.getSSMCommand(commandId, instanceId);
                final CommandInvocationStatus status = command.status();

                logger.debug("s3 sync command status is {}", status);

                if (status.equals(CommandInvocationStatus.SUCCESS)) {
                    return handleSuccessfulCommand(command);
                }
            } catch (InvocationDoesNotExistException e) {
                logger.debug("Command does not exist - maybe it hasn't reached the EC2 instance yet. Will continue retrying");
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.error("interrupted while waiting for s3 sync ssm command to be delivered", e);
                throw new UnsuccessfulSSMCommandInvocationException("Interrupted while waiting to check command status", e);
            }
        }
        throw new UnsuccessfulSSMCommandInvocationException(
                String.format(
                        "Command never completed successfully. Latest status is: %s. Latest response from SSM API is: %s",
                        command.status().toString(),
                        command.sdkHttpResponse().statusText()));
    }

    protected abstract T handleSuccessfulCommand(GetCommandInvocationResponse commandInvocation) throws SSMCommandInvocationProcessingError;

    public static class UnsuccessfulSSMCommandInvocationException extends Exception {
        public UnsuccessfulSSMCommandInvocationException(String message) {
            super(message);
        }

        public UnsuccessfulSSMCommandInvocationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SSMCommandInvocationProcessingError extends Exception {
        public SSMCommandInvocationProcessingError(String message) {
            super(message);
        }

        public SSMCommandInvocationProcessingError(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
