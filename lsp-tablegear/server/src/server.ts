import {
	createConnection,
	TextDocuments,
	Diagnostic,
	DiagnosticSeverity,
	ProposedFeatures,
	InitializeParams,
	CompletionItem,
	CompletionItemKind,
	TextDocumentPositionParams,
	TextDocumentSyncKind,
	InitializeResult,
	InsertTextFormat,
	SemanticTokensBuilder,
	SemanticTokensLegend,
	Hover,
	HoverParams,
	MarkupKind
} from 'vscode-languageserver/node';

import { TextDocument } from 'vscode-languageserver-textdocument';

const connection = createConnection(ProposedFeatures.all);
const documents: TextDocuments<TextDocument> = new TextDocuments(TextDocument);

let hasWorkspaceFolderCapability = false;

type ValueType = 'string' | 'boolean' | 'enum' | 'list' | 'path' | 'sql';
type ConfigMode = 'database' | 'elasticsearch' | 'json' | 'common';

interface TablegenPropertyDefinition {
	key: string;
	description: string;
	valueType: ValueType;
	enumValues?: string[];
	keySnippet?: string;
	mode?: ConfigMode | ConfigMode[]; // 配置项适用的模式，未指定则适用于所有模式
}

interface DbUrlTemplate {
	label: string;
	sample: string;
	description: string;
	drivers: RegExp[];
}

