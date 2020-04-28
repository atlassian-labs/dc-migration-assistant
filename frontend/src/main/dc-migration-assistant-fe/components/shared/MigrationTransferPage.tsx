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
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { Link } from 'react-router-dom';
import moment, { Moment } from 'moment';
import Spinner from '@atlaskit/spinner';
import { I18n } from '@atlassian/wrm-react-i18n';

import { MigrationTransferActions } from './MigrationTransferPageActions';
import { overviewPath } from '../../utils/RoutePaths';
import { ProgressCallback, Progress } from './Progress';
import { migration, MigrationStage } from '../../api/migration';
import { MigrationProgress } from './MigrationTransferProgress';

const POLL_INTERVAL_MILLIS = 3000;

export type MigrationTransferProps = {
    heading: string;
    description: string;
    nextText: string;
    startMoment?: moment.Moment;
    inProgressStages: Array<MigrationStage>;
    startMigrationPhase: () => Promise<void>;
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
                console.log('setting error from start');
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

    const transferError = progress?.error || error;

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
                            <SectionMessage appearance="error">{transferError}</SectionMessage>
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
                            completeness={progress?.completeness}
                            nextText={nextText}
                            startMigrationPhase={startMigration}
                            updateTransferProgress={updateProgress}
                            started={started}
                            loading={loading}
                        />
                        <Link to={overviewPath}>
                            <Button style={{ marginLeft: '20px', paddingLeft: '5px' }}>
                                {I18n.getText('atlassian.migration.datacenter.generic.cancel')}
                            </Button>
                        </Link>
                    </TransferActionsContainer>
                </>
            )}
        </TransferPageContainer>
    );
};
