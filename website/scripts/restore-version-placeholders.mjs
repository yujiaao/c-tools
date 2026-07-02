#!/usr/bin/env node
/**
 * 构建后将文档中的 c-tools 版本还原为 {{CTOOLS_VERSION}} 占位符，便于 git 提交。
 * 与 sync-versions.mjs 成对使用（prebuild 展开，postbuild 还原）。
 */
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const DOCS_DIR = path.resolve(__dirname, '../docs');
const POM = path.resolve(__dirname, '../../pom.xml');
const PLACEHOLDER = '{{CTOOLS_VERSION}}';
const CTOOLS_ARTIFACTS = ['c-tools-bom', 'c-dbtools', 'tablegen-maven-plugin'];

function readProjectVersion() {
  const xml = fs.readFileSync(POM, 'utf8');
  const match = xml.match(
    /<artifactId>c-tools-parent<\/artifactId>[\s\S]*?<version>([^<]+)<\/version>/,
  );
  if (!match) throw new Error('无法解析 pom 版本');
  return match[1].trim();
}

function collectMarkdownFiles(dir) {
  const out = [];
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) out.push(...collectMarkdownFiles(full));
    else if (entry.name.endsWith('.md')) out.push(full);
  }
  return out;
}

function restorePlaceholders(content, version) {
  let result = content;
  result = result.replace(
    new RegExp(`com\\.github\\.yujiaao:tablegen-maven-plugin:${version.replace(/\./g, '\\.')}:gen`, 'g'),
    `com.github.yujiaao:tablegen-maven-plugin:${PLACEHOLDER}:gen`,
  );
  for (const artifactId of CTOOLS_ARTIFACTS) {
    result = result.replace(
      new RegExp(
        `(<artifactId>${artifactId}</artifactId>\\s*<version>)${version.replace(/\./g, '\\.')}(</version>)`,
        'g',
      ),
      `$1${PLACEHOLDER}$2`,
    );
  }
  result = result.replace(
    new RegExp(
      `(当前 \\*\\*|当前为 \\*\\*|currently \\*\\*|Current: \\*\\*)${version.replace(/\./g, '\\.')}(\\*\\*)`,
      'gi',
    ),
    `$1${PLACEHOLDER}$2`,
  );
  return result;
}

const version = readProjectVersion();
let restored = 0;

for (const file of collectMarkdownFiles(DOCS_DIR)) {
  const before = fs.readFileSync(file, 'utf8');
  const after = restorePlaceholders(before, version);
  if (before !== after) {
    fs.writeFileSync(file, after, 'utf8');
    restored++;
  }
}

console.log(restored ? `已还原 ${restored} 个文件中的版本占位符` : '无需还原版本占位符');
