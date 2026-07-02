# 从 Hibernate / MyBatis 而来

如果你已经在用 Hibernate 或 MyBatis，TableGear（c-tools）不会要求你放弃「表结构优先」或「SQL 可控」的习惯——它把**重复劳动交给生成器**，把**业务判断留给你**。

> 一句话：**TableGear = 按表生成完整分层代码 + 轻量 JDBC 运行时**。没有 SessionFactory，没有 XML/注解 SQL 维护地狱，但保留链式条件与类型安全字段名。

---

## 30 秒建立心智模型

| 你现在的习惯 | TableGear 的对应物 |
|-------------|-------------------|
| Entity / `@Table` | `pojo/` 下生成的 POJO |
| Repository / Mapper 接口 | `dal/` + `business/*Manager` |
| `@Query` / Mapper XML 里的 CRUD | 生成器写好，`Manager.ar()` 补复杂查询 |
| Service 层 | `service/autogenerate/`（可扩展） |
| Controller | 可选生成 `controller/autogenerate/` |
| MyBatis `Example` / Hibernate Criteria | `c-dbtools` 的 **ActiveRecord 链式 API** |

**关键区别**：框架不替你「猜 SQL」，而是**从数据库元信息生成可读的 Java + JDBC**，你始终能看到最终 SQL 走向，调试成本接近手写 JDBC，开发效率接近 MyBatis Generator + 一套统一运行时。

---

## 对比 Hibernate

### 你可能在 Hibernate 里这样写

```java
@Entity
public class TEdu {
    @Id
    private Integer id;
    private String degree;
}

// 查询
session.createQuery("from TEdu where degree = :d", TEdu.class)
       .setParameter("d", "本科")
       .list();
```

### 在 TableGear 里

**POJO 与 DAL 由生成器产出**，你在 Manager 里写业务：

```java
@Repository
public class TEduManager extends TEduList {
    public TEduManager(@Qualifier("test") DataSource ds) {
        super(ds);
    }

    public List<TEdu> findByDegree(String degree) {
        return this.ar()
            .eq(F.DEGREE, degree)
            .findAll();
    }
}
```

字段名用 `F.DEGREE` 常量，重构时 IDE 可追踪，不依赖字符串魔法。

### 为什么很多团队从 Hibernate 换过来

| 痛点（Hibernate） | TableGear 的做法 |
|------------------|-----------------|
| 懒加载、N+1、Session 生命周期难控 | 纯 JDBC，无代理实体 |
| HQL/JPQL 与真实 SQL 有落差 | 链式 API 直接映射 SQL 条件 |
| 改表后要同步 Entity 注解 | `mvn tablegen:gen`，增量只改变更表 |
| 启动慢、依赖重 | 核心仅 `c-dbtools`，无 ORM 引擎 |
| 复杂报表 SQL 还是要 native query | 本来就走 SQL 思维，`view_list` 支持 SQL 片段 |

Hibernate 适合**对象模型驱动**的领域；TableGear 适合**表结构驱动、CRUD 密集、要掌控 SQL** 的业务系统——这正是大量 Spring + MyBatis 项目的真实画像。

---

## 对比 MyBatis

### 你可能在 MyBatis 里这样写

```xml
<!-- TEduMapper.xml -->
<select id="selectByDegree" resultType="TEdu">
  SELECT id, degree FROM t_edu WHERE degree = #{degree}
</select>
```

```java
public interface TEduMapper {
    List<TEdu> selectByDegree(@Param("degree") String degree);
}
```

### 在 TableGear 里

**单表 CRUD 不用写 XML**。生成器已产出 `TEduList` 的 insert/update/delete/select；复杂条件用链式 API：

```java
tEduManager.ar()
    .eq(F.DEGREE, "本科")
    .greaterOrEqualThan(F.ID, 100)
    .limit(0, 20)
    .findAll();
```

分页与搜索命令对象（`TEduSearchCmd` + `Paging`）在 demo 里已生成，Controller 直接接：

```java
@PostMapping("/list")
public BaseModel<PageModel<TEduDto>> list(
        @RequestParam Long maxRows,
        @RequestParam Long page,
        TEduSearchCmd searchCmd) {
    Paging<TEduSearchCmd> paging = Paging.of(page, maxRows, searchCmd);
    Pair<Integer, List<TEduDto>> result = tEduService.queryList(paging);
    // ...
}
```

### 与 MyBatis Generator 的差异

| 维度 | MyBatis Generator | TableGear |
|------|-------------------|-----------|
| 产出物 | Entity + Mapper + XML | POJO + DAL + Manager + DTO + Service + Controller |
| 复杂查询 | 继续手写 XML | `Manager.ar()` 链式 + 可扩展模板 |
| 表变更 | 重新生成，易覆盖 | `generate_all=no` 增量 diff |
| 多数据源 | 多套 Mapper 配置 | 多个 `tablegen.properties`，`alias` 区分 |
| 响应式 | 需另选方案 | 同一配置，`r2dbc=yes` 生成 Reactor API |

**迁移建议**：把原 Mapper 里「纯 CRUD + 简单条件」删掉，改写在 `*Manager`；保留在 XML 里的**极复杂报表 SQL**，可逐步迁到 `view_list` 或 Manager 自定义方法。

---

## 代码写在哪里？（扩展点）

这是从 Hibernate/MyBatis 转来最关心的问题：

```
生成器会覆盖          你应该手写
─────────────────────────────────────
pojo/  dal/           business/*Manager.java  ← 业务入口
service/autogenerate/  自定义 Service 实现类
controller/autogenerate/  按需改路由或另写 Controller
```

推荐模式：

```properties
over_write_all=no
generate_all=no
```

- 表结构变了 → 跑 `mvn tablegen:gen`，只更新生成层
- 业务逻辑始终在 **Manager** 或自定义 Service 子类，不被覆盖

---

## 五条命令跑通第一个表

```bash
# 1. 添加依赖与 tablegen 插件（见快速开始）
# 2. 编写 tablegen.properties
# 3. 生成
mvn tablegen:gen

# 4. 启动 Spring Boot（参考 c-tablegen-jdbc-demo）
# 5. 访问 Swagger：/swagger-ui.html
```

最小 `tablegen.properties`：

```properties
alias=demo
className=com.mysql.cj.jdbc.Driver
dburl=jdbc:mysql://127.0.0.1:3306/test
username=test
password=test123
schema=test
package_name=com.example.demo
src_dir=src/main/java
table_list=t_edu
use_annotation=yes
use_swagger=yes
```

---

## 什么时候仍不适合 TableGear？

诚实地说，以下场景请继续用 Hibernate / 手写 MyBatis：

- 领域模型与表结构**严重脱节**（大量继承、嵌入对象），且团队强依赖 JPA 生态
- 纯 NoSQL，无稳定表元信息
- 已有巨大 MyBatis XML 资产，短期无重构预算

以下场景**非常适合**：

- Spring Boot 单体 / 微服务，表多、CRUD 多
- 需要 JDBC 与 R2DBC 双栈
- 要读写分离、字段加解密、虚拟视图，但不想堆框架
- 受够 MyBatis Generator 只生成 Mapper、其余全手写

---

## 下一步

- [快速开始](./getting-started) — 安装与首次生成
- [核心特性](./features) — 链式查询、增量生成、加密字段等
- [配置说明](./configuration) — `tablegen.properties` 全量参考
- [c-tablegen-jdbc-demo](https://github.com/yujiaao/c-tools/tree/main/c-tablegen-jdbc-demo) — 可运行的完整示例
