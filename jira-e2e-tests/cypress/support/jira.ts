export const createAppContext = (base: string, contextPath: string): AppContext => {
    const baseURL = base + contextPath;
    const pluginPathWithContext = contextPath + '/plugins/servlet/dc-migration-assistant';
    const pluginFullUrl = base + pluginPathWithContext;

    const jiraContext: AppContext = {
        base: base,
        context: contextPath,
        baseURL: baseURL,
        welcomeURL: baseURL + '/secure/WelcomeToJIRA.jspa',
        loginURL: baseURL + '/login.jsp',
        sudoURL: baseURL + '/secure/admin/WebSudoAuthenticate!default.jspa',
        upmURL: baseURL + '/plugins/servlet/upm',
        pluginPath: pluginPathWithContext,
        migrationBase: pluginFullUrl,
        migrationHome: pluginFullUrl + '/home',
    };
    return jiraContext;
};

export const ampsContext = createAppContext('http://localhost:2990', '/jira');
export const devserverContext = createAppContext('http://localhost:3333', '');
export const composeContext = createAppContext('http://jira:8080', '/jira');

/**
 *  Returns application context to access product and plugin URLs
 */
export const getContext = () => createAppContext('http://localhost:2990', '/jira');
