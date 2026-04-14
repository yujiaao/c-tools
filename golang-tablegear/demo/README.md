# Demo: 代码生成与使用

这个 demo 展示两件事：

1. 如何使用 `tablegear` 从数据库表生成代码；
2. 生成后如何在业务代码中使用 `repository + query DSL`。

## 1) 准备示例表

`demo/main.go` 已内置自动建库建表逻辑（MySQL）：

- 自动创建数据库：`demo`
- 自动创建表：`user_account`

你也可以手工执行以下 SQL：

```sql
CREATE TABLE user_account (
  id BIGINT PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  status INT NOT NULL
);
```

## 2) 运行生成命令

在 `golang-tablegear` 目录下执行（以 MySQL 为例）：

```bash
go run ./cmd/tablegear \
  --driver mysql \
  --dsn "user:pass@tcp(127.0.0.1:3306)/demo" \
  --schema demo \
  --module github.com/yujiaao/golang-tablegear \
  --out ./demo/generated \
  --tables user_account
```

执行后会生成：

- `demo/generated/model/user_account.go`
- `demo/generated/query/user_account_query.go`
- `demo/generated/repository/user_account_repository.go`

如果要生成“逻辑 view（联表 SQL）”代码，可增加 `--views-config`：

```bash
go run ./cmd/tablegear \
  --driver mysql \
  --dsn "root@tcp(127.0.0.1:3306)/demo?parseTime=true" \
  --schema demo \
  --module github.com/yujiaao/golang-tablegear/demo/generated \
  --out ./demo/generated \
  --tables user_account \
  --views-config ./demo/views/views.json
```

其中 SQL 文件位于：

- `demo/views/active_user_logic_view.sql`
- `demo/views/user_order_logic_view.sql`（联表示例：user + order）

便于 IDE SQL 高亮与维护。

也可以直接使用 demo 根目录下的统一配置文件（推荐）：

```bash
go run ./cmd/tablegear --config ./demo/tablegear.json
```

## 2.1) 运行时主从配置（读写分离）

`demo` 新增运行时配置文件：`demo/runtime.json`

```json
{
  "adminDsn": "root@tcp(127.0.0.1:3306)/?parseTime=true",
  "masterDsn": "root@tcp(127.0.0.1:3306)/demo?parseTime=true",
  "slaveDsns": [
    "root@tcp(127.0.0.1:3306)/demo?parseTime=true"
  ]
}
```

- 写操作（如 `Insert`）默认走主库
- 查操作（如 `ListByQuery/FindOneByQuery/CountByQuery`）默认走从库
- 可用 `runtime.WithDBRole(ctx, runtime.DBRoleMaster/DBRoleSlave)` 强制路由

## 2.2) 管理接口代码生成

在 `tablegear.json` 里打开以下配置：

- `generateControllers: true`
- `generateRuntimeScaffold: true`
- `controllerTemplate: "std"`（可选：`std` / `gin` / `fiber`）

生成后会额外产出：

- `demo/generated/controller/*.go`：每张表/逻辑 view 对应管理接口注册函数
- `demo/generated/controller/common.go`：路由公共逻辑（支持 `X-DB-Role` 强制主/从）
- `demo/generated/controller/openapi.go`：OpenAPI 基础注释
- `demo/generated/runtimeconfig/runtime_config.go`：`runtime.json` 解析与 `runtime.NewRWDB` 启动脚手架

## 2.3) Swagger 文档（swag init）快速跑通

`controller` 代码已自动包含 Swagger 注释。demo 里额外提供了 `demo/swagger_demo.go`（带 `swagger` build tag）作为文档入口挂载示例。

安装工具并生成文档：

```bash
go install github.com/swaggo/swag/cmd/swag@latest
go get github.com/swaggo/http-swagger
go get github.com/swaggo/files
swag init -g demo/main.go -d demo,demo/generated/controller -o demo/docs
```

以 `swagger` tag 启动（启用 `RegisterSwaggerDocs`）：

```bash
go run -tags swagger ./demo
```

访问地址：

- `http://127.0.0.1:8080/swagger/index.html`

## 3) 在业务代码中使用

示例入口：`demo/main.go`

关键演示点：

- `Insert(ctx, item)`（实体入参）
- `ListByQuery(ctx, q)`（返回 `[]*model.UserAccount`）
- `FindOneByQuery(ctx, q)`（自动扫描到 model）
- `CountByQuery(ctx, q)`（计数）
- `active_user_logic_view` 的逻辑 view 查询（SQL 来自外部 `.sql` 文件）
- `SearchBean` 用法：`ListBySearchBean/CountBySearchBean/FindOneBySearchBean`
- 主从读写分离与强制路由示例
- 管理接口路由注册示例（`RegisterXxxManagerRoutes`）

> 注意：`demo/main.go` 主要用于展示 API 调用方式，实际运行需你本地安装并配置数据库驱动与 DSN。

