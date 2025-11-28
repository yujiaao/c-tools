/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as path from 'path';
import {
	workspace,
	ExtensionContext,
	window,
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

let client: LanguageClient;
let clientReady: Promise<void> | undefined;
const TABLEGEN_FILE_REGEX = /(tablege|es).*\.(properties|prop)$/i;
const TABLEGEN_FILE_GLOB = '**/{tablege,es}*.{properties,prop}';

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
			tooltip: 'TableGear 配置文件',
			color: new ThemeColor('charts.yellow')
		};
	}

	refresh(uri?: Uri): void {
		this._emitter.fire(uri);
	}
}
