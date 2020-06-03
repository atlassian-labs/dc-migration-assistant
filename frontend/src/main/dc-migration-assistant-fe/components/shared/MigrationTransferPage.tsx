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

import React, { FunctionComponent, useState, useEffect, ReactNode } from 'react';
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
import { CommandDetails as CommandResult } from '../../api/final-sync';
import { MigrationErrorSection } from './MigrationErrorSection';

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
     * An optional hyperlink that can be used to direct the user to more detail
     */
    infoLink?: ReactNode;
    /**
     * @see MigrationTransferActionsProps
     */
    nextText: string;
    /**
     * @see MigrationTransferActionsProps
     */
    nextRoute: string;
    /**
     * @see MigrationTransferActionsProps
     */
    startButtonText?: string;
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

    getDetails?: () => Promise<CommandResult>;
};

const TransferPageContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
    margin-right: auto;
    margin-bottom: auto;
    padding-left: 15px;
    max-width: 920px;
`;

const TransferContentContainer = styled.div`
    display: flex;
    flex-direction: column;
    padding-right: 30px;

    padding-bottom: 30px;
`;

const TransferActionsContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: flex-start;

    margin-top: 20px;
`;

const Divider = styled.div`
    margin-top: 30px;
    margin-bottom: 20px;
    border-bottom: 2px solid rgb(223, 225, 230);
`;

export const MigrationTransferPage: FunctionComponent<MigrationTransferProps> = ({
    description,
    infoLink,
    heading,
    nextText,
    nextRoute,
    startButtonText,
    startMoment,
    getProgress,
    inProgressStages,
    startMigrationPhase,
    getDetails: getCommandresult,
}) => {
    const [progressList, setProgressList] = useState<Array<Progress>>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [progressFetchingError, setProgressFetchingError] = useState<string>();
    const [started, setStarted] = useState<boolean>(false);
    const [finished, setFinished] = useState<boolean>(false);
    const [commandResult, setCommandResult] = useState<CommandResult>();

    const updateProgress = async (): Promise<void> => {
        return getProgress()
            .then(result => {
                setProgressList(result);
                setLoading(false);
                const allFinished =
                    result.length > 0 && result.every(progress => progress?.completeness === 1);
                setFinished(allFinished);
            })
            .catch(err => {
                setProgressFetchingError(err.message);
                setLoading(false);
            });
    };

    const startMigration = async (): Promise<void> => {
        setLoading(true);
        setProgressFetchingError('');
        return startMigrationPhase()
            .then(() => {
                setStarted(true);
            })
            .catch(err => {
                setProgressFetchingError(err.message);
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
                    updateProgress();
                }
                setLoading(false);
            })
            .catch(() => {
                setStarted(false);
                setLoading(false);
            });
    }, []);

    useEffect(() => {
        if (getCommandresult && finished) {
            getCommandresult()
                .then(d => {
                    setCommandResult(d);
                })
                .catch(e => {
                    console.log(e);
                });
        }
    }, [finished]);

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

    if (progressList.some(progress => progress?.failed)) {
        return <Redirect to={migrationErrorPath} push />;
    }

    const transferError = progressList
        .filter(progress => progress?.errorMessage)
        .map(progress => {
            return <p key={progress.phase}>{progress?.errorMessage}</p>;
        });

    const LearnMoreLink =
        'https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-errors';
    return (
        <TransferPageContainer>
            <TransferContentContainer>
                <h1>{heading}</h1>
                <p>{description}</p>
                {infoLink}
            </TransferContentContainer>
            {loading ? (
                <Spinner />
            ) : (
                <>
                    <TransferContentContainer>
                        {(transferError.length !== 0 || progressFetchingError) && (
                            <SectionMessage appearance="error">
                                {...transferError}
                                <p>
                                    {progressFetchingError || ''}{' '}
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
                        {started &&
                            progressList.map((progress, index) => (
                                <>
                                    <MigrationProgress
                                        key={progress.phase}
                                        progress={progress}
                                        loading={loading}
                                        startedMoment={startMoment}
                                    />
                                    {index !== progressList.length - 1 && <Divider />}
                                </>
                            ))}
                        {commandResult?.errorMessage && (
                            <MigrationErrorSection result={commandResult} />
                        )}
                    </TransferContentContainer>
                    <TransferActionsContainer>
                        <MigrationTransferActions
                            finished={finished}
                            nextText={nextText}
                            startButtonText={startButtonText}
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
