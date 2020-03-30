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

import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.aws.ssm.SuccessfulSSMCommandConsumer;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;

public class EnsureSuccessfulSSMCommandConsumer extends SuccessfulSSMCommandConsumer<Void> {
    public EnsureSuccessfulSSMCommandConsumer(SSMApi ssmApi, String commandId, String instanceId) {
        super(ssmApi, commandId, instanceId);
    }

    @Override
    protected Void handleSuccessfulCommand(GetCommandInvocationResponse commandInvocation) {
        return null;
    }
}
