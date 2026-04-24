/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as path from 'path';
import {
	workspace,
	ExtensionContext,
	window,
	commands,
	env,
	Range,
	ViewColumn,
	WorkspaceEdit,
	FileDecorationProvider,
	FileDecoration,
	ThemeColor,
	EventEmitter,
	Uri
} from 'vscode';

import {
	LanguageClient,
	LanguageClientOptions,
	ServerOptions,
	TransportKind
} from 'vscode-languageclient/node';
import { getLocale, getMessages, LocaleCode, Messages } from './i18n';

let client: LanguageClient;
let clientReady: Promise<void> | undefined;
const TABLEGEN_FILE_REGEX = /(tablege|es).*\.(properties|prop)$/i;
const TABLEGEN_FILE_GLOB = '**/{tablege,es}*.{properties,prop}';

type FieldType = 'text' | 'boolean' | 'select' | 'textarea';
interface FieldDefinition {
	key: string;
	label: string;
	type: FieldType;
	options?: string[];
	description?: string;
}
interface FieldSection {
	id: string;
	title: string;
	fields: FieldDefinition[];
}
const CONFIG_SECTIONS: FieldSection[] = [
	{
		id: 'database',
		title: '数据库配置',
		fields: [
			{ key: 'projectname', label: '项目名', type: 'text', description: '生成项目名称，用于生成代码中的展示。' },
			{ key: 'className', label: 'JDBC Driver', type: 'text', description: 'JDBC 驱动类，例如 com.mysql.cj.jdbc.Driver。' },
			{ key: 'dburl', label: '数据库 URL', type: 'text', description: '数据库 JDBC URL。' },
			{ key: 'username', label: '用户名', type: 'text', description: '数据库账号。' },
			{ key: 'password', label: '密码', type: 'text', description: '数据库密码。' },
			{ key: 'schema', label: 'Schema', type: 'text', description: '数据库 schema / catalog，用于过滤表。' },
			{ key: 'table_owner', label: 'Table Owner', type: 'text', description: '表所属用户（Oracle/DB2 等）。' },
			{ key: 'table_list', label: '表清单 (table_list)', type: 'textarea', description: '参与生成的表，可为逗号分隔或正则。' },
			{ key: 'exclude_table_list', label: '排除表清单', type: 'textarea', description: '排除的表，优先级高于 table_list。' }
		]
	},
	{
		id: 'codegen',
		title: '代码生成配置',
		fields: [
			{ key: 'package_name', label: '包名', type: 'text', description: '生成代码的根包名。' },
			{ key: 'src_dir', label: '源码目录', type: 'text', description: '主代码输出目录。' },
			{ key: 'test_dir', label: '测试目录', type: 'text', description: '测试代码输出目录。' },
			{ key: 'resource_dir', label: '资源目录', type: 'text', description: '资源文件输出目录。' },
			{ key: 'service_dir', label: 'Service目录', type: 'text', description: 'Service 代码输出目录。' },
			{ key: 'controller_dir', label: 'Controller目录', type: 'text', description: 'Controller 代码输出目录。' },
			{ key: 'dto_dir', label: 'DTO目录', type: 'text', description: 'DTO 代码输出目录。' },
			{ key: 'cmd_dir', label: 'CMD目录', type: 'text', description: 'Command/DTO 代码输出目录。' },
			{ key: 'enable_merge', label: '启用智能合并 (enable_merge)', type: 'boolean', description: '启用后会保留用户在 Dto、SearchCmd、Converter 文件中的修改。' },
			{ key: 'generate_all', label: '全量生成', type: 'boolean', description: '是否全量生成，no 时进入增量模式。' },
			{ key: 'over_write_all', label: '覆盖已有文件', type: 'boolean', description: '是否覆盖现有文件。' },
			{ key: 'use_annotation', label: '使用注解', type: 'boolean', description: '是否启用 Jakarta Validation/Swagger 注解。' },
			{ key: 'r2dbc', label: '生成 R2DBC', type: 'boolean', description: '是否生成 R2DBC 代码。' }
		]
	},
	{
		id: 'advanced',
		title: '高级配置',
		fields: [
			{ key: 'api_doc_provider', label: 'API 文档提供者', type: 'select', options: ['springdoc', 'swagger'], description: 'springdoc 模式下不会生成 Swagger 注解；若未配置则兼容老逻辑。' },
			{ key: 'use_swagger', label: '生成 Swagger 注解', type: 'boolean', description: '仅在 api_doc_provider=swagger 时生效。' },
			{ key: 'swagger_version', label: 'Swagger 版本', type: 'select', options: ['2', '3'], description: 'Swagger/OpenAPI 版本。' },
			{ key: 'view_list', label: '视图定义 (view_list)', type: 'textarea', description: '支持 view: select ...; 或 view: @relative/path.sql;' },
			{ key: 'ignore_fields', label: '忽略字段', type: 'textarea', description: '格式 table:field1,field2 或 field1,field2。' },
			{ key: 'encrypted_fields', label: '加密字段', type: 'textarea', description: '格式 table1:field1,field2;table2:field3,field4;' }
		]
	}
];

