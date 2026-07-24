# 核心特性

TableGear 不是「又一个 ORM」，而是 **生成器 + 轻量 JDBC 运行时** 的组合：生成器消灭样板代码，运行时给你 MyBatis 式的 SQL 掌控力，却没有 XML 维护负担。

下文每个特性都附带**与 Hibernate / MyBatis 的对照**，示例来自 `c-tablegen-jdbc-demo`、`c-tablegen-r2dbc-demo`。

> Maven 版本与根 `pom.xml` 同步：**1.17.1.3**

---

## 特性一览

| 特性 | 解决什么问题 | Hibernate 用户 | MyBatis 用户 |
|------|-------------|---------------|-------------|
| 分层代码生成 | CRUD 样板 | 不用写 Repository 接口 | 不用写 Mapper XML |
| ActiveRecord 链式 API | 动态条件 | 替代 Criteria / HQL 拼接 | 替代 XML `<if>` 堆砌 |
| 增量生成 | 表变更同步 | 改 Entity 注解 | 重新 Generator 不覆盖业务 |
| view_list | 复杂 JOIN | Native SQL | 报表 SQL 片段 |
| encrypted_fields | 敏感字段 | `@Convert` 自写 | TypeHandler 自写 |
| read_write_separate | 读写分离 | AbstractRoutingDataSource | 多 SqlSessionFactory |
| R2DBC | 非阻塞 IO | 不支持 | 需另选栈 |

---

## 多协议：JDBC / R2DBC / ElasticSearch

**一套配置骨架，切换协议只改连接信息。**

```properties
# JDBC — 默认，Spring Boot 阻塞式
alias=demo
className=com.mysql.cj.jdbc.Driver
dburl=jdbc:mysql://127.0.0.1:3306/test
package_name=com.bixuebihui.test
table_list=test_gen,t_edu
```

```properties
# R2DBC — 追加 r2dbc=yes，Manager 变为 Reactor API
r2dbc=yes
dburl=r2dbc:mysql://127.0.0.1:3306/test
```

```properties
# ElasticSearch — 独立 es.properties，生成索引 Mapping
type=elasticsearch_mapping
url=http://localhost:9200
index_list=my-index-000001
```

```bash
mvn tablegen:gen
```

MyBatis 用户常问：「换 R2DBC 要重写 Mapper 吗？」——TableGear 重新生成即可，业务仍写在 Manager 扩展方法里。

---

## 分层生成：从一张表到 REST

一次 `tablegen:gen`，按表输出完整流水线：

```
pojo/TEdu.java           ← 表字段 + 类型
dal/TEduList.java        ← insert/update/delete/select SQL
business/TEduManager.java ← 你写业务的地方（Spring @Repository）
dto/ + converter/        ← MapStruct，支持加密字段扩展
service/ + controller/   ← 分页 REST（可选 Swagger）
```

**Service 已接好分页**，对比 MyBatis 需自己拼 PageHelper + Mapper：

```java
@Override
public Pair<Integer, List<TEduDto>> queryList(Paging<TEduSearchCmd> searchCmd) {
    Pair<Long, List<TEdu>> infos = tEduManager.ar()
            .query(searchCmd).findAllWithCount();
    return Pair.of(
        infos.getLeft().intValue(),
        TEduConverter.MAPPER.toDtoList(infos.getRight())
    );
}
```

**类型安全字段名**——告别 MyBatis XML 里的字符串列名：

```java
public static final class F {
    public static final String ID = "id";
    public static final String DEGREE = "degree";
}
// 使用：.eq(F.DEGREE, "本科")  重构友好
```

---

## ActiveRecord 链式查询 {#activerecord-链式查询c-dbtools}

`Manager` 继承 DAL 后，`.ar()` 开启链式条件，语义接近 MyBatis Example，实现走 JDBC：

