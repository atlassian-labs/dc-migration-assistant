const webpackConfigGenerator = require('../config/webpack.config');
const webpackConstants = require('../config/webpack.constants');

module.exports = {
  "stories": [
    "../src/**/*.stories.mdx",
    "../src/**/*.stories.@(js|jsx|ts|tsx)"
  ],
  "addons": [
    "@storybook/addon-links",
    "@storybook/addon-essentials",
    "@storybook/preset-create-react-app"
  ],
  webpackFinal: async config => {
    const webpackConfig = webpackConfigGenerator('production', { mode: 'development' });
    config.plugins.push(...webpackConfig.plugins);
    config.module.rules.push({
        test: /\.(tsx|ts)?$/,
        exclude: /node_modules/,
        use: [
            {
                loader: '@atlassian/i18n-properties-loader',
                options: {
                    i18nFiles: webpackConstants.MY_I18N_FILES,
                    disabled: false,
                },
            },
        ],
    });
    return config;
  },
}