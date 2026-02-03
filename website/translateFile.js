const path = require('path');
const translate = require('./translate.js');

const args = process.argv.slice(2);

if (args && args[0]) {
  const pathname = path.join(__dirname, args[0]);
  translate(pathname);
} else {
  console.log('请输入需要翻译的文件');
}
