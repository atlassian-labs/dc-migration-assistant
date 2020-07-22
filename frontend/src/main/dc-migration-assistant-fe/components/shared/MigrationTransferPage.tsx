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

import React, { FunctionComponent, ReactNode, useEffect, useState } from 'react';
import styled from 'styled-components';
import Spinner from '@atlaskit/spinner';

import { MigrationTransferActions, MigrationStepState } from './MigrationTransferPageActions';
import { Progress } from './Progress';
import { migration, MigrationStage } from '../../api/migration';
import { MigrationProgress } from './MigrationTransferProgress';
import { CommandDetails as CommandResult } from '../../api/final-sync';
import { MigrationErrorSection } from './MigrationErrorSection';
import { ErrorFlag } from './ErrorFlag';
import { MigrationProcess, RetryProperties } from './MigrationProcess';
import { RetryMenu } from './RetryMigrationProcessMenu';

const POLL_INTERVAL_MILLIS = 8000;

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
    startButtonText: string;
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
    processes: Array<MigrationProcess>;

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

    padding-bottom: 15px;
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

const RetryMenuContainer = styled.div`
    margin-top: 5px;
`;

const ProgressContainer = styled.div`
    height: 185px;
`;

const getMigrationStepState = (started: boolean, finished: boolean): MigrationStepState => {
    if (finished) {
        return 'finished';
    }
    if (started) {
        return 'in_progress';
    }
    return 'not_started';
};

export const MigrationTransferPage: FunctionComponent<MigrationTransferProps> = ({
    description,
    infoLink,
    heading,
    nextText,
    nextRoute,
    startButtonText,
    processes,
    inProgressStages,
    startMigrationPhase,
    getDetails: getCommandresult,
}) => {
    const [processInfo, setProcessInfo] = useState<
        Array<{ progress: Progress; retryProps: RetryProperties }>
    >([]);
    // Content loading should be true when the entire content of the page is loading i.e. we don't know which actions should be
    // rendered or if the transfer is in progress
    const [contentLoading, setContentLoading] = useState<boolean>(true);
    // Progress loading should be true only when we are loading the transfer progress from scratch i.e. the transfer is starting or restarting
    const [progressLoading, setProgressLoading] = useState<boolean>(false);
    const [progressFetchingError, setProgressFetchingError] = useState<string>();
    const [started, setStarted] = useState<boolean>(false);
    const [finished, setFinished] = useState<boolean>(false);
    const [commandResult, setCommandResult] = useState<CommandResult>();

    const updateProgress = async (): Promise<void> => {
        Promise.all(
            processes.map(process =>
                process.getProgress().then(result => {
                    return {
                        progress: result,
                        retryProps: process.retryProps,
                    };
                })
            )
        )
            .then(result => {
                setProgressFetchingError('');
                setProcessInfo(result);
            })
            .catch(err => {
                setProgressFetchingError(err.message);
            })
            .finally((): void => {
                setProgressLoading(false);
            });
    };

    const startMigration = async (): Promise<void> => {
        setContentLoading(true);
        setProgressFetchingError('');
        return startMigrationPhase()
            .then(() => {
                setStarted(true);
            })
            .catch(err => {
                setProgressFetchingError(err.message);
            })
            .finally(() => setContentLoading(false));
    };

    useEffect(() => {
        setContentLoading(true);
        migration
            .getMigrationStage()
            .then(stage => {
                if (inProgressStages.includes(stage)) {
                    setStarted(true);
                    updateProgress();
                }
                setContentLoading(false);
            })
            .catch(() => {
                setStarted(false);
                setContentLoading(false);
            });
    }, []);

    useEffect(() => {
        if (getCommandresult && finished) {
            getCommandresult()
                .then(d => {
                    setCommandResult(d);
                })
                .catch(e => {
                    // eslint-disable-next-line no-console
                    console.log(e);
                });
        }
    }, [finished]);

    useEffect(() => {
        if (started) {
            const id = setInterval(async () => {
                await updateProgress();
            }, POLL_INTERVAL_MILLIS);

            setProgressLoading(true);
            updateProgress();

            return (): void => clearInterval(id);
        }
        return (): void => undefined;
    }, [started]);

    useEffect(() => {
        setFinished(
            processInfo.length > 0 &&
                processInfo.every(process => process.progress.completeness === 1)
        );
    }, [processInfo]);

    return (
        <TransferPageContainer>
            <TransferContentContainer>
                <h1>{heading}</h1>
                <p>{description}</p>
                {infoLink}
            </TransferContentContainer>
            {contentLoading ? (
                <Spinner />
            ) : (
                <>
                    <ErrorFlag
                        showError={progressFetchingError && progressFetchingError !== ''}
                        dismissErrorFunc={(): void => setProgressFetchingError('')}
                        title="Network error getting migration status"
                        description={`Check your internet connection and try refreshing - ${progressFetchingError}`}
                        id={progressFetchingError}
                    />
                    <TransferContentContainer>
                        {started &&
                            processInfo.map((process, index) => {
                                const { progress, retryProps } = process;
                                return (
                                    <>
                                        <ProgressContainer>
                                            {progressLoading ? (
                                                <Spinner />
                                            ) : (
                                                <MigrationProgress
                                                    key={progress.phase}
                                                    progress={progress}
                                                    loading={false}
                                                />
                                            )}
                                        </ProgressContainer>
                                        {index !== processInfo.length - 1 && <Divider />}
                                        {progress.errorMessage && (
                                            <RetryMenuContainer>
                                                <RetryMenu
                                                    {...retryProps}
                                                    onRetry={(): Promise<void> => {
                                                        setProgressLoading(true);
                                                        return retryProps
                                                            .onRetry()
                                                            .then(() => updateProgress())
                                                            .finally(() =>
                                                                setProgressLoading(false)
                                                            );
                                                    }}
                                                />
                                            </RetryMenuContainer>
                                        )}
                                    </>
                                );
                            })}
                        {commandResult?.errorMessage && (
                            <MigrationErrorSection result={commandResult} />
                        )}
                    </TransferContentContainer>
                    <TransferActionsContainer>
                        <MigrationTransferActions
                            state={getMigrationStepState(started, finished)}
                            nextText={nextText}
                            startButtonText={startButtonText}
                            nextRoute={nextRoute}
                            startMigrationPhase={startMigration}
                            onRefresh={updateProgress}
                            loading={contentLoading}
                        />
                    </TransferActionsContainer>
                </>
            )}
        </TransferPageContainer>
    );
};