```java
// 等效于：SELECT * FROM t_edu WHERE degree=? AND id>=? LIMIT 20
tEduManager.ar()
    .eq(F.DEGREE, degree)
    .greaterOrEqualThan(F.ID, minId)
    .limit(0, 20)
    .findAll();

// 分页 + 搜索命令对象（生成器已产出 SearchCmd）
tEduManager.ar().query(searchCmd).findAllWithCount();
```

**对比 Hibernate**：无 Session、无懒加载、无 N+1 代理——调试时看到的就是 JDBC。

**对比 MyBatis**：简单条件不用开 IDE 改 XML；复杂报表仍可用 `view_list` 或 Manager 内 raw SQL。

支持 `eq`、`in`、`ignoreBlank`（空参数自动忽略）、JSON 函数等，详见 `c-dbtools` 测试用例。

---

## 虚拟视图 view_list

**没有建数据库视图，也能生成「只读模型」代码。**

```properties
view_list=my_test_view: select edu_id from test_gen left join t_edu on test_gen.edu_id=t_edu.id;
```

产出 `view/dal/MyTestViewList`，继承 `AbstractViewBaseDao`，分层与普通表一致。

MyBatis 用户：这相当于**把一段固定 JOIN SQL 固化成可复用的 Dao**，且随生成器增量更新字段列表。

---

## 增量生成

```properties
generate_all=no
over_write_all=no
```

| 步骤 | 行为 |
|------|------|
| 首次生成 | 写入 `target/gen_table_data.cache` |
| 表加列/改类型 | diff 后**只重新生成该表** |
| 你在 Manager 里的方法 | **保留** |

Hibernate 改表要动 Entity + 有时动查询；MyBatis Generator 全量覆盖风险高——TableGear 的 diff 策略专为**长期演进的项目**设计。

---

## 字段加解密

```properties
encrypted_fields=test_gen:name;t_edu:degree
```

生成 MapStruct Converter 扩展方法，注入 `EncryptDecryptService`：

```java
default TEduDto toDtoDec(TEdu info, EncryptDecryptService svc) {
    TEduDto res = toDto(info);
    List<String> plain = svc.decrypt(strCollector(info));
    strSettersDto(res, plain, 0);
    return res;
}
```

对比 Hibernate `@Convert`、MyBatis `TypeHandler`：声明式配置表字段，生成器统一产出加解密入口。

---

## 读写分离

```properties
read_write_separate=yes
alias=demo
```

Manager 通过 `@Qualifier("demo")` 注入数据源；Spring 侧配置读/写两个 DataSource 即可，生成代码无侵入：

```java
@Repository
public class TEduManager extends TEduList {
    public TEduManager(@Qualifier("demo") DataSource ds) {
        super(ds);
    }
}
```

---

## R2DBC：Reactor 一等公民 {#r2dbc-reactor-一等公民}

`r2dbc=yes` 后，同一套表生成响应式 API：

```java
public Mono<Integer> batchInsert() {
    return Flux.fromIterable(names)
        .flatMap(name -> {
            TestGen row = new TestGen();
            row.setName(name);
            return this.insertAutoIncrement(row);
        })
        .map(ok -> ok ? 1 : 0)
        .reduce(Integer::sum);
}
```

Hibernate Reactive / MyBatis 原生均非 JDBC 生态标配；TableGear 与 Spring WebFlux 同栈时切换成本最低。

---

## Maven 依赖（BOM）

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.github.yujiaao</groupId>
            <artifactId>c-tools-bom</artifactId>
            <version>{{CTOOLS_VERSION}}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

## 示例项目

| 项目 | 说明 |
|------|------|
| [c-tablegen-jdbc-demo](https://github.com/yujiaao/c-tools/tree/main/c-tablegen-jdbc-demo) | 完整 Spring Boot + Swagger + 加密 + view_list |
| [c-tablegen-r2dbc-demo](https://github.com/yujiaao/c-tools/tree/main/c-tablegen-r2dbc-demo) | Reactor 批量写入 |

[从 Hibernate / MyBatis 而来](./from-orm) · [快速开始](./getting-started) · [配置说明](./configuration)
