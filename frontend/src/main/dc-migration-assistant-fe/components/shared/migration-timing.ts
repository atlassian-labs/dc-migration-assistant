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