export function activate(context: ExtensionContext) {
	// The server is implemented in node
	let serverModule = context.asAbsolutePath(
		path.join('server', 'out', 'server.js')
	);
	// The debug options for the server
	// --inspect=6009: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging
	let debugOptions = { execArgv: ['--nolazy', '--inspect=6009'] };

	// If the extension is launched in debug mode then the debug server options are used
	// Otherwise the run options are used
	let serverOptions: ServerOptions = {
		run: { module: serverModule, transport: TransportKind.ipc },
		debug: {
			module: serverModule,
			transport: TransportKind.ipc,
			options: debugOptions
		}
	};

	// Options to control the language client
	let clientOptions: LanguageClientOptions = {
		// Monitor properties 文件，由服务端按文件名再次过滤
		documentSelector: [
			{ scheme: 'file', language: 'tablegear' },
			{ scheme: 'file', language: 'properties' },
			{ scheme: 'untitled', language: 'properties' }
		],
		synchronize: {
			fileEvents: workspace.createFileSystemWatcher(TABLEGEN_FILE_GLOB)
		}
	};

	// Create the language client and start the client.
	client = new LanguageClient(
		'tablegearLanguageServer',
		'TableGear Language Server',
		serverOptions,
		clientOptions
	);

	// Start the client. This will also launch the server
	clientReady = client.start();

	const decorationProvider = new TablegearDecorationProvider();
	context.subscriptions.push(window.registerFileDecorationProvider(decorationProvider));

	const decorationWatcher = workspace.createFileSystemWatcher(TABLEGEN_FILE_GLOB);
	context.subscriptions.push(decorationWatcher);
	decorationWatcher.onDidCreate((uri) => decorationProvider.refresh(uri));
	decorationWatcher.onDidChange((uri) => decorationProvider.refresh(uri));
	decorationWatcher.onDidDelete((uri) => decorationProvider.refresh(uri));

	const openConfigEditorCommand = commands.registerCommand('tablegear.openConfigEditor', async () => {
		await openConfigEditor();
	});
	context.subscriptions.push(openConfigEditorCommand);

	return clientReady;
}

export function deactivate(): Thenable<void> | undefined {
	if (!client) {
		return undefined;
	}
	return client.stop();
}

class TablegearDecorationProvider implements FileDecorationProvider {
	private readonly _emitter = new EventEmitter<Uri | Uri[] | undefined>();
	readonly onDidChangeFileDecorations = this._emitter.event;

	provideFileDecoration(uri: Uri): FileDecoration | undefined {
		if (uri.scheme !== 'file') {
			return;
		}
		const fileName = path.basename(uri.fsPath);
		if (!TABLEGEN_FILE_REGEX.test(fileName)) {
			return;
		}

		return {
			badge: 'TG',
			tooltip: getMessages(getLocale(env.language)).fileTooltip,
			color: new ThemeColor('charts.yellow')
		};
	}

	refresh(uri?: Uri): void {
		this._emitter.fire(uri);
	}
}

