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
