
export const createAppContext = (
    base: string,
    contextPath: string,
    username: string = 'admin'
): AppContext => {
    const baseURL = base + contextPath;
    const pluginPathWithContext = contextPath + '/plugins/servlet/dc-migration-assistant';
    const pluginFullUrl = base + pluginPathWithContext;
    const password = Cypress.env('ADMIN_PASSWORD')
    assert.isDefined(
        password,
        'You need to define admin password via `CYPRESS_ADMIN_PASSWORD env variable`'
    );

    const jiraContext: AppContext = {
        username: username,
        password: password,
        base: base,
        context: contextPath,
        baseURL: baseURL,
        welcomeURL: baseURL + '/secure/WelcomeToJIRA.jspa',
        loginURL: baseURL + '/login.jsp',
        sudoURL: baseURL + '/secure/admin/WebSudoAuthenticate!default.jspa',
        upmURL: baseURL + '/plugins/servlet/upm',
        restBaseURL: baseURL + '/rest/api/2',
        restAuth: {
            user: username,
            pass: password,
            sendImmediately: true,
        },
        pluginPath: pluginPathWithContext,
        pluginFullUrl: pluginFullUrl,
        pluginHomePage: pluginFullUrl + '/home',
    };
    return jiraContext;
};

export const ampsContext = createAppContext('http://localhost:2990', '/jira');
export const fsDevServerContext = createAppContext('http://localhost:3333', '');
export const dockerComposeContext = createAppContext('http://jira:8080', '/jira');
export const dockerLocalContext = createAppContext('http://localhost:2990', '/jira');

/**
 *  Returns application context to access product and plugin URLs
 */
export const getContext = () => {
    const context = Cypress.env('CONTEXT');
    console.log(context);
    switch (context) {
        case 'amps': {
            return ampsContext;
        }
        case 'fsdev': {
            return fsDevServerContext;
        }
        case 'local': {
            return dockerLocalContext;
        }
        default:
            return dockerComposeContext;
    }
};


export const reindex = (issues: string[], ctx: AppContext, targetURL: string) => {
    cy.request({
        url: targetURL+`/rest/api/2/reindex/issue?issueID=${issues.join(",")}`,
        method: "POST",
        headers: {"Origin": targetURL},
        auth: ctx.restAuth,
    })
}
