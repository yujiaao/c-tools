const path = require('path');
const { readdirSync, statSync } = require('fs');

const translate = require('./translate.js');

const dirDocs = './docs';
const dir = path.join(__dirname, dirDocs);

const delay = ms => new Promise(resolve => setTimeout(resolve, ms));

async function travel(dir, callback) {
  const dirs = readdirSync(dir);
  for (let i = 0; i < dirs.length; i++) {
    const file = dirs[i];
    const pathname = path.join(dir, file);
    if (statSync(pathname).isDirectory()) {
      await travel(pathname, callback);
    } else {
      await callback(pathname);
    }
  }
}

travel(dir, async function(pathname) {
  if (path.extname(pathname) === '.md' && pathname.indexOf('en-US') < 0) {
    /* 随机 sleep 防止被 google 反爬虫 */
    await delay(parseInt(Math.random() * 10000 + 10000));

    await translate(pathname);
  }
});
