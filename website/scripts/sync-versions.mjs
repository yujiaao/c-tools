#!/usr/bin/env node
/**
 * 从 c-tools 根 pom.xml 读取项目版本，同步文档中的 Maven 坐标。
 *
 * 支持：
 *   - {{CTOOLS_VERSION}} 占位符
 *   - 已写死的 tablegen 插件坐标 / BOM / c-dbtools 版本（pom 升级后自动替换）
 *
 * 用法:
 *   node scripts/sync-versions.mjs          # 写入
 *   node scripts/sync-versions.mjs --check  # 仅检查，有漂移则 exit 1
 */
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, '../..');
const DOCS_DIR = path.resolve(__dirname, '../docs');
const POM = path.join(ROOT, 'pom.xml');
const PLACEHOLDER = '{{CTOOLS_VERSION}}';
const VERSION_META = path.resolve(__dirname, '../.ctools-version.json');
const CTOOLS_ARTIFACTS = ['c-tools-bom', 'c-dbtools', 'tablegen-maven-plugin'];

const checkOnly = process.argv.includes('--check');

function readProjectVersion() {
  const xml = fs.readFileSync(POM, 'utf8');
  const match = xml.match(
    /<artifactId>c-tools-parent<\/artifactId>[\s\S]*?<version>([^<]+)<\/version>/,
  );
  if (!match) {
    throw new Error(`无法在 ${POM} 中解析 c-tools-parent 版本`);
  }
  return match[1].trim();
}

function collectMarkdownFiles(dir) {
  const out = [];
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      out.push(...collectMarkdownFiles(full));
    } else if (entry.name.endsWith('.md')) {
      out.push(full);
    }
  }
  return out;
}

/** 将文档中的 c-tools 相关版本统一为 pom 版本 */
function applyVersion(content, version) {
  let result = content.split(PLACEHOLDER).join(version);

  result = result.replace(
    /com\.github\.yujiaao:tablegen-maven-plugin:[\d.]+:gen/g,
    `com.github.yujiaao:tablegen-maven-plugin:${version}:gen`,
  );

  for (const artifactId of CTOOLS_ARTIFACTS) {
    result = result.replace(
      new RegExp(
        `(<artifactId>${artifactId}</artifactId>\\s*<version>)[\\d.]+(</version>)`,
        'g',
      ),
      `$1${version}$2`,
    );
  }

  result = result.replace(
    /(当前 \*\*|当前为 \*\*|currently \*\*|Current: \*\*)[\d.]+(\*\*)/gi,
    `$1${version}$2`,
  );

  return result;
}

function validateForGit(content, version) {
  if (content.includes(PLACEHOLDER)) {
    return null;
  }
  if (applyVersion(content, version) === content) {
    return null;
  }
  return 'stale';
}

function syncFile(file, version) {
  const before = fs.readFileSync(file, 'utf8');
  if (checkOnly) {
    return validateForGit(before, version) ? true : false;
  }
  const after = applyVersion(before, version);
  if (before === after) {
    return false;
  }
  fs.writeFileSync(file, after, 'utf8');
  return true;
}

function writeVersionMeta(version) {
  const meta = {
    version,
    source: path.relative(ROOT, POM),
    syncedAt: new Date().toISOString(),
  };
  if (!checkOnly) {
    fs.writeFileSync(VERSION_META, `${JSON.stringify(meta, null, 2)}\n`, 'utf8');
  }
}

const version = readProjectVersion();
const files = collectMarkdownFiles(DOCS_DIR);
const changed = [];

for (const file of files) {
  if (syncFile(file, version)) {
    changed.push(path.relative(path.resolve(__dirname, '..'), file));
  }
}

writeVersionMeta(version);

if (changed.length === 0) {
  console.log(checkOnly ? `文档版本检查通过 (${version})` : `版本 ${version} 已与文档一致`);
  process.exit(0);
}

const action = checkOnly ? '版本漂移' : '已更新';
console.log(`${action} ${changed.length} 个文件 -> ${version}:`);
changed.forEach((f) => console.log(`  - ${f}`));

if (checkOnly) {
  console.error('\n请运行: npm run sync-versions');
  process.exit(1);
}
