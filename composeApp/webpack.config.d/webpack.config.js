if (config.mode === 'production') {
    config.output.publicPath = '/TetrisLite/';
}

config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
    }
};

const CopyWebpackPlugin = require('copy-webpack-plugin');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: '../../node_modules/@cashapp/sqldelight-sqljs-worker/sqljs.worker.js',
                to: '.'
            },
            {
                from: '../../node_modules/sql.js/dist/sql-wasm.wasm',
                to: '.'
            },
            {
                from: '../../node_modules/sql.js/dist/sql.js',
                to: '.'
            }
        ]
    })
);