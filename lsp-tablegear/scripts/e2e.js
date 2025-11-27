const { execSync } = require('child_process');
const path = require('path');

const rootDir = path.resolve(__dirname, '..');

function run(command) {
	execSync(command, {
		cwd: rootDir,
		env: process.env,
		stdio: 'inherit',
		shell: true
	});
}

console.log('[e2e] Compiling sources...');
run('npm run compile');

console.log('[e2e] Running VS Code integration tests...');
run('node ./client/out/test/runTest.js');