const TABLEGEN_PROPERTIES: TablegenPropertyDefinition[] = [
	// 通用配置项（适用于所有模式）
	{ key: 'alias', description: '数据源或项目别名，会被当做 Spring Bean qualifier, 用于区分不同的数据库，以及主从读写分离的不同数据源', valueType: 'string', mode: 'common' },
	{ key: 'package_name', description: '生成代码的根包名', valueType: 'string', mode: 'common' },
	{ key: 'src_dir', description: '主代码输出目录', valueType: 'path', mode: 'common' },
	{ key: 'test_dir', description: '测试代码输出目录', valueType: 'path', mode: 'common' },
	{ key: 'resource_dir', description: '资源文件输出目录', valueType: 'path', mode: 'common' },
	{ key: 'service_dir', description: 'Service 代码输出目录', valueType: 'path', mode: 'common' },
	{ key: 'controller_dir', description: 'Controller 代码输出目录', valueType: 'path', mode: 'common' },
	{ key: 'dto_dir', description: 'DTO 代码输出目录', valueType: 'path', mode: 'common' },
	{ key: 'cmd_dir', description: 'Command/DTO 代码目录', valueType: 'path', mode: 'common' },
	{ key: 'extra_setting', description: '额外配置文件路径（如 pojos.xml）', valueType: 'path', mode: 'common' },
	{ key: 'generate_all', description: '是否全量生成，no 时进入增量模式', valueType: 'boolean', mode: 'common' },
	{ key: 'over_write_all', description: '是否覆盖现有文件', valueType: 'boolean', mode: 'common' },
	{ key: 'use_annotation', description: '是否启用 Jakarta Validation/Swagger 注解', valueType: 'boolean', mode: 'common' },
	{ key: 'use_swagger', description: 'Controller 是否生成 Swagger 注解', valueType: 'boolean', mode: 'common' },
	{ key: 'swagger_version', description: 'Swagger/OpenAPI 版本', valueType: 'enum', enumValues: ['2', '3'], mode: 'common' },
	{ key: 'ignore_fields', description: '生成时忽略的字段，格式 table:field1,field2 或 field1,field2', valueType: 'list', mode: 'common' },
	{ key: 'collection_fields', description: '需要生成集合类型的字段', valueType: 'list', mode: 'common' },
	{ key: 'add_prefix', description: '生成类名、文件名前统一追加的前缀', valueType: 'string', mode: 'common' },
	{ key: 'remove_prefix', description: '解析表名时需要去掉的前缀，常用于数据库真实表名前缀', valueType: 'string', mode: 'common' },
	
	// 数据库模式专用配置项
	{ key: 'projectname', description: '生成项目名称，用于生成代码中的展示', valueType: 'string', mode: 'database' },
	{ key: 'className', description: 'JDBC 驱动类，例如 com.mysql.cj.jdbc.Driver', valueType: 'string', mode: 'database' },
	{ key: 'dburl', description: '数据库 JDBC URL', valueType: 'string', mode: 'database' },
	{ key: 'username', description: '数据库账号', valueType: 'string', mode: ['database', 'elasticsearch', 'json'] },
	{ key: 'password', description: '数据库密码', valueType: 'string', mode: ['database', 'elasticsearch', 'json'] },
	{ key: 'schema', description: '数据库 schema / catalog，用于过滤表', valueType: 'string', mode: 'database' },
	{ key: 'table_owner', description: '表所属用户（Oracle/DB2 等）', valueType: 'string', mode: 'database' },
	{ key: 'catalog', description: 'JDBC catalog，用于限定元信息', valueType: 'string', mode: 'database' },
	{ key: 'read_write_separate', description: '是否按 alias 生成读写分离注入', valueType: 'boolean', mode: 'database' },
	{ key: 'keys', description: '是否生成主键信息', valueType: 'boolean', mode: 'database' },
	{ key: 'indexes', description: '是否生成索引相关方法', valueType: 'boolean', mode: 'database' },
	{ key: 'types_are_strings', description: '是否将所有字段解析为字符串', valueType: 'boolean', mode: 'database' },
	{ key: 'use_custom_meta_table', description: '是否使用自定义元信息表', valueType: 'boolean', mode: 'database' },
	{ key: 'table_list', description: '参与生成的表，可为逗号或正则', valueType: 'list', keySnippet: 'table_list=${1:table_a,table_b}', mode: 'database' },
	{ key: 'exclude_table_list', description: '排除的表，优先级高于 table_list', valueType: 'list', mode: 'database' },
	{ key: 'view_list', description: '虚拟视图定义，支持 “view: select ...;” 或 “view: @relative/path.sql;” 两种写法（@ 表示引用外部 SQL）', valueType: 'sql', keySnippet: 'view_list=${1:view_name}: ${2:select * from table};', mode: 'database' },
	{ key: 'r2dbc', description: '是否生成 R2DBC 代码', valueType: 'boolean', mode: 'database' },
	{ key: 'generate_baselist', description: '是否生成 BaseList / 列表基础类', valueType: 'boolean', mode: 'database' },
	{ key: 'use_autoincrement', description: '是否使用数据库自增，不再生成主键插入', valueType: 'boolean', mode: 'database' },
	{ key: 'foreign_keys', description: '是否读取外键信息', valueType: 'boolean', mode: 'database' },
	{ key: 'kuozhanbiao', description: '是否启用扩展表生成', valueType: 'boolean', mode: 'database' },
	{ key: 'modifydate_col', description: '修改时间列名', valueType: 'string', mode: 'database' },
	{ key: 'pojo_modifydate_interface', description: '实现修改时间接口的全限定名', valueType: 'string', mode: 'database' },
	{ key: 'encrypted_fields', description: '需要加解密的字段，格式 table1:field1,field2;table2:field3,field4;', valueType: 'list', mode: 'database' },
	{ key: 'pojo_node_interface', description: 'POJO 需要实现的接口', valueType: 'string', mode: 'database' },
	{ key: 'pojo_node_interface_list', description: '按表控制实现接口的清单', valueType: 'list', mode: 'database' },
	{ key: 'pojo_version_interface', description: 'POJO 版本接口全限定名', valueType: 'string', mode: 'database' },
	{ key: 'pojo_version_interface_list', description: '按表控制实现版本接口的清单', valueType: 'list', mode: 'database' },
	{ key: 'pojo_state_interface', description: 'POJO 状态接口全限定名', valueType: 'string', mode: 'database' },
	{ key: 'pojo_state_interface_list', description: '按表控制实现状态接口的清单', valueType: 'list', mode: 'database' },
	{ key: 'pojo_uuid_interface', description: 'POJO UUID 接口全限定名', valueType: 'string', mode: 'database' },
	{ key: 'pojo_uuid_interface_list', description: '按表控制实现 UUID 接口的清单', valueType: 'list', mode: 'database' },
	{ key: 'pojo_modifydate_interface_list', description: '按表控制实现修改时间接口的清单', valueType: 'list', mode: 'database' },
	{ key: 'del_col_name', description: '逻辑删除列名，默认 is_del', valueType: 'string', mode: 'database' },
	{ key: 'version_col_name', description: '版本列名，默认 version', valueType: 'string', mode: 'database' },
	{ key: 'jsp_dir', description: 'JSP 文件输出目录', valueType: 'path', mode: 'common' },
	{ key: 'shared_model_group', description: '共享模型组名', valueType: 'string', mode: 'common' },
	{ key: 'generate_procedures', description: '是否生成存储过程相关代码', valueType: 'boolean', mode: 'database' },
	
	// Elasticsearch/JSON 模式专用配置项
	{ key: 'type', description: '生成模式：database（默认，从数据库生成）、elasticsearch_mapping（从 ES mapping 生成）、elasticsearch_sample（从 ES 样本生成）、json（从 JSON 文件生成）', valueType: 'enum', enumValues: ['database', 'elasticsearch_mapping', 'elasticsearch_sample', 'json'], mode: 'common' },
	{ key: 'url', description: 'ElasticSearch 访问地址（如 http://localhost:9200）或 JSON 文件路径', valueType: 'string', mode: ['elasticsearch', 'json'] },
	{ key: 'index_list', description: 'ElasticSearch 索引列表，格式 indexName:aliasName 或 indexName', valueType: 'list', mode: ['elasticsearch', 'json'] },
	{ key: 'primary_key_fields', description: 'ES 主键字段（映射到 _id），多个用逗号分隔', valueType: 'list', mode: ['elasticsearch', 'json'] }
];

const PROPERTY_MAP = new Map(TABLEGEN_PROPERTIES.map((item) => [item.key, item]));

const BOOLEAN_VALUES = ['yes', 'no', 'true', 'false'];
const TABLEGEN_FILE_PATTERN = /^(tablegen|es).*\.(properties|prop)$/i;
const SEMANTIC_LEGEND: SemanticTokensLegend = {
	tokenTypes: [
		'tablegearComment',
		'tablegearDatabase',
		'tablegearPath',
		'tablegearBoolean',
		'tablegearList',
		'tablegearViewSeparator',
		'tablegearAssignment',
		'tablegearViewName',
		'tablegearSql',
		'tablegearSensitive',
		'tablegearSensitiveKey',
		'tablegearSensitiveField',
		'tablegearPackageName',
		'tablegearViewListKey'
	],
	tokenModifiers: []
};
const TOKEN_TYPE_INDEX = new Map(SEMANTIC_LEGEND.tokenTypes.map((type, index) => [type, index]));

