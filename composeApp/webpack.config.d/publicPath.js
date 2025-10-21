// Configure webpack public path for GitHub Pages deployment
config.output = config.output || {};
config.output.publicPath = process.env.WEBPACK_PUBLIC_PATH || '/';

console.log('Webpack public path set to:', config.output.publicPath);
