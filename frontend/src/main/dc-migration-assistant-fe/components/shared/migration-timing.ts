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

import moment, { Moment } from 'moment';

type TransferDuration = {
    days: number;
    hours: number;
    minutes: number;
};

export const calculateDurationFromBeginning = (start: Moment): TransferDuration => {
    if (!start) {
        return undefined;
    }

    const elapsedTime = moment.duration(moment.now() - start.valueOf());

    return {
        days: elapsedTime.days(),
        hours: elapsedTime.hours(),
        minutes: elapsedTime.minutes(),
    };
};

export const calculateStartedFromElapsedSeconds = (elapsedSeconds: number): Moment => {
    const now = moment();
    return now.subtract(elapsedSeconds, 'seconds');
};

export const calcualateDurationFromElapsedSeconds = (seconds: number): TransferDuration => {
    if (!seconds) {
        return undefined;
    }

    const duration = moment.duration(seconds, 'seconds');

    return {
        days: duration.days(),
        hours: duration.hours(),
        minutes: duration.minutes(),
    };
};
