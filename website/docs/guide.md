---
marp: true
theme: gaia
footer: "c-tools"
paginate: true
style: "section a { font-size: 40px; }"
---
<!--
_class: lead gaia
_paginate: false
_backgroundColor: black
-->

## c-tools / c-tablegen 代码生成指引
![invert opacity:.9 w:400](./assets/logo.svg)
#### xwx@live.cn
2025-11-26

---
# 目录

* 项目概览
* 核心能力与架构
* 快速起步
* 配置全解
* 模板与扩展
* 常见问题

---
# 项目概览
## 什么是 c-tablegen？

* c-tools 家族的核心代码生成器，负责 JDBC/R2DBC/ES 多种目标代码产出
* 通过数据库元信息生成 pojo、dal、service、controller 等分层代码
* 结合缓存与差异比对支持增量生成，避免覆盖手写逻辑
* 与 family 中的 c-cache、c-dbtools、c-estools 等模块协作，生成后的代码可直接调用这些运行时库

---
# 核心能力

* **多协议**：同一套属性配置即可生成 JDBC、Project Reactor (R2DBC) 以及 ElasticSearch 访问层
* **分层模板**：`Generator[]` 以数组形式串联 pojo/dal/business/dto/service/controller 输出，单次配置即可全栈产出
* **增量 & 缓存**：`generate_all=no` 时自动读取 `target/gen_table_data.cache`，仅对比发生变化的表
* **虚拟视图**：`view_list` 支持 SQL 片段，适合未在数据库落表的场景
* **读写分离与加解密**：通过 `read_write_separate` 和 `encrypted_fields` 实现拓展
* **ElasticSearch Schema**：当检测到 ES 配置时，会转向索引级模板批量生成 JSON 映射

---
# 架构速览

1. **配置解析**：`ProjectConfig.readFrom` 解析 `tablegen.properties`，注册路径、命名、接口扩展等参数
2. **元数据收集**：`TableGen` 连接数据库（HikariCP/BitmechanicDataSource），抓取表、列、索引、外键
3. **生成调度**：`GenerateAll` 将 `Generator` 分为按表、按视图、按索引及一次性生成四类流水线
4. **增量比对**：若未全量生成，则通过 `DbDiff` 对比缓存与当前 schema，只对差异表触发生成器
5. **产物落地**：模板存放于 `src/main/resources/templates/{jdbc,r2dbc,es}`，按需输出 Java/JSON 文件

---
# 先决条件

* JDK 17+，Maven 3.8+
* 数据库驱动（MySQL/H2/Derby/Postgres/SQLServer/Oracle partial）及可访问的实例
* 可选：ElasticSearch 8.x+，Node/npm 用于 VS Code 插件 (lsp-tablegear) 体验
* 推荐 IDE：IntelliJ IDEA / VS Code，启用 Lombok 与 Jakarta EE 依赖

---
# 快速起步（JDBC）

1. 复制 `c-tablegen-jdbc-demo`
2. 配置 `tablegen.properties`
3. 运行 `mvn com.github.yujiaao:tablegen-maven-plugin:1.15.12.7:gen -DpropPath=tablegen.properties`
4. 查看 `src/main/java` 下生成的 pojo、dal、service、controller 目录
5. 若需要 REST 骨架，可结合 `c-tablegen-mojo` 自定义方法或在 `templates/jdbc` 中拓展

---
# 快速起步（R2DBC / Reactor）

1. 复制 `c-tablegen-r2dbc-demo`
2. 在配置中加入 `r2dbc=yes`
3. 执行同样的 Maven 命令，生成的 `baselist`, `dal`, `business` 将引用 Reactor API
4. 运行 demo 中的 Spring Boot 应用验证非阻塞数据访问

---
# 快速起步（ElasticSearch）

1. 使用 `es.properties`
2. `type=elasticsearch_mapping` / `elasticsearch_sample` / `json` 控制输出 JSON 映射或示例数据
3. `index_list` 支持多索引配置，`primary_key_fields` 对应 `_id`
4. 运行 `GenerateAll` 时检测到 ES 配置会直接切换到索引生成流程

---
# 配置 - 数据源

