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

import React, { FunctionComponent, useEffect } from 'react';
import { I18n } from '@atlassian/wrm-react-i18n';

import { MigrationTransferPage } from '../../shared/MigrationTransferPage';
import { ProgressBuilder, ProgressCallback } from '../../shared/Progress';
import { provisioning, ProvisioningStatus } from '../../../api/provisioning';
import { MigrationStage } from '../../../api/migration';
import { fsPath } from '../../../utils/RoutePaths';

const getDeploymentProgress: ProgressCallback = () => {
    return provisioning
        .getProvisioningStatus()
        .then(result => {
            const builder = new ProgressBuilder();
            switch (result) {
                case ProvisioningStatus.Complete:
                    builder.setPhase('Deployment Complete');
                    builder.setCompleteness(1);
                    break;
                case ProvisioningStatus.ProvisioningApplicationStack:
                    builder.setPhase('Deploying Jira infrastructure');
                    builder.setCompleteness(0.25);
                    break;
                case ProvisioningStatus.PreProvisionMigrationStack:
                    builder.setPhase('Preparing migration infrastructure deployment');
                    builder.setCompleteness(0.5);
                    break;
                case ProvisioningStatus.ProvisioningMigrationStack:
                    builder.setPhase('Deploying migration infrastructure');
                    builder.setCompleteness(0.75);
                    break;
                default:
                    builder.setPhase(
                        I18n.getText('atlassian.migration.datacenter.provision.aws.status.error')
                    );
                    builder.setError(`Unexpected deployment status ${result}`);
            }
            return builder.build();
        })
        .catch(err => {
            const builder = new ProgressBuilder();
            builder.setPhase(
                I18n.getText('atlassian.migration.datacenter.provision.aws.status.error')
            );
            builder.setError(err.message);
            builder.setCompleteness(0);
            builder.setFailed(true);
            return builder.build();
        });
};

const inProgressStages = [
    MigrationStage.PROVISION_APPLICATION,
    MigrationStage.PROVISION_APPLICATION_WAIT,
    MigrationStage.PROVISION_MIGRATION_STACK,
    MigrationStage.PROVISION_MIGRATION_STACK_WAIT,
];

type DeploymentMode = {
    deploymentMode: string;
};

export const ProvisioningStatusPage: FunctionComponent<DeploymentMode> = ({ deploymentMode }) => {
    /*
     * Pages that are navigated to from long content pages
     * stay scrolled down. Scroll the window up after render.
     */
    useEffect(() => {
        window.scrollTo(0, 0);
    });
    const migrationTransferPageDescription =
        deploymentMode === 'with-vpc'
            ? I18n.getText('atlassian.migration.datacenter.provision.aws.wait.description.with.vpc')
            : I18n.getText(
                  'atlassian.migration.datacenter.provision.aws.wait.description.without.vpc'
              );

    return (
        <MigrationTransferPage
            description={migrationTransferPageDescription}
            infoLink={
                <a
                    target="_blank"
                    rel="noreferrer noopener"
                    href="https://console.aws.amazon.com/cloudformation"
                >
                    {I18n.getText(
                        'atlassian.migration.datacenter.provision.aws.wait.description.link'
                    )}
                </a>
            }
            getProgress={getDeploymentProgress}
            inProgressStages={inProgressStages}
            heading={I18n.getText('atlassian.migration.datacenter.provision.aws.title')}
            nextText={I18n.getText('atlassian.migration.datacenter.generic.next')}
            // This page is only rendered when provisioning has already started. The deployment will be started by the QuickstartDeploy page
            startMigrationPhase={Promise.resolve}
            nextRoute={fsPath}
        />
    );
};