async function openConfigEditor(): Promise<void> {
	const locale = getLocale(env.language);
	const messages = getMessages(locale);
	const editor = window.activeTextEditor;
	if (!editor) {
		window.showWarningMessage(messages.notTablegenOpen);
		return;
	}
	const doc = editor.document;
	const fileName = path.basename(doc.uri.fsPath);
	if (!TABLEGEN_FILE_REGEX.test(fileName)) {
		window.showWarningMessage(messages.notTablegenFile);
		return;
	}

	const panel = window.createWebviewPanel(
		'tablegearConfigEditor',
		`${messages.panelTitle} - ${fileName}`,
		ViewColumn.Beside,
		{ enableScripts: true, retainContextWhenHidden: true }
	);
	panel.webview.html = buildConfigEditorHtml(fileName, parseProperties(doc.getText()), locale, messages);
	panel.webview.onDidReceiveMessage(async (message: any) => {
		if (message?.type !== 'save' || !message.values || typeof message.values !== 'object') {
			return;
		}
		const latestDoc = await workspace.openTextDocument(doc.uri);
		const values = normalizeFormValues(message.values as Record<string, any>);
		const merged = mergeWithLatest(latestDoc.getText(), values);
		const nextText = serializeProperties(latestDoc.getText(), merged);

		const edit = new WorkspaceEdit();
		const fullRange = latestDoc.validateRange(
			new Range(latestDoc.positionAt(0), latestDoc.positionAt(latestDoc.getText().length))
		);
		edit.replace(latestDoc.uri, fullRange, nextText);
		await workspace.applyEdit(edit);
		await latestDoc.save();
		panel.dispose();
		window.showInformationMessage(messages.savedMessage);
	});
}

function parseProperties(text: string): Map<string, string> {
	const map = new Map<string, string>();
	text.split(/\r?\n/).forEach((line) => {
		const trimmed = line.trim();
		if (!trimmed || trimmed.startsWith('#')) {
			return;
		}
		const idx = line.indexOf('=');
		if (idx <= 0) {
			return;
		}
		const key = line.substring(0, idx).trim();
		const value = line.substring(idx + 1).trim();
		if (key) {
			map.set(key, value);
		}
	});
	return map;
}

function serializeProperties(originalText: string, values: Map<string, string>): string {
	const lines = originalText.split(/\r?\n/);
	const updated = lines.map((line) => {
		const idx = line.indexOf('=');
		if (idx <= 0) {
			return line;
		}
		const key = line.substring(0, idx).trim();
		if (!values.has(key)) {
			return '';
		}
		const value = values.get(key) ?? '';
		values.delete(key);
		return `${key}=${value}`;
	}).filter((line) => line.length > 0);

	values.forEach((value, key) => {
		updated.push(`${key}=${value}`);
	});
	return `${updated.join('\n')}\n`;
}

function normalizeFormValues(formValues: Record<string, any>): Map<string, string> {
	const map = new Map<string, string>();
	const allKeys = CONFIG_SECTIONS.flatMap((section) => section.fields.map((f) => f.key));
	allKeys.forEach((key) => {
		const raw = formValues[key];
		if (typeof raw === 'boolean') {
			if (raw) {
				map.set(key, 'yes');
			}
			return;
		}
		if (typeof raw === 'string' && raw.trim().length > 0) {
			map.set(key, raw.trim());
		}
	});
	if ((map.get('api_doc_provider') ?? '') === 'springdoc') {
		map.set('use_swagger', 'no');
	}
	return map;
}

function mergeWithLatest(originalText: string, updatedValues: Map<string, string>): Map<string, string> {
	const latest = parseProperties(originalText);
	CONFIG_SECTIONS.flatMap((section) => section.fields.map((f) => f.key)).forEach((key) => {
		if (updatedValues.has(key)) {
			latest.set(key, updatedValues.get(key)!);
		} else {
			latest.delete(key);
		}
	});
	return latest;
}

