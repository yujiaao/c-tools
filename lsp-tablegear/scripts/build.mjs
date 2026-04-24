import * as esbuild from 'esbuild';

const watch = process.argv.includes('--watch');

const shared = {
  bundle: true,
  platform: 'node',
  format: 'cjs',
  target: 'node16',
  sourcemap: true,
  external: ['vscode'],
  logLevel: 'info'
};

const buildOptions = [
  {
    ...shared,
    entryPoints: ['client/src/extension.ts'],
    outfile: 'client/out/extension.js'
  },
  {
    ...shared,
    entryPoints: ['server/src/server.ts'],
    outfile: 'server/out/server.js'
  }
];

const run = async () => {
  if (!watch) {
    await Promise.all(buildOptions.map((options) => esbuild.build(options)));
    return;
  }

  const contexts = await Promise.all(buildOptions.map((options) => esbuild.context(options)));
  await Promise.all(contexts.map((ctx) => ctx.watch()));
  console.log('Watching bundle builds...');
};

run()
  .catch((err) => {
    console.error(err);
    process.exit(1);
  });

