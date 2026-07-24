# 常见问题

## 从 Hibernate 迁移，Session 和懒加载怎么处理？

TableGear **没有 Session 与实体代理**。每次查询通过 JDBC 得到 detached POJO，行为与 MyBatis 一致。需要关联数据时：

- 单表：用 `Manager.ar()` 链式查询
- 多表 JOIN：用 `view_list` 生成只读视图，或在 Manager 里组合多次查询
- 避免 N+1：在 Manager 层显式 batch 查询，不依赖 ORM 自动 fetch

## 从 MyBatis 迁移，原来的 XML 怎么办？

建议分阶段：

1. **单表 CRUD** — 删除 XML，改用生成的 DAL + Manager
2. **动态条件** — 用 `ar().eq().in().ignoreBlank()` 替代 `<if>` 片段
3. **复杂报表 SQL** — 暂保留 XML 或迁入 `view_list`，逐步收拢到 Manager

`over_write_all=no` 保证你手写代码不被生成器覆盖。

## 和 MyBatis Generator 比，生成范围差在哪？

MyBatis Generator 通常只产出 Entity + Mapper + XML。TableGear 额外生成 **Manager（扩展点）、DTO、Converter、Service、Controller**，并内置分页与 Swagger 注解，更接近「可运行的垂直切片」。

## 和 Hibernate 比，事务怎么用？

使用 Spring 声明式事务即可。JDBC 模式下请确保从 Spring 管理的 `DataSource` 取连接：

```java
Connection conn = DataSourceUtils.getConnection(dataSource);
```

配置 `DataSourceTransactionManager` 后，Manager 内的多个 DAL 操作可处于同一事务。

---

## 如何重新生成但保留手写逻辑？

关闭 `over_write_all`，业务写在 `business/*Manager` 或自定义 Service 子类：

```java
@Repository
public class TEduManager extends TEduList {
    // 生成器不会删除此类中的自定义方法
    public List<TEdu> myCustomQuery() { ... }
}
```

配合 `generate_all=no` 可只对 schema 变更的表重新生成。

## 如何支持多数据库？

`propPath` 逗号分隔多个属性文件：

```bash
mvn tablegen:gen -DpropPath=mysql.properties,postgres.properties
```

每个文件用不同 `alias` 区分数据源。

## 生成失败如何定位？

1. 检查 `target/gen_table_data.cache`
2. 查看 Maven 日志：`mvn tablegen:gen -X`
3. 确认数据库账号有读表结构权限

## 如何自定义生成的代码？

1. **模板覆盖**：在 `src/main/resources/templates/jdbc` 放置同名 `.hbs`
2. **Manager 扩展**：推荐，业务逻辑集中在此
3. **c-tablegen-mojo**：`AddSourceMethodMojo` 用 JavaParser 向类追加方法

## 增量生成不工作怎么办？

确认 `generate_all=no`，且 `target/gen_table_data.cache` 存在。缓存损坏时删除后全量重生。

## 生成的代码依赖什么？

- **必需**：`c-dbtools`、数据库驱动
- **可选**：`hibernate-validator`（`use_annotation=yes`）、SpringDoc（`use_swagger=yes`）、MapStruct（DTO 转换）

## 支持哪些数据库？

MySQL、PostgreSQL、H2、Derby、SQL Server、Access；Oracle 部分支持。

## 虚拟视图 view_list 是什么？

不建 DB 视图，用 SQL 片段声明只读模型并生成完整分层代码：

```properties
view_list=order_summary: select o.id, u.name from orders o join users u on o.user_id=u.id;
```

## 字段加密与读写分离？

```properties
encrypted_fields=user:phone,id_card
read_write_separate=yes
```

详见 [核心特性](./features)。

---

更多概念说明：[从 Hibernate / MyBatis 而来](./from-orm) · [配置说明](./configuration)