function buildConfigEditorHtml(fileName: string, values: Map<string, string>, locale: LocaleCode, messages: Messages): string {
	const sections = localizeSections(messages);
	const sectionsHtml = sections.map((section) => {
		const fieldsHtml = section.fields.map((field) => renderField(field, values.get(field.key) ?? '')).join('\n');
		return `<section class="section" id="section-${escapeHtml(section.id)}">
      <details open>
        <summary>${escapeHtml(section.title)}</summary>
        <div class="section-body">${fieldsHtml}</div>
      </details>
    </section>`;
	}).join('\n');
	const navHtml = sections.map((section) =>
		`<a href="#section-${escapeHtml(section.id)}">${escapeHtml(section.title)}</a>`
	).join('\n');
	const defaultsJson = JSON.stringify(buildDefaultValues());
	const valuesJson = JSON.stringify(Object.fromEntries(values.entries()));

	return `<!DOCTYPE html>
<html lang="${locale === 'zh' ? 'zh-CN' : 'en'}">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>TableGear Config Editor</title>
  <style>
    body { font-family: var(--vscode-font-family); color: var(--vscode-foreground); margin: 0; }
    .layout { display: grid; grid-template-columns: 210px 1fr; min-height: 100vh; }
    .sidebar { border-right: 1px solid var(--vscode-editorWidget-border); padding: 14px 10px; position: sticky; top: 0; height: 100vh; box-sizing: border-box; }
    .sidebar h3 { margin: 0 0 10px 0; font-size: 12px; opacity: 0.9; text-transform: uppercase; letter-spacing: 0.5px; }
    .sidebar a { display: block; color: var(--vscode-textLink-foreground); text-decoration: none; margin-bottom: 8px; font-size: 13px; }
    .content { padding: 16px; }
    h1 { font-size: 18px; margin: 0 0 12px 0; }
    .section { border: 1px solid var(--vscode-editorWidget-border); border-radius: 6px; padding: 12px; margin-bottom: 12px; }
    .section details > summary { cursor: pointer; font-size: 14px; font-weight: 600; margin-bottom: 8px; }
    .section-body { margin-top: 8px; }
    .field { display: grid; grid-template-columns: 270px 1fr; gap: 8px; align-items: center; margin-bottom: 8px; }
    label { font-size: 12px; }
    .field-meta { display: flex; flex-direction: column; gap: 4px; }
    .field-meta small { color: var(--vscode-descriptionForeground); font-size: 11px; line-height: 1.35; }
    input, select, textarea { width: 100%; box-sizing: border-box; color: var(--vscode-input-foreground); background: var(--vscode-input-background); border: 1px solid var(--vscode-input-border); padding: 4px 6px; border-radius: 4px; }
    textarea { min-height: 52px; resize: vertical; }
    .actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 16px; }
    button { border: 1px solid var(--vscode-button-border, transparent); color: var(--vscode-button-foreground); background: var(--vscode-button-background); padding: 6px 12px; border-radius: 4px; cursor: pointer; }
    .subtle { color: var(--vscode-descriptionForeground); font-size: 12px; margin-bottom: 10px; }
  </style>
</head>
<body>
  <div class="layout">
    <aside class="sidebar">
      <h3>${escapeHtml(messages.sectionGroups)}</h3>
      ${navHtml}
    </aside>
    <main class="content">
      <h1>${escapeHtml(messages.panelTitle)}</h1>
      <div class="subtle">${escapeHtml(messages.currentFile)}: ${escapeHtml(fileName)}</div>
      ${sectionsHtml}
      <div class="actions">
        <button id="reloadBtn" type="button">${escapeHtml(messages.reloadButton)}</button>
        <button id="resetBtn" type="button">${escapeHtml(messages.resetButton)}</button>
        <label style="display:flex;align-items:center;gap:6px;margin-right:auto;">
          <input id="configuredOnly" type="checkbox" />
          <span>${escapeHtml(messages.showConfiguredOnly)}</span>
        </label>
        <button id="saveBtn">${escapeHtml(messages.saveButton)}</button>
      </div>
    </main>
  </div>
  <script>
    const vscode = acquireVsCodeApi();
    const initialValues = ${defaultsJson};
    const loadedValues = ${valuesJson};
    function collectFormValues() {
      const values = {};
      document.querySelectorAll('[data-key]').forEach((el) => {
        const key = el.getAttribute('data-key');
        if (!key) return;
        if (el.type === 'checkbox') {
          values[key] = !!el.checked;
        } else {
          values[key] = el.value || '';
        }
      });
      if (values.api_doc_provider === 'springdoc') {
        values.use_swagger = false;
      }
      return values;
    }
    function applyValues(values) {
      document.querySelectorAll('[data-key]').forEach((el) => {
        const key = el.getAttribute('data-key');
        if (!key) return;
        const v = values[key];
        if (el.type === 'checkbox') {
          el.checked = (String(v || '').toLowerCase() === 'yes' || String(v || '').toLowerCase() === 'true');
        } else {
          el.value = v || '';
        }
      });
      syncApiDocState();
      syncConfiguredOnly();
    }
    function syncConfiguredOnly() {
      const toggle = document.getElementById('configuredOnly');
      if (!toggle) return;
      const configuredOnly = !!toggle.checked;
      document.querySelectorAll('[data-key]').forEach((el) => {
        const row = el.closest('.field');
        if (!row) return;
        if (!configuredOnly) {
          row.style.display = '';
          return;
        }
        const val = el.type === 'checkbox' ? (el.checked ? 'yes' : '') : (el.value || '').trim();
        row.style.display = val ? '' : 'none';
      });
    }
    function syncApiDocState() {
      const providerEl = document.querySelector('[data-key="api_doc_provider"]');
      const useSwaggerEl = document.querySelector('[data-key="use_swagger"]');
      const swaggerVersionEl = document.querySelector('[data-key="swagger_version"]');
      if (!providerEl || !useSwaggerEl || !swaggerVersionEl) return;
      const isSwagger = providerEl.value === 'swagger';
      useSwaggerEl.disabled = !isSwagger;
      swaggerVersionEl.disabled = !isSwagger;
      if (!isSwagger) useSwaggerEl.checked = false;
    }
    document.getElementById('saveBtn').addEventListener('click', () => {
      vscode.postMessage({ type: 'save', values: collectFormValues() });
    });
    document.getElementById('reloadBtn').addEventListener('click', () => applyValues(loadedValues));
    document.getElementById('resetBtn').addEventListener('click', () => applyValues(initialValues));
    document.getElementById('configuredOnly').addEventListener('change', syncConfiguredOnly);
    const providerEl = document.querySelector('[data-key="api_doc_provider"]');
    if (providerEl) providerEl.addEventListener('change', syncApiDocState);
    applyValues(loadedValues);
  </script>
</body>
</html>`;
}

