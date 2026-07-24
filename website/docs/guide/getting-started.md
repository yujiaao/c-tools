# 快速开始

TableGear（c-tools）面向**表结构驱动**的 Java 项目：连上数据库，跑一条 Maven 命令，即可得到 POJO、DAL、Manager、Service、Controller 全套骨架。

如果你来自 **Hibernate** 或 **MyBatis**，建议先读 [从 Hibernate / MyBatis 而来](./from-orm)，再按本文动手——5 分钟内可以跑通第一个 REST 接口。

> Maven 版本号与根 `pom.xml` 同步（当前 **{{CTOOLS_VERSION}}**）。

---

## 你将得到什么

以表 `t_edu` 为例，生成后的分层与职责：

| 目录 | 作用 | 类比 |
|------|------|------|
| `pojo/` | 与表字段一一对应的实体 | JPA Entity / MyBatis Entity |
| `dal/` | SQL 与 `BaseList`，含字段常量 `F.ID` | Mapper XML + BaseMapper |
| `business/` | `*Manager`，**手写业务的主战场** | 自定义 Mapper 方法 |
| `dto/`、`converter/` | 对外传输与 MapStruct 转换 | 常见 Service 层 DTO |
| `service/`、`controller/` | 分页查询、REST 接口（可选） | 省去样板代码 |

**运行时**只依赖轻量库 `c-dbtools`（ActiveRecord 链式查询），**不引入** Hibernate、MyBatis 引擎。

---

## 先决条件

- JDK 17+
- Maven 3.8+
- 可访问的数据库（MySQL / PostgreSQL / H2 等）
- 可选：Spring Boot 3.x（运行 demo 与 REST 层）

---

## 第一步：引入依赖

推荐用 BOM 锁定版本，与代码生成插件一致：

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

<dependencies>
    <dependency>
        <groupId>com.github.yujiaao</groupId>
        <artifactId>c-dbtools</artifactId>
    </dependency>
</dependencies>
```

添加代码生成插件：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.yujiaao</groupId>
            <artifactId>tablegen-maven-plugin</artifactId>
            <version>{{CTOOLS_VERSION}}</version>
            <configuration>
                <propPath>${project.basedir}/tablegen.properties</propPath>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 第二步：编写 tablegen.properties

在项目根目录创建配置文件（JDBC 示例）：

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
over_write_all=no
```

| 配置项 | 含义 |
|--------|------|
| `alias` | 数据源别名，对应 Spring `@Qualifier` |
| `table_list` | 要生成的表，支持正则 |
| `over_write_all=no` | 保留你在 Manager 里手写的方法 |

---

## 第三步：生成代码

```bash
mvn tablegen:gen
```

等价于：

```bash
mvn com.github.yujiaao:tablegen-maven-plugin:{{CTOOLS_VERSION}}:gen -DpropPath=tablegen.properties
```

生成完成后，在 `business/TEduManager.java` 里编写业务——这是 MyBatis 用户习惯的「Mapper 扩展点」：

```java
@Repository
public class TEduManager extends TEduList {
    public TEduManager(@Qualifier("demo") DataSource ds) {
        super(ds);
    }

    /** 链式条件查询，无需 XML */
    public List<TEdu> listByDegree(String degree) {
        return this.ar().eq(F.DEGREE, degree).findAll();
    }
}
```

Service 层已生成分页查询，可直接注入使用：

```java
Pair<Integer, List<TEduDto>> page = tEduManager.ar()
    .query(searchCmd)
    .findAllWithCount();
```

---

## 第四步：接入 Spring Boot

1. 配置 `DataSource`，bean 名称与 `alias=demo` 一致
2. 组件扫描 `package_name` 包路径
3. 参考 [c-tablegen-jdbc-demo](https://github.com/yujiaao/c-tools/tree/main/c-tablegen-jdbc-demo) 启动应用
4. 若开启 Swagger，访问 `/swagger-ui.html` 调试生成的 REST 接口

---

## R2DBC 快速切换 {#r2dbc}

在**同一张表**上切换响应式栈，只需改连接与一行开关：

```properties
r2dbc=yes
className=io.r2dbc.mysql.MySqlConnectionFactoryProvider
dburl=r2dbc:mysql://127.0.0.1:3306/test
```

重新 `mvn tablegen:gen`，Manager 注入 `ConnectionFactory`，DAL 返回 `Mono` / `Flux`。详见 [核心特性 - R2DBC](./features#r2dbc-reactor-一等公民)。

---

## 表结构变更后怎么办？

```properties
generate_all=no
```

- 首次全量生成后，表结构 diff 写入 `target/gen_table_data.cache`
- 再次执行 `mvn tablegen:gen` 时**只更新有变化的表**
- 你在 `*Manager` 里的手写逻辑不会被覆盖

---

## 示例项目

| 项目 | 适合学习 |
|------|----------|
| [c-tablegen-jdbc-demo](https://github.com/yujiaao/c-tools/tree/main/c-tablegen-jdbc-demo) | JDBC + Swagger + 加密字段 + view_list |
| [c-tablegen-r2dbc-demo](https://github.com/yujiaao/c-tools/tree/main/c-tablegen-r2dbc-demo) | R2DBC + Reactor 批量写入 |

---

## 推荐阅读顺序

1. [从 Hibernate / MyBatis 而来](./from-orm) — 概念对照与迁移策略
2. [核心特性](./features) — 链式查询、虚拟视图、读写分离
3. [配置说明](./configuration) — 全量配置项
4. [常见问题](./faq) — 事务、多数据源、模板定制