```properties
alias=test
className=com.mysql.cj.jdbc.Driver
dburl=jdbc:mysql://127.0.0.1:3306/test
username=test
password=test123
schema=test
table_owner=test
catalog=test
read_write_separate=yes
```
* `alias` 会成为 Spring `@Qualifier` 数据源别名
* `read_write_separate` 会在生成的 Manager 中按别名注入读/写数据源

---
# 配置 - 目录与包名

```properties
package_name=com.bixuebihui.test
src_dir=/src/main/java
test_dir=/src/test/java
resource_dir=/src/main/resources
service_dir=/src/main/java
controller_dir=/src/main/java
dto_dir=/src/main/java
cmd_dir=/src/main/java
```
* 配置项会在 `ProjectConfig.readFrom` 中拼接成绝对路径
* 目录缺省时回退到 `src_dir`

---
# 配置 - 生成范围

```properties
#table_list=test(\w+)
table_list=test_gen,t_edu
exclude_table_list=PLAN_TABLE,T_SESSION
over_write_all=yes
indexes=no
foreign_keys=yes
view_list=my_view: select ...;
```
* 支持正则，`exclude_table_list` 优先级更高
* `indexes=yes` 会为每个索引生成 `retrieveByIndex` / `updateByIndex`
* `view_list` 允许通过 SQL 字符串定义“虚拟视图”

---
# 配置 - 行为开关

```properties
use_annotation=yes
use_swagger=yes
swagger_version=3
use_autoincrement=yes
generate_all=yes
generate_baselist=no
kuozhanbiao=no
```
* `generate_all=no` 将进入增量模式，依赖本地缓存文件
* `use_annotation` 打开 Jakarta Validation / Swagger 注解

---
# 配置 - 安全与扩展

```properties
encrypted_fields=t_encrypt_test:personal_id
ignore_fields=test_gen:age,birth
pojo_node_interface=com.example.Node
pojo_node_interface_list=Business_Category,Forum
```
* `encrypted_fields` & `ignore_fields` 通过 `table:field1,field2` 声明
* `pojo_*_interface_list` 允许按表/视图精细控制接口继承

---
# 配置 - ElasticSearch

```properties
alias=my_content
type=elasticsearch_mapping
url=http://localhost:9200
index_list=my-index-000001,all-content-info
primary_key_fields=biz_id,global_id
ignore_fields=keyword
collection_fields=play_url
```
* `type=json` 可直接导出实体示例
* `collection_fields` 用于提示框架将字段视为集合类型

---
# 模板目录

* `templates/jdbc`: 面向经典阻塞式 DAO（pojo/dal/business/dto/service/controller）
* `templates/r2dbc`: Reactor 友好模板，生成 `Mono`/`Flux`
* `templates/es`: 生成 ES Mappings、Sample JSON
* 可复制模板文件并以同名覆盖，以便在项目中自定义注释或代码结构

---
# 增量生成与缓存

* `target/gen_table_data.cache` 持久化表结构快照
* 当 `generate_all=no` 时，`GenerateAll` 使用 `DbDiff` 比对数据库与缓存，仅对变更表调用各 `Generator`
* 删除缓存即可触发全量回写（适合大版本升级）

---
# 扩展方式

* 自定义 Maven 插件：`c-tablegen-mojo` 中的 `AddSourceMethodMojo` 利用 JavaParser 自动向指定类添加方法
* 模板扩展：在资源目录新增 `.hbs` 文件即可覆盖默认模板
* 管理元表：`InstallMetaTable` 可在数据库内安装 `t_metatable` / `t_metacolumn` 字典，实现字段描述统一维护

---
# 常见问题

**Q:** 如何重新生成但保留手写逻辑？
**A:** 关闭 `over_write_all` 并在自定义子类中扩展，生成器会只更新 `Base` 层代码。

**Q:** 如何支持多数据库？
**A:** 在 `propPath` 中使用逗号分隔多个属性文件，插件会顺序执行。

**Q:** 生成失败如何定位？
**A:** 查看 `target/gen_table_data.cache` 与 `logs`，必要时开启 `-X` Maven log 并检查数据库权限。

---
# Q & A

![bg contain](./assets/logo.svg)
