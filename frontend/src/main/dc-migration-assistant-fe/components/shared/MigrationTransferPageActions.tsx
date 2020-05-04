import React, { FunctionComponent } from 'react';
import Button from '@atlaskit/button';
import { Link } from 'react-router-dom';
import { CancelButton } from './CancelButton';

export type MigrationTransferActionsProps = {
    /**
     * A function which will be called when the Refresh button is clicked
     */
    onRefresh: () => Promise<void>;
    /**
     * Whether the current migration transfer has completed or not
     */
    finished: boolean;
    /**
     * The text to display on the button that takes the user to the next step of the migration
     */
    nextText: string;
    /**
     * The path to take the user to whent hey click the "next" action button
     */
    nextRoute: string;
    /**
     * A function which will be called when the "start" action button is called.
     */
    startMigrationPhase: () => Promise<void>;
    /**
     * Whether the current migration transfer has already started or not
     */
    started: boolean;
    /**
     * Should be true if any of the above properties are being fetched
     */
    loading: boolean;
};

/**
 * A component which renders the actions that can be taken at a given step of the migration.
 * The "Cancel" action is always available and cancels the current step. There are three other
 * possible actions which can be availabl depending on the props:
 *   **Start**: Will be available when the current transfer hasn't started
 *   **Refresh**: Will be available when the current transfer is in progress
 *   **Next**: Will be available when the current transfer is completed. It's text can be overriden
 */
export const MigrationTransferActions: FunctionComponent<MigrationTransferActionsProps> = ({
    finished,
    nextText,
    nextRoute,
    startMigrationPhase,
    onRefresh: updateTransferProgress,
    started,
    loading,
}) => {
    const defaultButtonStyle = {
        padding: '5px',
    };

    const marginButtonStyle = {
        ...defaultButtonStyle,
        marginRight: '20px',
    };

    const StartButton = (
        <Button
            style={marginButtonStyle}
            isLoading={loading}
            appearance="primary"
            onClick={startMigrationPhase}
        >
            Start
        </Button>
    );

    let ActionButton = StartButton;

    if (finished) {
        const NextButton = (
            <Link to={nextRoute}>
                <Button style={defaultButtonStyle} appearance="primary">
                    {nextText}
                </Button>
            </Link>
        );
        ActionButton = NextButton;
    } else if (started) {
        const RefreshButton = (
            <Button style={marginButtonStyle} isLoading={loading} onClick={updateTransferProgress}>
                Refresh
            </Button>
        );
        ActionButton = RefreshButton;
    }

    return (
        <>
            {ActionButton}
            <CancelButton />
        </>
    );
};
