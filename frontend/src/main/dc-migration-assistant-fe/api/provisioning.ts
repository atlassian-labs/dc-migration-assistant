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

import { I18n } from '@atlassian/wrm-react-i18n';
import { callAppRest } from '../utils/api';
import { ASIDescription } from '../components/aws/quickstart/asi/ExistingASIConfiguration';

export enum ProvisioningStatus {
    ProvisioningApplicationStack,
    PreProvisionMigrationStack,
    ProvisioningMigrationStack,
    Complete,
}

enum RestApiPathConstants {
    GetDeploymentStatusPath = 'aws/stack/status',
    GetASIInfoPath = `/aws/global-infrastructure/asi`,
}

type InfrastructureDeploymentState =
    | 'CREATE_IN_PROGRESS'
    | 'CREATE_COMPLETE'
    | 'CREATE_FAILED'
    | 'PREPARING_MIGRATION_INFRASTRUCTURE_DEPLOYMENT';

type StackStatusResponse = {
    status: {
        state: InfrastructureDeploymentState;
        reason: string;
    };
    phase?: 'app_infra' | 'migration_infra';
};

type StackStatusErrorResponse = {
    error: string;
};

type ASIInfo = {
    name: string;
    id: string;
    prefix: string;
};

export const provisioning = {
    getProvisioningStatus: async (): Promise<ProvisioningStatus> => {
        return callAppRest('GET', RestApiPathConstants.GetDeploymentStatusPath)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status) {
                    const statusResp = resp as StackStatusResponse;
                    const { phase, status } = statusResp;
                    switch (status.state) {
                        case 'PREPARING_MIGRATION_INFRASTRUCTURE_DEPLOYMENT':
                            return ProvisioningStatus.PreProvisionMigrationStack;
                        case 'CREATE_IN_PROGRESS':
                            return phase === 'app_infra'
                                ? ProvisioningStatus.ProvisioningApplicationStack
                                : ProvisioningStatus.ProvisioningMigrationStack;
                        case 'CREATE_COMPLETE':
                            if (phase === 'app_infra') {
                                return ProvisioningStatus.PreProvisionMigrationStack;
                            }
                            return ProvisioningStatus.Complete;
                        case 'CREATE_FAILED':
                            throw new Error(
                                I18n.getText(
                                    'atlassian.migration.datacenter.provision.aws.status.failed',
                                    status.reason
                                )
                            );
                        default:
                            throw new Error(
                                I18n.getText(
                                    'atlassian.migration.datacenter.provision.aws.status.unexpected',
                                    `${phase}`
                                )
                            );
                    }
                }
                if (resp.error) {
                    // hanle error
                    const errorResponse = resp as StackStatusErrorResponse;
                    throw new Error(errorResponse.error);
                }
                throw new Error(
                    I18n.getText(
                        'atlassian.migration.datacenter.provision.aws.status.badServer',
                        `${JSON.stringify(resp)}`
                    )
                );
            });
    },
    getASIs: async (): Promise<Array<ASIDescription>> => {
        const json = await callAppRest('GET', RestApiPathConstants.GetASIInfoPath).then(resp => {
            if (resp.ok) return resp.json();
            return Promise.resolve([]);
        });

        const stackData = json as Array<ASIInfo>;

        return stackData.map(data => ({ prefix: data.prefix, stackName: data.name }));
    },
};
