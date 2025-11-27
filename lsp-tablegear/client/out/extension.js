"use strict";
/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
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
exports.deactivate = exports.activate = void 0;
const path = __importStar(require("path"));
const vscode_1 = require("vscode");
const node_1 = require("vscode-languageclient/node");
let client;
let clientReady;
const TABLEGEN_FILE_REGEX = /(tablegen|es).*\.(properties|prop)$/i;
const TABLEGEN_FILE_GLOB = '**/{tablegen,es}*.{properties,prop}';
function activate(context) {
    // The server is implemented in node
    let serverModule = context.asAbsolutePath(path.join('server', 'out', 'server.js'));
    // The debug options for the server
    // --inspect=6009: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging
    let debugOptions = { execArgv: ['--nolazy', '--inspect=6009'] };
    // If the extension is launched in debug mode then the debug server options are used
    // Otherwise the run options are used
    let serverOptions = {
        run: { module: serverModule, transport: node_1.TransportKind.ipc },
        debug: {
            module: serverModule,
            transport: node_1.TransportKind.ipc,
            options: debugOptions
        }
    };
    // Options to control the language client
    let clientOptions = {
        // Monitor properties 文件，由服务端按文件名再次过滤
        documentSelector: [
            { scheme: 'file', language: 'tablegear' },
            { scheme: 'file', language: 'properties' },
            { scheme: 'untitled', language: 'properties' }
        ],
        synchronize: {
            fileEvents: vscode_1.workspace.createFileSystemWatcher(TABLEGEN_FILE_GLOB)
        }
    };
    // Create the language client and start the client.
    client = new node_1.LanguageClient('tablegearLanguageServer', 'TableGear Language Server', serverOptions, clientOptions);
    // Start the client. This will also launch the server
    clientReady = client.start();
    const decorationProvider = new TablegearDecorationProvider();
    context.subscriptions.push(vscode_1.window.registerFileDecorationProvider(decorationProvider));
    const decorationWatcher = vscode_1.workspace.createFileSystemWatcher(TABLEGEN_FILE_GLOB);
    context.subscriptions.push(decorationWatcher);
    decorationWatcher.onDidCreate((uri) => decorationProvider.refresh(uri));
    decorationWatcher.onDidChange((uri) => decorationProvider.refresh(uri));
    decorationWatcher.onDidDelete((uri) => decorationProvider.refresh(uri));
    return clientReady;
}
exports.activate = activate;
function deactivate() {
    if (!client) {
        return undefined;
    }
    return client.stop();
}
exports.deactivate = deactivate;
class TablegearDecorationProvider {
    constructor() {
        this._emitter = new vscode_1.EventEmitter();
        this.onDidChangeFileDecorations = this._emitter.event;
    }
    provideFileDecoration(uri) {
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
            color: new vscode_1.ThemeColor('charts.yellow')
        };
    }
    refresh(uri) {
        this._emitter.fire(uri);
    }
}
//# sourceMappingURL=extension.js.map