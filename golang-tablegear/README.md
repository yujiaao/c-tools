# golang-tablegear

Go 版本的 tablegear，目标是组合：

- 类 `c-dbtools` 的 ORM 运行时能力
- 类 `c-tablegen` 的元数据驱动代码生成能力
- 吸收 GORM Gen 在类型安全生成方面的设计思想

当前版本（M1+M2）提供：

- `tablegear` CLI 骨架
- 基于 `information_schema` 的表结构读取
- 生成 `model` 与 `repository` 基础代码
- 方言层：MySQL/PostgreSQL 分页与 upsert SQL 拼装
- 运行时 DB 封装：事务辅助、慢 SQL/超慢 SQL 日志、SQL 统计快照
- 运行时读写分离：主从路由（写主读从）+ `context` 强制主/从
- 生成器升级：`repository` 生成 `Insert/ListPage/BuildUpsertSQL` 等方法
- M3 初版 Query DSL：字段常量、链式 `Where/OrderBy/Limit/Offset`、强类型 `EqXxx/LikeXxx`
- 可选生成管理接口与运行时脚手架（支持 `std/gin/fiber`，统一响应体 `code/message/data`，`list+total+page` 分页结构，含 OpenAPI 注释）

## M3 DSL 示例

生成后可使用类似如下方式（以 `User` 为例）：

```go
q := query.NewUserQuery()
q.Where(
  corequery.Eq(q.F.Status, 1),
  corequery.Like(q.F.Name, "alice"),
).
  OrderByDesc(q.F.Id).
  Limit(20).
  Offset(0)

err := userRepo.ListByQuery(ctx, q)
```

## 快速开始

```bash
go run ./cmd/tablegear --help
```

示例（仅预览，不落盘）：

```bash
go run ./cmd/tablegear \
  --driver mysql \
  --dsn "user:pass@tcp(127.0.0.1:3306)/demo" \
  --schema demo \
  --module github.com/acme/demo \
  --out ./generated \
  --tables users,orders \
  --dry-run
```

参考：

- [GORM Gen](https://gorm.io/zh_CN/gen)
- 启动文档：`docs/startup.md`

