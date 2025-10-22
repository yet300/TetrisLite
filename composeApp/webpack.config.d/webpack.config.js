const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');

// Определяем абсолютный путь к корневой директории сборки JS (`build/js`)
// __dirname указывает на .../build/js/packages/TetrisLite-composeApp/
// Поднимаемся на 2 уровня вверх, чтобы попасть в .../build/js/
const buildJsDir = path.resolve(__dirname, '../../');

// Устанавливаем publicPath для корректной работы на GitHub Pages
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

config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: path.resolve(buildJsDir, 'node_modules/@cashapp/sqldelight-sqljs-worker/sqljs.worker.js'),
                to: '.'
            },
            {
                from: path.resolve(buildJsDir, 'node_modules/sql.js/dist/sql-wasm.wasm'),
                to: '.'
            },
            {
                from: path.resolve(buildJsDir, 'node_modules/sql.js/dist/sql.js'),
                to: '.'
            }
        ]
    })
);