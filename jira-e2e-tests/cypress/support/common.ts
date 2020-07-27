type AppContext = {
    username: string;
    password: string;
    base: string;
    context: string;
    baseURL: string;
    welcomeURL: string;
    loginURL: string;
    sudoURL: string;
    upmURL: string;
    restBaseURL: string;
    pluginPath: string;
    pluginFullUrl: string;
    pluginHomePage: string;
};

type CloudFormationFormValues = {
    stackName: string;
    dbMasterPassword: string;
    dbPassword: string;
    dbMultiAz?: boolean;
    cidrBlock?: string;
};

type AWSCredentials = {
    keyId: string;
    secretKey: string;
};