const DATABASE_KEYS = new Set([
	'className',
	'dburl',
	'username',
	'password',
	'schema',
	'table_owner',
	'catalog',
	'projectname',
	'alias',
	'add_prefix',
	'remove_prefix'
]);

const PATH_KEYS = new Set(
	TABLEGEN_PROPERTIES.filter((item) => item.valueType === 'path').map((item) => item.key)
);

const BOOLEAN_KEYS = new Set(
	TABLEGEN_PROPERTIES.filter((item) => item.valueType === 'boolean').map((item) => item.key)
);

const LIST_KEYS = new Set(
	TABLEGEN_PROPERTIES.filter((item) => item.valueType === 'list').map((item) => item.key)
);

const SENSITIVE_VALUE_KEYS = new Set(['encrypted_fields', 'ignore_fields']);

interface LineInfo {
	lines: string[];
	offsets: number[];
}

interface PendingProperty {
	key: string;
	lineStartOffset: number;
	keyStartOffset: number;
	keyEndOffset: number;
	valueStartOffset: number;
	valueParts: string[];
}

function computeLineInfo(text: string): LineInfo {
	const lines = text.split(/\r?\n/);
	const offsets: number[] = [];
	let offset = 0;
	lines.forEach((line) => {
		offsets.push(offset);
		const lineEnd = offset + line.length;
		offset = advanceOffset(text, lineEnd);
	});
	return { lines, offsets };
}

function isCommentLine(line: string): boolean {
	return line.trim().startsWith('#');
}

function hasContinuation(line: string): boolean {
	if (!line.length) {
		return false;
	}
	if (isCommentLine(line)) {
		return false;
	}
	return /\\\s*$/.test(line.replace(/\r$/, ''));
}

function stripContinuationSegment(segment: string): string {
	return segment.replace(/\\\s*$/, '');
}

function findBaseLineIndex(lineIndex: number, lines: string[]): number {
	let current = lineIndex;
	while (current > 0) {
		const prevLine = lines[current - 1];
		if (!hasContinuation(prevLine)) {
			break;
		}
		current--;
	}
	return current;
}

const DB_URL_TEMPLATES: DbUrlTemplate[] = [
	{
		label: 'MySQL / MariaDB',
		sample: 'jdbc:mysql://localhost:3306/your_db?useSSL=false&serverTimezone=UTC',
		description: 'MySQL 8+ 或 MariaDB 常见连接串',
		drivers: [/com\.mysql\./i, /org\.mariadb\./i]
	},
	{
		label: 'PostgreSQL',
		sample: 'jdbc:postgresql://localhost:5432/your_db',
		description: 'PostgreSQL 标准连接串',
		drivers: [/org\.postgresql\./i]
	},
	{
		label: 'SQL Server',
		sample:
			'jdbc:sqlserver://localhost:1433;databaseName=your_db;encrypt=true;trustServerCertificate=true',
		description: 'Microsoft SQL Server 连接串（默认开启加密）',
		drivers: [/com\.microsoft\.sqlserver\./i]
	},
	{
		label: 'Oracle',
		sample: 'jdbc:oracle:thin:@localhost:1521/ORCLPDB1',
		description: 'Oracle Thin Driver 连接串',
		drivers: [/oracle\.jdbc\./i]
	},
	{
		label: 'H2 内存数据库',
		sample: 'jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1',
		description: 'H2 in-memory 模式，常用于单元测试',
		drivers: [/org\.h2\./i]
	}
];

const JDBC_DRIVER_CLASSES: Array<{ class: string; description: string }> = [
	{ class: 'com.mysql.cj.jdbc.Driver', description: 'MySQL Connector/J 8.0+ (推荐)' },
	{ class: 'com.mysql.jdbc.Driver', description: 'MySQL Connector/J 5.x (已弃用)' },
	{ class: 'org.mariadb.jdbc.Driver', description: 'MariaDB JDBC Driver' },
	{ class: 'org.postgresql.Driver', description: 'PostgreSQL JDBC Driver' },
	{ class: 'com.microsoft.sqlserver.jdbc.SQLServerDriver', description: 'Microsoft SQL Server JDBC Driver' },
	{ class: 'oracle.jdbc.driver.OracleDriver', description: 'Oracle JDBC Driver (旧版)' },
	{ class: 'oracle.jdbc.OracleDriver', description: 'Oracle JDBC Driver (新版)' },
	{ class: 'org.h2.Driver', description: 'H2 Database Driver' },
	{ class: 'com.ibm.db2.jcc.DB2Driver', description: 'IBM DB2 JDBC Driver' },
	{ class: 'com.sap.db.jdbc.Driver', description: 'SAP HANA JDBC Driver' },
	{ class: 'org.apache.derby.jdbc.EmbeddedDriver', description: 'Apache Derby Embedded Driver' },
	{ class: 'org.apache.derby.jdbc.ClientDriver', description: 'Apache Derby Client Driver' },
	{ class: 'com.sybase.jdbc4.jdbc.SybDriver', description: 'Sybase JDBC Driver' },
	{ class: 'com.informix.jdbc.IfxDriver', description: 'IBM Informix JDBC Driver' }
];
function getConfigMode(documentText: string): ConfigMode {
	const typeValue = extractPropertyValue(documentText, 'type');
	if (!typeValue) {
		return 'database'; // 默认数据库模式
	}
	const typeLower = typeValue.toLowerCase();
	if (typeLower === 'json') {
		return 'json';
	}
	if (typeLower.includes('elasticsearch')) {
		return 'elasticsearch';
	}
	return 'database';
}

