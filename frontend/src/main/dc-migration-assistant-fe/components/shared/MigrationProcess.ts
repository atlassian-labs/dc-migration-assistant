import { ProgressCallback } from './Progress';

export interface RetryCallback {
    (): Promise<void>;
}

export type RetryProperties = {
    /**
     * Text to display on the retry button
     */
    retryText: string;
    /**
     * A callback which will retry the operation
     */
    onRetry: RetryCallback;
    /**
     * A boolean to dictate whether or not failures
     * can be ignored and the migration continued
     */
    canContinueOnFailure: boolean;
    /**
     * The path to be directed to when ignoring errors and continuing.
     * Must be set when canContinueOnFailure is true
     */
    continuePath?: string;
};

export interface MigrationProcess {
    getProgress: ProgressCallback;
    retryProps: RetryProperties;
}
