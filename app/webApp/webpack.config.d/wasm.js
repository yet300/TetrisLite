// As per https://cashapp.github.io/sqldelight/js_sqlite/

var CopyWebpackPlugin = require('copy-webpack-plugin');
config.output = {
    ...config.output,
    publicPath: 'auto'
};
config.plugins.push(
    new CopyWebpackPlugin(
        {
            patterns: [
                {
                    from: "../../node_modules/@sqlite.org/sqlite-wasm/sqlite-wasm/jswasm/sqlite3.js",
                    to: "sqlite3.js"
                },
                {
                    from: "../../node_modules/@sqlite.org/sqlite-wasm/sqlite-wasm/jswasm/sqlite3.wasm",
                    to: "sqlite3.wasm"
                },
                {
                    from: "../../node_modules/@sqlite.org/sqlite-wasm/sqlite-wasm/jswasm/sqlite3-opfs-async-proxy.js",
                    to: "sqlite3-opfs-async-proxy.js"
                },
            ]
        }
    )
);