function getKeyCompletionsForMode(mode: ConfigMode): CompletionItem[] {
	return TABLEGEN_PROPERTIES.filter((def) => {
		if (!def.mode) {
			return true; // 未指定模式则适用于所有模式
		}
		if (Array.isArray(def.mode)) {
			return def.mode.includes(mode) || def.mode.includes('common');
		}
		return def.mode === mode || def.mode === 'common';
	}).map((def) => ({
		label: def.key,
		kind: CompletionItemKind.Property,
		documentation: def.description,
		insertText: def.keySnippet ?? `${def.key}=`,
		insertTextFormat: def.keySnippet ? InsertTextFormat.Snippet : InsertTextFormat.PlainText
	}));
}

connection.onInitialize((params: InitializeParams) => {
	const capabilities = params.capabilities;
	hasWorkspaceFolderCapability = !!(capabilities.workspace && capabilities.workspace.workspaceFolders);

	const result: InitializeResult = {
		capabilities: {
			textDocumentSync: TextDocumentSyncKind.Incremental,
			completionProvider: {
				resolveProvider: false
			},
			hoverProvider: true,
			semanticTokensProvider: {
				legend: SEMANTIC_LEGEND,
				range: false,
				full: true
			}
		}
	};

	if (hasWorkspaceFolderCapability) {
		result.capabilities.workspace = {
			workspaceFolders: {
				supported: true
			}
		};
	}
	return result;
});

connection.onInitialized(() => {
	if (hasWorkspaceFolderCapability) {
		connection.workspace.onDidChangeWorkspaceFolders((event) => {
			connection.console.log(`Workspace folder changed: ${JSON.stringify(event)}`);
		});
	}
});

connection.onDidChangeConfiguration(() => {
	documents.all().forEach(validateTextDocument);
});

documents.onDidOpen((event) => {
	validateTextDocument(event.document);
});

documents.onDidChangeContent((change) => {
	validateTextDocument(change.document);
});

documents.onDidClose((event) => {
	connection.sendDiagnostics({ uri: event.document.uri, diagnostics: [] });
});

connection.onCompletion((params: TextDocumentPositionParams): CompletionItem[] => {
	const textDocument = documents.get(params.textDocument.uri);
	if (!textDocument) {
		return [];
	}

	if (!isTablegenUri(textDocument.uri)) {
		return [];
	}

	const text = textDocument.getText();
	const offset = textDocument.offsetAt(params.position);
	const { lines, offsets } = computeLineInfo(text);
	const currentLine = textDocument.positionAt(offset).line;
	const baseLineIndex = findBaseLineIndex(currentLine, lines);
	const line = lines[baseLineIndex] ?? '';
	const lineStart = offsets[baseLineIndex] ?? 0;
	const relativePos = offset - lineStart;
	const equalsIndex = line.indexOf('=');
	const isValuePosition =
		baseLineIndex !== currentLine ? true : equalsIndex !== -1 && relativePos > equalsIndex;

	if (equalsIndex === -1 || !isValuePosition) {
		const mode = getConfigMode(text);
		return getKeyCompletionsForMode(mode);
	}

	const key = line.substring(0, equalsIndex).trim();
	const def = PROPERTY_MAP.get(key);
	if (!def) {
		return [];
	}

	return getValueCompletionItems(def, text);
});

connection.onHover((params: HoverParams): Hover | null => {
	const textDocument = documents.get(params.textDocument.uri);
	if (!textDocument || !isTablegenUri(textDocument.uri)) {
		return null;
	}

	const text = textDocument.getText();
	const offset = textDocument.offsetAt(params.position);
	const { lines } = computeLineInfo(text);
	const currentLine = textDocument.positionAt(offset).line;
	const baseLineIndex = findBaseLineIndex(currentLine, lines);
	const line = lines[baseLineIndex] ?? '';
	const equalsIndex = line.indexOf('=');
	if (equalsIndex === -1) {
		return null;
	}

	const key = line.substring(0, equalsIndex).trim();
	const def = PROPERTY_MAP.get(key);
	if (!def) {
		return null;
	}

	// 不区分 hover 在 key 还是 value，都展示配置说明
	return {
		contents: {
			kind: MarkupKind.Markdown,
			value: formatPropertyDocumentation(def)
		}
	};
});

