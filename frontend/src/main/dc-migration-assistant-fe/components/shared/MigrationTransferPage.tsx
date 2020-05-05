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

import React, { FunctionComponent, useState, useEffect } from 'react';
import SectionMessage from '@atlaskit/section-message';
import styled from 'styled-components';
import moment from 'moment';
import Spinner from '@atlaskit/spinner';
import { Redirect } from 'react-router-dom';

import { I18n } from '@atlassian/wrm-react-i18n';
import { MigrationTransferActions } from './MigrationTransferPageActions';
import { ProgressCallback, Progress } from './Progress';
import { migration, MigrationStage } from '../../api/migration';
import { MigrationProgress } from './MigrationTransferProgress';
import { migrationErrorPath } from '../../utils/RoutePaths';

const POLL_INTERVAL_MILLIS = 3000;

export type MigrationTransferProps = {
    /**
     * The heading for the current migration transfer. Should follow pattern "Step X of Y: Z"
     */
    heading: string;
    /**
     * A description for what the current transfer does. Will be rendered below the title
     */
    description: string;
    /**
     * @see MigrationTransferActionsProps
     */
    nextText: string;
    /**
     * @see MigrationTransferActionsProps
     */
    nextRoute: string;
    /**
     * @see MigrationProgressProps
     */
    startMoment?: moment.Moment;
    /**
     * The MigrationStages where this transfer is "in progress"
     * @see MigrationStage
     */
    inProgressStages: Array<MigrationStage>;
    /**
     * A function which starts this migration transfer
     */
    startMigrationPhase: () => Promise<void>;
    /**
     * A function which will be called to get the progress of the current transfer
     */
    getProgress: ProgressCallback;
};

const TransferPageContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
    margin-right: auto;
    margin-bottom: auto;
    padding-left: 15px;
`;

const TransferContentContainer = styled.div`
    display: flex;
    flex-direction: column;
    padding-right: 30px;

    padding-bottom: 5px;
`;

const TransferActionsContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: flex-start;

    margin-top: 20px;
`;

export const MigrationTransferPage: FunctionComponent<MigrationTransferProps> = ({
    description,
    heading,
    nextText,
    nextRoute,
    startMoment,
    getProgress,
    inProgressStages,
    startMigrationPhase,
}) => {
    const [progress, setProgress] = useState<Progress>();
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>();
    const [started, setStarted] = useState<boolean>(false);

    const updateProgress = (): Promise<void> => {
        return getProgress()
            .then(result => {
                setProgress(result);
                setLoading(false);
            })
            .catch(err => {
                setError(err);
                setLoading(false);
            });
    };

    const startMigration = (): Promise<void> => {
        setLoading(true);
        setError('');
        return startMigrationPhase()
            .then(() => {
                setStarted(true);
            })
            .catch(err => {
                setError(err.message);
                setLoading(false);
            });
    };

    useEffect(() => {
        setLoading(true);
        migration
            .getMigrationStage()
            .then(stage => {
                if (inProgressStages.includes(stage)) {
                    setStarted(true);
                    return updateProgress();
                }
                setLoading(false);
            })
            .catch(() => {
                setStarted(false);
                setLoading(false);
            });
    }, []);

    useEffect(() => {
        if (started) {
            const id = setInterval(async () => {
                await updateProgress();
            }, POLL_INTERVAL_MILLIS);

            setLoading(true);
            updateProgress();

            return (): void => clearInterval(id);
        }
        return (): void => undefined;
    }, [started]);

    if (progress?.failed) {
        return <Redirect to={migrationErrorPath} push />;
    }

    const transferError = progress?.error || error;

    const LearnMoreLink =
        'https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-Knownissue';
    return (
        <TransferPageContainer>
            <TransferContentContainer>
                <h1>{heading}</h1>
                <p>{description}</p>
            </TransferContentContainer>
            {loading ? (
                <Spinner />
            ) : (
                <>
                    <TransferContentContainer>
                        {transferError && (
                            <SectionMessage appearance="error">
                                <p>
                                    {transferError}{' '}
                                    <a
                                        target="_blank"
                                        rel="noreferrer noopener"
                                        href={LearnMoreLink}
                                    >
                                        {I18n.getText(
                                            'atlassian.migration.datacenter.common.learn_more'
                                        )}
                                    </a>
                                </p>
                            </SectionMessage>
                        )}
                        {started && (
                            <MigrationProgress
                                progress={progress}
                                loading={loading}
                                startedMoment={startMoment}
                            />
                        )}
                    </TransferContentContainer>
                    <TransferActionsContainer>
                        <MigrationTransferActions
                            finished={progress?.completeness === 1}
                            nextText={nextText}
                            nextRoute={nextRoute}
                            startMigrationPhase={startMigration}
                            onRefresh={updateProgress}
                            started={started}
                            loading={loading}
                        />
                    </TransferActionsContainer>
                </>
            )}
        </TransferPageContainer>
    );
};
