# lsp-TableGear

**English**

Tablegear Language Assistant is a VS Code extension tailored for `tablegen*.properties/prop` files used by c-tablegen. It ships a full language server that understands every configuration key, provides intelligent completion, inline documentation, diagnostics (duplicate keys, invalid boolean/enum values, missing values), and semantic highlighting inspired by Rainbow CSV. Sensitive options like `encrypted_fields` get per-part coloring, `view_list` separators stand out, and `#` comments render in soft gray. The client watches relevant files, adds a “TG” badge in the Explorer via a file-decoration provider, and exposes E2E tests plus scripts to regenerate the bundled logo. Default settings automatically enable semantic highlighting and color rules, so users install and start editing—no extra setup required.

`view_list` supports two value styles:  
1. `view_name: SELECT ... FROM ...;` inline SQL segments (cyan view name + SQL highlighting).  
2. `view_name: @relative/path/to_file.sql;` file references that point to external SQL files (path is resolved relative to the properties file).  
Both styles can be mixed—each entry is split by `;`.

**中文**

Tablegear Language Assistant 是专为 `tablegen*.properties/prop` 配置文件打造的 VS Code 插件。它内置完整的语言服务器，可识别全部配置项，提供智能补全、参数说明、实时诊断（如重复 key、布尔/枚举取值错误、空值等）以及类似 Rainbow CSV 的语义高亮。`encrypted_fields`、`view_list` 等敏感字段会按段落高亮，“#” 注释默认灰色显示。客户端会监控相关文件，并通过 FileDecorationProvider 在资源管理器里给 Tablegear 配置打上 “TG” 角标，同时附带端到端测试与 Logo 生成脚本。扩展默认启用语义高亮与配色，安装后即可使用，无需额外配置。

`view_list` 有两种写法：  
1. `view_name: SELECT ... FROM ...;` 内联 SQL（view 名为青色，SQL 部分按 SQL 高亮）。  
2. `view_name: @relative/path/to_file.sql;` 使用 `@` 引用外部 SQL 文件（相对路径基于当前配置文件）。  
两种格式可以混用，每个视图依旧以分号分隔。