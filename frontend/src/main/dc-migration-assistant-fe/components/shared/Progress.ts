/**
 * **boldPrefix** - text that will be at the beginning of the message in bold. This should be used
 * to communicate *how much* data has been migrated
 *
 * **message** - the remainder of the complete text
 */
export type CompleteMessage = {
    boldPrefix: string;
    message: string;
};

/**
 * **phase**: Text describing the current phase of the transfer e.g. uploading, downloading, importing..
 * **completeness**: A fraction representing the percentage complete the transfer is.
 * **elapsedTimeSeconds**: The number of seconds the transfer has been going on for.
 * **error**: Text describing a non-critical error that the user may want to verify.
 * **completeMessage**: Message to display when the transfer has completed.
 * **failed**: a flag that should be true if a migration-stopping error has occcured, false otherwise.
 */
export type Progress = {
    phase: string;
    completeness?: number;
    elapsedTimeSeconds?: number;
    error?: string;
    completeMessage?: CompleteMessage;
    failed?: boolean;
};

/**
 * Builder or creating Progress objects. You must at least set a phase.
 */
export class ProgressBuilder {
    private phase: string;

    private completeness: number;

    private error: string;

    private completeMessage: CompleteMessage;

    private elapsedSeconds: number;

    private failed: boolean;

    setElapsedSeconds(seconds: number): ProgressBuilder {
        this.elapsedSeconds = seconds;
        return this;
    }

    setPhase(phase: string): ProgressBuilder {
        this.phase = phase;
        return this;
    }

    setCompleteness(completeness: number): ProgressBuilder {
        this.completeness = completeness;
        return this;
    }

    setError(error: string): ProgressBuilder {
        this.error = error;
        return this;
    }

    setCompleteMessage(boldPrefix: string, message: string): ProgressBuilder {
        this.completeMessage = {
            boldPrefix,
            message,
        };
        return this;
    }

    setFailed(failed: boolean) {
        this.failed = failed;

        return this;
    }

    build(): Progress {
        if (!this.phase) {
            throw new Error('must include phase in progress object');
        }

        const { phase, completeMessage, completeness, error, elapsedSeconds, failed } = this;

        return {
            phase,
            completeMessage,
            completeness,
            error,
            elapsedTimeSeconds: elapsedSeconds,
            failed: failed || false,
        };
    }
}

export interface ProgressCallback {
    (): Promise<Progress>;
}
