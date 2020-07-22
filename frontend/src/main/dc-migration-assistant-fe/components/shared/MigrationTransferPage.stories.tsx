import React, { ReactNode } from 'react';
import { MemoryRouter as Router } from 'react-router-dom';
import { MigrationTransferPage } from './MigrationTransferPage';
import { Progress } from './Progress';
import { MigrationStage } from '../../api/migration';

export default { title: 'Migration step' };

const finishedAfterCalls = 3;

const storybookTransferPageDefaultProps = {
    heading: 'Migrate content',
    nextText: 'Next step',
    nextRoute: '/',
    startButtonText: 'Start',
    inProgressStages: new Array<MigrationStage>(),
    startMigrationPhase: (): Promise<void> => {
        return new Promise(resolve => setTimeout(resolve, 1000));
    },
};

const completeMessage = {
    boldPrefix: '25 of 25 files',
    message: 'were successfully migrated',
};

export const happyPath = (): ReactNode => {
    let progressFetchCount = 0;

    return (
        <Router>
            <MigrationTransferPage
                {...storybookTransferPageDefaultProps}
                description="This is a migration process which has not been started yet. Once you click start, it will finish after three ticks."
                processes={[
                    {
                        getProgress: (): Promise<Progress> => {
                            if (progressFetchCount === 3) {
                                return Promise.resolve({
                                    phase: 'Completed content migration',
                                    completeness: 1,
                                    elapsedTimeSeconds: 18,
                                    completeMessage,
                                });
                            }
                            const result = Promise.resolve({
                                phase: 'Migrating content',
                                completeness: progressFetchCount / finishedAfterCalls,
                                elapsedTimeSeconds: 6 * progressFetchCount,
                                completeMessage,
                            });

                            if (progressFetchCount !== 3) {
                                progressFetchCount += 1;
                            }

                            return result;
                        },
                        retryProps: {
                            retryText: 'Retry',
                            onRetry: (): Promise<void> => Promise.resolve(),
                            canContinueOnFailure: false,
                        },
                    },
                ]}
            />
        </Router>
    );
};

export const unhappyPath = (): ReactNode => {
    let progressFetchCount = 0;

    return (
        <Router>
            <MigrationTransferPage
                {...storybookTransferPageDefaultProps}
                description="This is a migration process which has not been started yet. Once you click start, It will update twice, then fail."
                processes={[
                    {
                        getProgress: (): Promise<Progress> => {
                            if (progressFetchCount === 3) {
                                return Promise.resolve({
                                    phase: 'Failed to migrate content',
                                    completeness: 0.7,
                                    elapsedTimeSeconds: 18,
                                    errorMessage:
                                        'Something went wrong while running your migration. Retry and see if it improves things',
                                    completeMessage,
                                });
                            }
                            const result = Promise.resolve({
                                phase: 'Migrating content',
                                completeness: progressFetchCount / finishedAfterCalls,
                                elapsedTimeSeconds: 6 * progressFetchCount,
                                completeMessage,
                            });

                            if (progressFetchCount !== 3) {
                                progressFetchCount += 1;
                            }

                            return result;
                        },
                        retryProps: {
                            retryText: 'Retry',
                            onRetry: (): Promise<void> => {
                                progressFetchCount = 0;
                                return new Promise(resolve => setTimeout(resolve, 1000));
                            },
                            canContinueOnFailure: false,
                        },
                    },
                ]}
            />
        </Router>
    );
};
