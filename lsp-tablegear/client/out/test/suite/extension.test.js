"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const assert = __importStar(require("assert"));
const vscode = __importStar(require("vscode"));
const path = __importStar(require("path"));
const mocha_1 = require("mocha");
const EXTENSION_ID = 'vscode-tablegear.lsp-tablegear';
(0, mocha_1.suite)('Tablegear language features', () => {
    (0, mocha_1.suiteSetup)(async () => {
        const extension = vscode.extensions.getExtension(EXTENSION_ID);
        assert.ok(extension, `未找到扩展 ${EXTENSION_ID}`);
        await extension.activate();
        await delay(1500);
    });
    (0, mocha_1.test)('能够为 tablegen 配置提供诊断', async () => {
        const doc = await openFixtureDocument('tablegen-demo.properties');
        const selectorMatch = vscode.languages.match([
            { scheme: 'file', language: 'tablegear' },
            { scheme: 'file', language: 'properties' },
            { scheme: 'untitled', language: 'properties' }
        ], doc);
        assert.ok(selectorMatch > 0, 'DocumentSelector 未匹配到测试文件');
        await pokeDocument(doc);
        const diagnostics = await waitForDiagnostics(doc.uri, (entries) => entries.length >= 2 && entries.some((d) => d.message.includes('未识别的配置项')));
        assert.ok(diagnostics.some((d) => d.message.includes('未识别的配置项 unknown_key')), '应检测到未知配置项的诊断');
        assert.ok(diagnostics.some((d) => d.message.includes('read_write_separate')), '应检测到布尔值格式错误的诊断');
    });
    (0, mocha_1.test)('api_doc_provider=springdoc 时提示 swagger 配置会被忽略', async () => {
        const doc = await openFixtureDocument('tablegen-apidoc-link.properties');
        await pokeDocument(doc);
        const diagnostics = await waitForDiagnostics(doc.uri, (entries) => entries.some((d) => d.message.includes('忽略 use_swagger')) &&
            entries.some((d) => d.message.includes('忽略 swagger_version')));
        assert.ok(diagnostics.some((d) => d.message.includes('忽略 use_swagger')), '应提示 springdoc 模式下 use_swagger 会被忽略');
        assert.ok(diagnostics.some((d) => d.message.includes('忽略 swagger_version')), '应提示 springdoc 模式下 swagger_version 会被忽略');
    });
});
async function openFixtureDocument(name) {
    const workspaceFolder = vscode.workspace.workspaceFolders?.[0];
    assert.ok(workspaceFolder, '测试需要打开 workspace folder');
    const filePath = path.join(workspaceFolder.uri.fsPath, name);
    const document = await vscode.workspace.openTextDocument(vscode.Uri.file(filePath));
    await vscode.window.showTextDocument(document, { preview: true });
    return document;
}
async function waitForDiagnostics(uri, predicate, timeout = 15000) {
    const start = Date.now();
    return new Promise((resolve, reject) => {
        const check = () => {
            const diags = vscode.languages.getDiagnostics(uri);
            if (predicate(diags)) {
                resolve(diags);
                return;
            }
            if (Date.now() - start > timeout) {
                reject(new Error('等待诊断超时'));
                return;
            }
            setTimeout(check, 250);
        };
        check();
    });
}
function delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}
async function pokeDocument(document) {
    const position = new vscode.Position(0, 0);
    const range = new vscode.Range(position, position.translate(0, 1));
    const insertEdit = new vscode.WorkspaceEdit();
    insertEdit.insert(document.uri, position, ' ');
    await vscode.workspace.applyEdit(insertEdit);
    const deleteEdit = new vscode.WorkspaceEdit();
    deleteEdit.delete(document.uri, range);
    await vscode.workspace.applyEdit(deleteEdit);
}
//# sourceMappingURL=extension.test.js.map