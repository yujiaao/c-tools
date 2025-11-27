import * as assert from 'assert';
import * as vscode from 'vscode';
import * as path from 'path';
import { suite, suiteSetup, test } from 'mocha';

const EXTENSION_ID = 'vscode-tablegear.lsp-tablegear';

suite('Tablegear language features', () => {
	suiteSetup(async () => {
		const extension = vscode.extensions.getExtension(EXTENSION_ID);
		assert.ok(extension, `未找到扩展 ${EXTENSION_ID}`);
		await extension.activate();
		await delay(1500);
	});

	test('能够为 tablegen 配置提供诊断', async () => {
		const doc = await openFixtureDocument('tablegen-demo.properties');
		const selectorMatch = vscode.languages.match(
			[
				{ scheme: 'file', language: 'tablegear' },
				{ scheme: 'file', language: 'properties' },
				{ scheme: 'untitled', language: 'properties' }
			],
			doc
		);
		assert.ok(selectorMatch > 0, 'DocumentSelector 未匹配到测试文件');
		await pokeDocument(doc);

		const diagnostics = await waitForDiagnostics(
			doc.uri,
			(entries) => entries.length >= 2 && entries.some((d) => d.message.includes('未识别的配置项'))
		);

		assert.ok(
			diagnostics.some((d) => d.message.includes('未识别的配置项 unknown_key')),
			'应检测到未知配置项的诊断'
		);

		assert.ok(
			diagnostics.some((d) => d.message.includes('read_write_separate')),
			'应检测到布尔值格式错误的诊断'
		);
	});
});

async function openFixtureDocument(name: string): Promise<vscode.TextDocument> {
	const workspaceFolder = vscode.workspace.workspaceFolders?.[0];
	assert.ok(workspaceFolder, '测试需要打开 workspace folder');

	const filePath = path.join(workspaceFolder.uri.fsPath, name);
	const document = await vscode.workspace.openTextDocument(vscode.Uri.file(filePath));
	await vscode.window.showTextDocument(document, { preview: true });
	return document;
}

async function waitForDiagnostics(
	uri: vscode.Uri,
	predicate: (diagnostics: vscode.Diagnostic[]) => boolean,
	timeout = 15000
): Promise<vscode.Diagnostic[]> {
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

function delay(ms: number): Promise<void> {
	return new Promise((resolve) => setTimeout(resolve, ms));
}

async function pokeDocument(document: vscode.TextDocument): Promise<void> {
	const position = new vscode.Position(0, 0);
	const range = new vscode.Range(position, position.translate(0, 1));

	const insertEdit = new vscode.WorkspaceEdit();
	insertEdit.insert(document.uri, position, ' ');
	await vscode.workspace.applyEdit(insertEdit);

	const deleteEdit = new vscode.WorkspaceEdit();
	deleteEdit.delete(document.uri, range);
	await vscode.workspace.applyEdit(deleteEdit);
}

