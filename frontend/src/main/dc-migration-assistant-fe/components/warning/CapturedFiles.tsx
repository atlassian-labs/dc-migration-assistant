import React, { FunctionComponent, useEffect, useState } from 'react';

import { I18n } from '@atlassian/wrm-react-i18n';
import Spinner from '@atlaskit/spinner';
import { fs } from '../../api/fs';

export const CapturedFiles: FunctionComponent = () => {
    const [loading, setLoading] = useState<boolean>(false);
    const [capturedFiles, setCapturedFiles] = useState<Array<string>>([]);

    useEffect(() => {
        setLoading(true);
        fs.getCapturedFiles()
            .then(setCapturedFiles)
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return <Spinner />;
    }

    return (
        <div>
            <p>
                {I18n.getText('atlassian.migration.datacenter.warning.capturedFiles.description')}
            </p>
            <ul>
                {capturedFiles.map(file => (
                    <li key={file}>{file}</li>
                ))}
            </ul>
        </div>
    );
};
