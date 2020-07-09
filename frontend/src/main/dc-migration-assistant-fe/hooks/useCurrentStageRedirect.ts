import { useState, useEffect } from 'react';
import { useHistory, useLocation } from 'react-router-dom';

import { migration } from '../api/migration';
import { getPathForStage } from '../utils/migration-stage-to-path';

export const useCurrentStageRedirect = (): boolean => {
    const [loading, setLoading] = useState<boolean>(true);

    const history = useHistory();
    const location = useLocation();

    useEffect(() => {
        setLoading(true);
        migration
            .getMigrationStage()
            .then(stage => {
                console.log(location.pathname);
                const stagePath = getPathForStage(stage);
                if (location.pathname.indexOf(stagePath) < 0) history.replace(stagePath);
            })
            .finally(() => setLoading(false));
    }, []);

    return loading;
};