connection.languages.semanticTokens.on((params) => {
	const textDocument = documents.get(params.textDocument.uri);
	if (!textDocument || !isTablegenUri(textDocument.uri)) {
		return { data: [] };
	}

	const builder = new SemanticTokensBuilder();
	const text = textDocument.getText();
	const lines = text.split(/\r?\n/);

	for (let lineIndex = 0; lineIndex < lines.length; lineIndex++) {
		const line = lines[lineIndex];
		const trimmed = line.trim();
		if (!trimmed.length) {
			continue;
		}

		if (trimmed.startsWith('#')) {
			const commentStart = line.indexOf('#');
			if (commentStart >= 0) {
				addToken(builder, lineIndex, commentStart, line.length - commentStart, 'tablegearComment');
			}
			continue;
		}

		if (lineIndex > 0 && hasContinuation(lines[lineIndex - 1])) {
			continue;
		}

		const equalsIndex = line.indexOf('=');
		if (equalsIndex <= 0) {
			continue;
		}

		const keyRaw = line.substring(0, equalsIndex);
		const key = keyRaw.trim();
		if (!key.length) {
			continue;
		}

		const keyStart = keyRaw.indexOf(key);
		if (keyStart === -1) {
			continue;
		}

		const valueRaw = line.substring(equalsIndex + 1);

		addToken(builder, lineIndex, equalsIndex, 1, 'tablegearAssignment');
		addKeyToken(builder, lineIndex, keyStart, key.length, key);
		handleSpecialTokens(builder, lineIndex, equalsIndex + 1, valueRaw, key);
	}

	return builder.build();
});

function getValueCompletionItems(
	def: TablegenPropertyDefinition,
	documentText?: string
): CompletionItem[] {
	if (def.valueType === 'boolean') {
		return BOOLEAN_VALUES.map((value) => ({
			label: value,
			kind: CompletionItemKind.Value,
			documentation: `${def.key}: ${def.description}`,
			insertText: value
		}));
	}

	if (def.key === 'className') {
		const mode = documentText ? getConfigMode(documentText) : 'database';
		if (mode !== 'database') {
			return []; // className 仅在数据库模式下可用
		}
		return JDBC_DRIVER_CLASSES.map((driver) => ({
			label: driver.class,
			kind: CompletionItemKind.Class,
			documentation: driver.description,
			insertText: driver.class
		}));
	}

	if (def.key === 'dburl') {
		const mode = documentText ? getConfigMode(documentText) : 'database';
		if (mode !== 'database') {
			return []; // dburl 仅在数据库模式下可用
		}
		return getDbUrlCompletionItems(documentText);
	}

	if (def.key === 'type') {
		return [
			{
				label: 'database',
				kind: CompletionItemKind.EnumMember,
				documentation: '数据库模式（默认）：从数据库表生成代码',
				insertText: 'database'
			},
			{
				label: 'elasticsearch_mapping',
				kind: CompletionItemKind.EnumMember,
				documentation: 'Elasticsearch 模式：从 ES mapping 生成代码',
				insertText: 'elasticsearch_mapping'
			},
			{
				label: 'elasticsearch_sample',
				kind: CompletionItemKind.EnumMember,
				documentation: 'Elasticsearch 模式：从 ES 样本数据生成代码',
				insertText: 'elasticsearch_sample'
			},
			{
				label: 'json',
				kind: CompletionItemKind.EnumMember,
				documentation: 'JSON 模式：从 JSON 文件生成代码',
				insertText: 'json'
			}
		];
	}

	if (def.key === 'url') {
		const mode = documentText ? getConfigMode(documentText) : 'database';
		if (mode === 'elasticsearch' || mode === 'json') {
			return [
				{
					label: 'ES 地址示例',
					kind: CompletionItemKind.Snippet,
					insertText: 'http://localhost:9200',
					insertTextFormat: InsertTextFormat.PlainText,
					documentation: 'Elasticsearch 访问地址，格式：http://host:port'
				},
				{
					label: 'JSON 文件路径',
					kind: CompletionItemKind.Snippet,
					insertText: '${1:path/to/sample.json}',
					insertTextFormat: InsertTextFormat.Snippet,
					documentation: 'JSON 文件路径（相对或绝对路径）'
				}
			];
		}
	}

	if (def.enumValues) {
		return def.enumValues.map((value) => ({
			label: value,
			kind: CompletionItemKind.EnumMember,
			documentation: `${def.key}: ${def.description}`,
			insertText: value
		}));
	}

	if (def.valueType === 'list') {
		return [
			{
				label: '示例列表',
				kind: CompletionItemKind.Snippet,
				insertText: '${1:item_a},${2:item_b}',
				insertTextFormat: InsertTextFormat.Snippet,
				documentation: '逗号分隔的列表值'
			}
		];
	}

	if (def.valueType === 'sql') {
		return [
			{
				label: '示例视图',
				kind: CompletionItemKind.Snippet,
				insertText: '${1:view_name}: ${2:select * from table};',
				insertTextFormat: InsertTextFormat.Snippet,
				documentation: 'view_list 需要 “视图名: select ...;” 格式'
			}
		];
	}

	return [];
}

