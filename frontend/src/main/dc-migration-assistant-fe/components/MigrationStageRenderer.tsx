import React, { FunctionComponent, useState, useEffect } from 'react';
import { Redirect } from 'react-router-dom';
import Spinner from '@atlaskit/spinner';
import { getPathForStage } from '../utils/migration-stage-to-path';
import { migration, MigrationStage } from '../api/migration';

export const MigrationStageRenderer: FunctionComponent = () => {
    const [loading, setLoading] = useState<boolean>(true);
    const [currentStage, setStage] = useState<MigrationStage>();

    useEffect(() => {
        setLoading(true);
        migration
            .getMigrationStage()
            .then(stage => {
                setStage(stage);
            })
            .catch(() => {
                setStage(MigrationStage.ERROR);
            })
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return <Spinner />;
    }

    return <Redirect to={getPathForStage(currentStage)} />;
};
