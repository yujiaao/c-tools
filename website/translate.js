const fetch = require('node-fetch');
const { readFileSync, appendFileSync, unlinkSync, existsSync } = require('fs');

const LIMIT_FILE_CONTENT = 3000;

async function trasnlate(pathname) {
  console.log('translate:', pathname);

  const fileContent = readFileSync(pathname, { encoding: 'utf-8' });

  const enFilePath =
    pathname.slice(0, pathname.indexOf('.md')) +
    '.en-US' +
    pathname.slice(pathname.indexOf('.md'));

  async function fetchTranslate(content, delEnglishFile) {
    const url = `https://translate.googleapis.com/translate_a/single?oe=UTF-8&i=UTF-8&client=gtx&dt=t&hl=zh-CN&sl=zh-CN&tl=en&text=${encodeURIComponent(
      content,
    )}`;

    try {
      const res = await fetch(url);

      const data = await res.json();

      if (existsSync(enFilePath) && delEnglishFile) {
        unlinkSync(enFilePath);
      }

      if (data && data[0]) {
        data[0].forEach(item => {
          if (item && item[0]) {
            let str = item[0] || '';
            // Deal with '- xxxxxxx' for md.translation will lose spaces.The exclusion of '---'.
            if (str.charAt(0) === '-' && str.charAt(1) !== '-') {
              str = '- ' + str.slice(1, str.length);
            }
            // Deal with english path.
            str = str
              .replace(/\]\(\//g, '](/en-US/')
              .replace(/href="\//g, 'href="/en-US/');
            appendFileSync(enFilePath, str);
          }
        });
      }

      console.log(pathname, ' success.');
    } catch (e) {
      console.log(pathname, ' failed.');
      console.log(e);
    }
  }

  for (let i = 0; i < Math.ceil(fileContent.length / LIMIT_FILE_CONTENT); i++) {
    await fetchTranslate(
      fileContent.slice(LIMIT_FILE_CONTENT * i, LIMIT_FILE_CONTENT * (i + 1)),
      i === 0,
    );
  }
}

module.exports = trasnlate;
