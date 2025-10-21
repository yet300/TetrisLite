config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
    }
};
const isProduction = config.mode === "production";

config.output = config.output || {};
config.output.publicPath = isProduction ? "/TetrisLite/" : "/";

const CopyWebpackPlugin = require('copy-webpack-plugin');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            '../../node_modules/sql.js/dist/sql-wasm.wasm'
        ]
    })
);