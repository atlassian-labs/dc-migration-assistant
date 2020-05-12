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

import React, { ReactElement, FunctionComponent } from 'react';
import { Moment } from 'moment';
import SectionMessage from '@atlaskit/section-message';
import Spinner from '@atlaskit/spinner';
import ProgressBar, { SuccessProgressBar } from '@atlaskit/progress-bar';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';

import { Progress } from './Progress';
import {
    calculateDurationFromBeginning,
    calcualateDurationFromElapsedSeconds,
    calculateStartedFromElapsedSeconds,
} from './migration-timing';

export type MigrationProgressProps = {
    progress: Progress;
    /**
     * Should be true when the progress is being fetched
     */
    loading: boolean;
    /**
     * A moment representing the time that the transfer was started
     */
    startedMoment: Moment;
};

/**
 * A component which renders the progress of the current transfer. It renders
 * a progress bar and elapsed time. It handles when the completeness is indeterminate.
 */
export const MigrationProgress: FunctionComponent<MigrationProgressProps> = ({
    progress,
    loading,
    startedMoment,
}) => {
    if (loading) {
        return (
            <>
                <Spinner />
                <ProgressBar isIndeterminate />
                <Spinner />
            </>
        );
    }

    const duration =
        calculateDurationFromBeginning(startedMoment) ||
        calcualateDurationFromElapsedSeconds(progress.elapsedTimeSeconds);

    return (
        <>
            {progress?.completeness === 1 && progress?.completeMessage && (
                <SectionMessage appearance="confirmation">
                    <strong>{progress.completeMessage.boldPrefix}</strong>{' '}
                    {progress.completeMessage.message}
                </SectionMessage>
            )}

            <>
                <h4>
                    {progress.phase}
                    {progress.completeness === undefined &&
                        ` (${I18n.getText('atlassian.migration.datacenter.common.estimating')}...)`}
                </h4>
                {progress.completeness ? (
                    <SuccessProgressBar value={progress.completeness} />
                ) : (
                    <ProgressBar isIndeterminate />
                )}
                <p>
                    {I18n.getText(
                        'atlassian.migration.datacenter.common.progress.started',
                        (
                            startedMoment ||
                            calculateStartedFromElapsedSeconds(progress.elapsedTimeSeconds)
                        ).format('D/MMM/YY h:mm A')
                    )}
                </p>
                <p>
                    {duration &&
                        I18n.getText(
                            'atlassian.migration.datacenter.common.progress.mins_elapsed',
                            `${duration.days * 24 + duration.hours}`,
                            `${duration.minutes}`
                        )}
                </p>
            </>
        </>
    );
};
