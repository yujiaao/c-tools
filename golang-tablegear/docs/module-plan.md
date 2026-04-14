# golang-tablegear 模块功能规划（V0 -> V1）

## 1. 目标与边界

- 目标：结合 `c-dbtools` 的运行时能力与 `c-tablegen` 的代码生成能力，构建 Go 版 ORM+代码生成工具。
- 产物形态：
  - 运行时库（查询执行、方言、日志、分页、事务）
  - 代码生成器（从数据库元数据生成 model/repository/query DSL）
  - CLI 工具（`tablegear`）
- 首阶段聚焦：MySQL/PostgreSQL 两类常见数据库，先实现“可生成 + 可编译 + 可扩展”。

## 2. 核心能力映射

### 来自 c-dbtools 的能力（运行时）

- 方言抽象：分页 SQL、参数占位、`upsert` 差异化。
- 通用 CRUD 基座：按主键查、条件查、分页、批量写。
- 读写分离：主从路由（先提供接口与策略，默认单库）。
- 可观测性：SQL 执行耗时、慢 SQL 阈值、参数脱敏日志。
- 事务封装：统一 `WithTx` 辅助函数。

### 来自 c-tablegen 的能力（生成器）

- 读取数据库元数据（表、列、主键、索引、注释）。
- 模板驱动生成（model/repository/query）。
- 增量生成策略：保留用户自定义代码（后续通过 marker 或 partial file 机制）。
- CLI 配置化：支持 table 白名单、输出目录、包名、命名策略。

### 参考 GORM Gen 的设计

- 类型安全查询入口（后续提供轻量 DSL）。
- 以“生成代码 + 可组合 API”为核心，而非纯反射式 ORM。
- 参考文档：[GORM Gen](https://gorm.io/zh_CN/gen)。

## 3. 模块拆分

- `cmd/tablegear`：CLI 入口。
- `internal/config`：配置解析与校验。
- `internal/meta`：数据库元数据抽取。
- `internal/generator`：模板渲染与文件输出。
- `pkg/runtime`（后续）：运行时能力（DB、Dialect、Logger、Tx）。
- `pkg/query`（后续）：类型安全查询构造器。

## 4. 里程碑

### M1（当前实现）

- 初始化 Go module 与 CLI。
- 配置结构体与命令行参数解析。
- 元数据抽取基础实现（information_schema）。
- 生成基础 `model` 与 `repository` 代码。
- 生成规划文档与 README。

### M2

- 完善方言层（MySQL/PostgreSQL 分页、upsert）。
- 运行时 `DB` 封装（Exec/Query/Tx + SQL 日志）。
- 生成器支持更多标签（nullable/default/index/comment）。

### M3

- 类型安全 Query DSL（类似 GORM Gen 的链式查询）。
- 增量合并（保留用户自定义扩展区）。
- 测试样例与基准测试。

> 当前进度：已实现 M3 的 Query DSL 初版（字段常量、链式 where/order/limit、生成强类型条件构造器），增量合并与系统化测试待后续阶段完善。

## 5. 非功能要求

- 代码默认 ASCII。
- 生成结果可通过 `go test ./...` 编译校验。
- 生成文件可重复执行，默认覆盖受控目录下自动生成文件。

