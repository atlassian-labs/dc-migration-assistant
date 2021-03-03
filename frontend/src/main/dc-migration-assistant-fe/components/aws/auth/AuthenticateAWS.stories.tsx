import React, { ReactNode } from 'react';
import { AuthenticateAWS } from './AuthenticateAWS';

export default { title: 'AWS Credentials' };

export const AWSAuthentication = (): ReactNode => (
    <AuthenticateAWS
        onSubmitCreds={(): Promise<string> => {
            // eslint-disable-next-line no-alert
            alert('Thanks for submitting your credentials, we promise not to steal them');
            return Promise.resolve('blah');
        }}
        getRegions={(): Promise<Array<string>> =>
            Promise.resolve(['us-east-1', 'us-east-2', 'ap-southeast-2', 'the-moon'])
        }
    />
);
