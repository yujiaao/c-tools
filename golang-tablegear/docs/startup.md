# golang-tablegear Startup 指南

这份文档用于指导你“从零开始为一个新项目生成 ORM 代码并接入使用”。

## 1. 前置条件

- Go 版本：建议 `1.23+`
- 数据库：MySQL 或 PostgreSQL
- 已有目标数据库表（生成器基于 `information_schema` 读取元数据）

## 2. 准备你的新项目

假设你的新项目目录是 `myapp`：

```bash
mkdir myapp
cd myapp
go mod init github.com/your-org/myapp
```

## 3. 选择一种使用方式

你可以二选一：

- **方式 A（推荐）**：在 `golang-tablegear` 仓库里执行生成，把 `--out` 指向你的项目目录
- **方式 B**：把 `cmd/tablegear` 打包成你自己的工具后再在任意目录运行

本文用方式 A 演示。

## 4. 先预览（dry-run）

在 `golang-tablegear` 根目录执行：

```bash
go run ./cmd/tablegear \
  --driver mysql \
  --dsn "root@tcp(127.0.0.1:3306)/your_db?parseTime=true" \
  --schema your_db \
  --module github.com/your-org/myapp \
  --out "D:/path/to/myapp/internal/dbgen" \
  --tables user_account,order_info \
  --dry-run
```

说明：

- `--driver`：`mysql` 或 `postgres`
- `--dsn`：数据库连接串
- `--schema`：MySQL 填数据库名；PostgreSQL 填 schema（常见是 `public`）
- `--module`：你的 Go module 名（用于生成 import）
- `--out`：生成代码输出目录
- `--tables`：逗号分隔，留空表示所有表
- `--dry-run`：仅预览不写文件
- `--views-config`：逻辑 view 配置文件（JSON，引用 `.sql` 文件）

也可以把所有参数收敛到一个配置文件里：

`tablegear.json` 示例：

```json
{
  "driver": "mysql",
  "dsn": "root@tcp(127.0.0.1:3306)/your_db?parseTime=true",
  "schema": "your_db",
  "module": "github.com/your-org/myapp/internal/dbgen",
  "out": "./internal/dbgen",
  "tables": ["user_account", "order_info"],
  "viewsConfig": "./views/views.json",
  "generateControllers": true,
  "generateRuntimeScaffold": true,
  "controllerTemplate": "std",
  "dryRun": false
}
```

执行：

```bash
go run ./cmd/tablegear --config ./tablegear.json
```

## 5. 正式生成

去掉 `--dry-run` 再执行一次：

```bash
go run ./cmd/tablegear \
  --driver mysql \
  --dsn "root@tcp(127.0.0.1:3306)/your_db?parseTime=true" \
  --schema your_db \
  --module github.com/your-org/myapp \
  --out "D:/path/to/myapp/internal/dbgen" \
  --tables user_account,order_info
```

会生成三个目录：

- `model`：实体定义
- `query`：类型安全查询 DSL（字段常量 + 链式条件）
- `repository`：仓储实现（Insert/ListByQuery/FindOneByQuery/CountByQuery）

启用 `generateControllers/generateRuntimeScaffold` 后还会生成：

- `controller`：管理接口注册函数（`RegisterXxxManagerRoutes`）
- `controller/openapi.go`：OpenAPI 注释入口（可配合 swag 使用）
- `runtimeconfig`：`runtime.json` 解析和 `OpenRWDB` 启动模板
- `runtime.json`（位于 `out` 的上级目录）：主从配置样例

管理接口返回结构统一为：

- `code`：业务码（`0` 成功，`40001` 参数错误，`50001` 数据库错误，`50000` 通用错误）
- `message`：错误或成功描述
- `data`：业务数据
- 列表接口 `data` 为 `{ list, total, page }`

## 6. 在新项目里接入运行时

在你的项目里确保依赖可用：

```bash
go get github.com/yujiaao/golang-tablegear
go get github.com/go-sql-driver/mysql
```

初始化 DB 并使用生成代码（示例）：

```go
masterDB, _ := sql.Open("mysql", "root@tcp(127.0.0.1:3306)/your_db?parseTime=true")
slaveDB, _ := sql.Open("mysql", "root@tcp(127.0.0.1:3306)/your_db?parseTime=true")
db := runtime.NewRWDB(masterDB, []*sql.DB{slaveDB}, log.Default(), runtime.DBConfig{})
repo, _ := repository.NewUserAccountRepository(db)

q := query.NewUserAccountQuery()
q.Where(
  corequery.Eq(q.F.Status, 1),
  corequery.Like(q.F.Name, "alice"),
).
  OrderByDesc(q.F.Id).
  Limit(20).
  Offset(0)

items, _ := repo.ListByQuery(ctx, q)
oneQuery := query.NewUserAccountQuery()
oneQuery.Where(corequery.Eq(oneQuery.F.Id, int64(1001)))
one, _ := repo.FindOneByQuery(ctx, oneQuery)
total, _ := repo.CountByQuery(ctx, query.NewUserAccountQuery())

// 强制主库查询（例如读自己的写）
masterCtx := runtime.WithDBRole(ctx, runtime.DBRoleMaster)
_, _ = repo.CountByQuery(masterCtx, query.NewUserAccountQuery())

// 强制从库查询
slaveCtx := runtime.WithDBRole(ctx, runtime.DBRoleSlave)
_, _ = repo.CountByQuery(slaveCtx, query.NewUserAccountQuery())
_ = items
_ = one
_ = total
```

## 7. 常见问题

- **no tables found**
  - 检查 `--schema` 是否正确
  - 检查数据库账号是否有 `information_schema` 读取权限
- **Unknown database**
  - 先创建数据库，或修正 `--dsn`
- **Scan 类型不匹配**
  - 优先检查数据库列类型与生成字段类型映射
- **导入路径错误**
  - 检查 `--module` 是否与目标项目 `go.mod` 完全一致

## 8. 逻辑 view（联表 SQL）生成

可以用 JSON 文件声明逻辑 view，并把 SQL 放在独立 `.sql` 文件里：

`views.json` 示例：

```json
{
  "views": [
    { "name": "active_user_logic_view", "sqlFile": "active_user_logic_view.sql" }
  ]
}
```

`active_user_logic_view.sql` 示例：

```sql
SELECT u.id, u.name, u.status
FROM user_account u
WHERE u.status = 1
```

执行：

```bash
go run ./cmd/tablegear \
  --driver mysql \
  --dsn "root@tcp(127.0.0.1:3306)/your_db?parseTime=true" \
  --schema your_db \
  --module github.com/your-org/myapp/internal/dbgen \
  --out ./internal/dbgen \
  --tables user_account \
  --views-config ./views/views.json
```

逻辑 view 生成结果为只读查询仓储（支持 `ListByQuery/FindOneByQuery/CountByQuery`，不支持写入）。
## 9. 推荐工作流

- 第一次：`--dry-run` 先看输出
- 第二次：正式生成并提交
- 表结构变更后：重新生成并运行 `go test ./...`