function renderField(field: FieldDefinition, value: string): string {
	const key = escapeHtml(field.key);
	const label = escapeHtml(`${field.label} (${field.key})`);
	const description = field.description ? escapeHtml(field.description) : '';
	const labelHtml = `<div class="field-meta"><label title="${description}">${label}</label>${description ? `<small>${description}</small>` : ''}</div>`;
	if (field.type === 'boolean') {
		const checked = value.toLowerCase() === 'yes' || value.toLowerCase() === 'true' ? 'checked' : '';
		return `<div class="field">${labelHtml}<input type="checkbox" data-key="${key}" ${checked} title="${description}" /></div>`;
	}
	if (field.type === 'select') {
		const options = (field.options ?? []).map((opt) => {
			const selected = opt === value ? 'selected' : '';
			return `<option value="${escapeHtml(opt)}" ${selected}>${escapeHtml(opt)}</option>`;
		}).join('');
		return `<div class="field">${labelHtml}<select data-key="${key}" title="${description}">${options}</select></div>`;
	}
	if (field.type === 'textarea') {
		return `<div class="field">${labelHtml}<textarea data-key="${key}" title="${description}">${escapeHtml(value)}</textarea></div>`;
	}
	return `<div class="field">${labelHtml}<input type="text" data-key="${key}" value="${escapeHtml(value)}" title="${description}" /></div>`;
}

function escapeHtml(text: string): string {
	return text
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;')
		.replace(/"/g, '&quot;')
		.replace(/'/g, '&#39;');
}

function buildDefaultValues(): Record<string, string> {
	return {
		api_doc_provider: 'springdoc',
		swagger_version: '2',
		enable_merge: 'no',
		use_swagger: 'no'
	};
}

function localizeSections(messages: Messages): FieldSection[] {
	return CONFIG_SECTIONS.map((section) => ({
		...section,
		title: messages.sections[section.id] ?? section.title,
		fields: section.fields.map((field) => ({
			...field,
			label: messages.fields[field.key]?.label ?? field.label,
			description: messages.fields[field.key]?.description ?? field.description
		}))
	}));
}