function formatPropertyDocumentation(def: TablegenPropertyDefinition): string {
	const lines: string[] = [`**${def.key}**`, '', def.description];
	lines.push('', `类型: \`${def.valueType}\``);

	if (def.enumValues && def.enumValues.length) {
		lines.push('', `可选值: ${def.enumValues.map((item) => `\`${item}\``).join(', ')}`);
	}

	if (def.valueType === 'boolean') {
		lines.push('', '有效值: `yes`, `no`, `true`, `false`');
	}

	return lines.join('\n');
}

function getDbUrlCompletionItems(documentText?: string): CompletionItem[] {
	const className = documentText ? extractPropertyValue(documentText, 'className') : undefined;
	const templates = getDbUrlTemplatesForClassName(className);
	return templates.map((template) => ({
		label: template.label,
		kind: CompletionItemKind.Snippet,
		insertText: template.sample,
		insertTextFormat: InsertTextFormat.PlainText,
		documentation: template.description
	}));
}

function validateTextDocument(textDocument: TextDocument): void {
	if (!isTablegenUri(textDocument.uri)) {
		connection.sendDiagnostics({
			uri: textDocument.uri,
			diagnostics: []
		});
		return;
	}

	const text = textDocument.getText();
	const diagnostics: Diagnostic[] = [];
	const seenKeys = new Map<string, number>();

	let offset = 0;
	const lines = text.split(/\r?\n/);
	let pending: PendingProperty | null = null;

	lines.forEach((line) => {
		const lineStart = offset;
		const lineEnd = offset + line.length;
		const trimmed = line.trim();

		if (!pending) {
			if (trimmed.length === 0 || trimmed.startsWith('#')) {
				offset = advanceOffset(text, lineEnd);
				return;
			}

			const equalsIndex = line.indexOf('=');
			if (equalsIndex <= 0) {
				diagnostics.push({
					severity: DiagnosticSeverity.Error,
					range: {
						start: textDocument.positionAt(lineStart),
						end: textDocument.positionAt(lineEnd)
					},
					message: '配置项必须使用 key=value 形式',
					source: 'lsp-tablegear'
				});
				offset = advanceOffset(text, lineEnd);
				return;
			}

			const keyRaw = line.substring(0, equalsIndex);
			const key = keyRaw.trim();
			if (!key.length) {
				offset = advanceOffset(text, lineEnd);
				return;
			}

			const keyStartOffset = lineStart + keyRaw.indexOf(key);
			const keyEndOffset = keyStartOffset + key.length;
			const valuePart = line.substring(equalsIndex + 1);

			pending = {
				key,
				lineStartOffset: lineStart,
				keyStartOffset,
				keyEndOffset,
				valueStartOffset: lineStart + equalsIndex + 1,
				valueParts: [stripContinuationSegment(valuePart)]
			};

			if (!hasContinuation(line)) {
				finalizePendingProperty(textDocument, pending, lineEnd, diagnostics, seenKeys);
				pending = null;
			}
		} else {
			pending.valueParts.push(stripContinuationSegment(line));

			if (!hasContinuation(line)) {
				finalizePendingProperty(textDocument, pending, lineEnd, diagnostics, seenKeys);
				pending = null;
			}
		}

		offset = advanceOffset(text, lineEnd);
	});

	if (pending) {
		finalizePendingProperty(textDocument, pending, text.length, diagnostics, seenKeys);
	}

	connection.sendDiagnostics({
		uri: textDocument.uri,
		diagnostics
	});
}

function finalizePendingProperty(
	textDocument: TextDocument,
	pending: PendingProperty,
	finalLineEnd: number,
	diagnostics: Diagnostic[],
	seenKeys: Map<string, number>
): void {
	const key = pending.key;
	const keyRange = {
		start: textDocument.positionAt(pending.keyStartOffset),
		end: textDocument.positionAt(pending.keyEndOffset)
	};

	if (seenKeys.has(key)) {
		diagnostics.push({
			severity: DiagnosticSeverity.Warning,
			range: keyRange,
			message: `重复定义的配置项 ${key}`,
			source: 'lsp-tablegear'
		});
	} else {
		seenKeys.set(key, pending.lineStartOffset);
	}

	const definition = PROPERTY_MAP.get(key);
	if (!definition) {
		diagnostics.push({
			severity: DiagnosticSeverity.Warning,
			range: keyRange,
			message: `未识别的配置项 ${key}`,
			source: 'lsp-tablegear'
		});
		return;
	}

	const combinedValue = pending.valueParts.join('').trim();
	const firstSegment = pending.valueParts[0] ?? '';
	const leadingWhitespaceLength = firstSegment.length - firstSegment.trimStart().length;
	const valueStartOffset = Math.min(
		pending.valueStartOffset + Math.max(leadingWhitespaceLength, 0),
		finalLineEnd
	);

	const valueRange = {
		start: textDocument.positionAt(valueStartOffset),
		end: textDocument.positionAt(finalLineEnd)
	};

	if (!combinedValue.length) {
		diagnostics.push({
			severity: DiagnosticSeverity.Warning,
			range: valueRange,
			message: `${key} 的值为空`,
			source: 'lsp-tablegear'
		});
	} else if (definition.valueType === 'boolean' && !/^(yes|no|true|false)$/i.test(combinedValue)) {
		diagnostics.push({
			severity: DiagnosticSeverity.Error,
			range: valueRange,
			message: `${key} 需要 yes/no（或 true/false）`,
			source: 'lsp-tablegear'
		});
	} else if (
		definition.enumValues &&
		!definition.enumValues.includes(combinedValue.toLowerCase())
	) {
		diagnostics.push({
			severity: DiagnosticSeverity.Error,
			range: valueRange,
			message: `${key} 仅支持 ${definition.enumValues.join(', ')}`,
			source: 'lsp-tablegear'
		});
	}
}

function advanceOffset(text: string, currentEnd: number): number {
	let next = currentEnd;
	if (text[next] === '\r') {
		next++;
	}
	if (text[next] === '\n') {
		next++;
	}
	return next;
}

function addKeyToken(
	builder: SemanticTokensBuilder,
	line: number,
	startChar: number,
	length: number,
	key: string
) {
	if (DATABASE_KEYS.has(key)) {
		addToken(builder, line, startChar, length, 'tablegearDatabase');
		return;
	}
	if (PATH_KEYS.has(key)) {
		addToken(builder, line, startChar, length, 'tablegearPath');
		return;
	}
	if (BOOLEAN_KEYS.has(key)) {
		addToken(builder, line, startChar, length, 'tablegearBoolean');
		return;
	}
	if (SENSITIVE_VALUE_KEYS.has(key)) {
		addToken(builder, line, startChar, length, 'tablegearSensitive');
		return;
	}
	if (LIST_KEYS.has(key)) {
		addToken(builder, line, startChar, length, 'tablegearList');
		return;
	}
	if (key === 'package_name') {
		addToken(builder, line, startChar, length, 'tablegearPackageName');
		return;
	}
	if (key === 'view_list') {
		addToken(builder, line, startChar, length, 'tablegearViewListKey');
		return;
	}
}

function handleSpecialTokens(
	builder: SemanticTokensBuilder,
	line: number,
	valueStartIndex: number,
	valueRaw: string,
	key: string
) {
	if (!valueRaw.length) {
		return;
	}

	if (key === 'view_list') {
		highlightViewList(builder, line, valueStartIndex, valueRaw);
	}

	if (SENSITIVE_VALUE_KEYS.has(key)) {
		highlightEncryptedFields(builder, line, valueStartIndex, valueRaw);
	}
}

function addToken(
	builder: SemanticTokensBuilder,
	line: number,
	character: number,
	length: number,
	tokenType: string
) {
	if (length <= 0) {
		return;
	}
	const tokenTypeIndex = TOKEN_TYPE_INDEX.get(tokenType);
	if (tokenTypeIndex === undefined) {
		return;
	}
	builder.push(line, character, length, tokenTypeIndex, 0);
}

function getDbUrlTemplatesForClassName(className?: string): DbUrlTemplate[] {
	if (!className) {
		return DB_URL_TEMPLATES;
	}
	const matched = DB_URL_TEMPLATES.filter((template) =>
		template.drivers.some((regex) => regex.test(className))
	);
	return matched.length ? matched : DB_URL_TEMPLATES;
}

function extractPropertyValue(text: string, targetKey: string): string | undefined {
	const lines = text.split(/\r?\n/);
	let continuationActive = false;
	let capturingTarget = false;
	let collectedValue = '';

	for (const rawLine of lines) {
		const trimmed = rawLine.trim();

		if (!continuationActive) {
			if (!trimmed.length || trimmed.startsWith('#')) {
				continue;
			}
			const equalsIndex = rawLine.indexOf('=');
			if (equalsIndex <= 0) {
				continue;
			}

			const keyPart = rawLine.substring(0, equalsIndex).trim();
			const valuePart = rawLine.substring(equalsIndex + 1);
			const continuation = hasContinuation(rawLine);

			if (keyPart === targetKey) {
				capturingTarget = true;
				collectedValue = stripContinuationSegment(valuePart).trim();
				if (!continuation) {
					return collectedValue;
				}
			} else {
				capturingTarget = false;
				collectedValue = '';
			}

			continuationActive = continuation;
		} else {
			const continuation = hasContinuation(rawLine);
			if (capturingTarget) {
				collectedValue += stripContinuationSegment(rawLine).trim();
				if (!continuation) {
					return collectedValue.trim();
				}
			}

			continuationActive = continuation;
			if (!continuation) {
				capturingTarget = false;
				collectedValue = '';
			}
		}
	}

	return capturingTarget ? collectedValue.trim() : undefined;
}

function highlightViewList(
	builder: SemanticTokensBuilder,
	line: number,
	valueStartIndex: number,
	valueRaw: string
) {
	let cursor = 0;
	const length = valueRaw.length;

	while (cursor < length) {
		cursor = findNextNonWhitespace(valueRaw, cursor, length);
		if (cursor >= length) {
			break;
		}

		const semicolonIndex = valueRaw.indexOf(';', cursor);
		const segmentEnd = semicolonIndex === -1 ? length : semicolonIndex;
		if (cursor >= segmentEnd) {
			if (semicolonIndex !== -1) {
				addToken(builder, line, valueStartIndex + semicolonIndex, 1, 'tablegearViewSeparator');
				cursor = semicolonIndex + 1;
				continue;
			}
			break;
		}

		const colonIndex = valueRaw.indexOf(':', cursor);
		if (colonIndex === -1 || colonIndex >= segmentEnd) {
			if (semicolonIndex !== -1) {
				addToken(builder, line, valueStartIndex + semicolonIndex, 1, 'tablegearViewSeparator');
				cursor = semicolonIndex + 1;
				continue;
			}
			break;
		}

		const viewStart = findNextNonWhitespace(valueRaw, cursor, colonIndex);
		const viewEnd = findPrevNonWhitespace(valueRaw, colonIndex - 1) + 1;
		if (viewStart < viewEnd) {
			addToken(
				builder,
				line,
				valueStartIndex + viewStart,
				viewEnd - viewStart,
				'tablegearViewName'
			);
		}

		addToken(builder, line, valueStartIndex + colonIndex, 1, 'tablegearViewSeparator');

		const sqlStart = findNextNonWhitespace(valueRaw, colonIndex + 1, segmentEnd);
		if (sqlStart < segmentEnd) {
			if (valueRaw[sqlStart] === '@') {
				addToken(builder, line, valueStartIndex + sqlStart, 1, 'tablegearViewSeparator');
			} else {
				const sqlEnd = findPrevNonWhitespace(valueRaw, segmentEnd - 1) + 1;
				if (sqlStart < sqlEnd) {
					addToken(
						builder,
						line,
						valueStartIndex + sqlStart,
						sqlEnd - sqlStart,
						'tablegearSql'
					);
				}
			}
		}

		if (semicolonIndex !== -1) {
			addToken(builder, line, valueStartIndex + semicolonIndex, 1, 'tablegearViewSeparator');
			cursor = semicolonIndex + 1;
		} else {
			break;
		}
	}
}

function highlightEncryptedFields(
	builder: SemanticTokensBuilder,
	line: number,
	valueStartIndex: number,
	valueRaw: string
) {
	let cursor = 0;
	const length = valueRaw.length;
	while (cursor < length) {
		cursor = findNextNonWhitespace(valueRaw, cursor, length);
		if (cursor >= length) {
			break;
		}

		const semicolonIndex = valueRaw.indexOf(';', cursor);
		const segmentEnd = semicolonIndex === -1 ? length : semicolonIndex;
		const colonIndex = valueRaw.indexOf(':', cursor);

		if (colonIndex !== -1 && colonIndex < segmentEnd) {
			const tableStart = findNextNonWhitespace(valueRaw, cursor, colonIndex);
			const tableEnd = findPrevNonWhitespace(valueRaw, colonIndex - 1) + 1;
			if (tableStart < tableEnd) {
				addToken(
					builder,
					line,
					valueStartIndex + tableStart,
					tableEnd - tableStart,
					'tablegearSensitiveKey'
				);
			}

			addToken(builder, line, valueStartIndex + colonIndex, 1, 'tablegearSensitive');

			const fieldStart = findNextNonWhitespace(valueRaw, colonIndex + 1, segmentEnd);
			const fieldEnd = findPrevNonWhitespace(valueRaw, segmentEnd - 1) + 1;
			if (fieldStart < fieldEnd) {
				addToken(
					builder,
					line,
					valueStartIndex + fieldStart,
					fieldEnd - fieldStart,
					'tablegearSensitiveField'
				);
			}

			for (
				let commaIdx = valueRaw.indexOf(',', colonIndex + 1);
				commaIdx >= 0 && commaIdx < segmentEnd;
				commaIdx = valueRaw.indexOf(',', commaIdx + 1)
			) {
				addToken(builder, line, valueStartIndex + commaIdx, 1, 'tablegearSensitive');
			}
		} else {
			const listStart = findNextNonWhitespace(valueRaw, cursor, segmentEnd);
			const listEnd = findPrevNonWhitespace(valueRaw, segmentEnd - 1) + 1;
			if (listStart < listEnd) {
				addToken(
					builder,
					line,
					valueStartIndex + listStart,
					listEnd - listStart,
					'tablegearSensitiveField'
				);
			}
			for (
				let commaIdx = valueRaw.indexOf(',', cursor);
				commaIdx >= 0 && commaIdx < segmentEnd;
				commaIdx = valueRaw.indexOf(',', commaIdx + 1)
			) {
				addToken(builder, line, valueStartIndex + commaIdx, 1, 'tablegearSensitive');
			}
		}

		if (semicolonIndex !== -1) {
			addToken(builder, line, valueStartIndex + semicolonIndex, 1, 'tablegearSensitive');
		}

		cursor = semicolonIndex === -1 ? length : semicolonIndex + 1;
	}
}

function findNextNonWhitespace(text: string, start: number, end: number): number {
	let index = start;
	while (index < end && /\s/.test(text[index])) {
		index++;
	}
	return index;
}

function findPrevNonWhitespace(text: string, start: number): number {
	let index = start;
	while (index >= 0 && /\s/.test(text[index])) {
		index--;
	}
	return index;
}

function isTablegenUri(uri: string): boolean {
	if (!uri.startsWith('file:')) {
		return false;
	}
	try {
		const decoded = decodeURIComponent(uri);
		const fileNameMatch = decoded.match(/[^/\\]+$/);
		if (!fileNameMatch) {
			return false;
		}
		return TABLEGEN_FILE_PATTERN.test(fileNameMatch[0]);
	} catch {
		return false;
	}
}

documents.listen(connection);
connection.listen();
