# 配置说明

`tablegen.properties` 是 TableGear 的单一配置入口：连接哪张库、生成哪些表、产出哪些层、是否增量/加密/读写分离，都在这里声明。

**建议阅读顺序**：先完成 [快速开始](./getting-started) 跑通最小配置，再按需查阅本文。从 Hibernate / MyBatis 迁移的用户可对照 [概念指南](./from-orm) 理解各配置项的设计意图。

---

## 数据源配置

### 基本连接配置

```properties
# 数据源别名，用于 Spring @Qualifier
alias=test

# JDBC 驱动类名
className=com.mysql.cj.jdbc.Driver

# 数据库连接 URL
dburl=jdbc:mysql://127.0.0.1:3306/test

# 数据库用户名
username=test

# 数据库密码
password=test123

# 数据库 Schema
schema=test

# 表所有者（某些数据库需要）
table_owner=test

# 数据库 Catalog
catalog=test
```

### R2DBC 配置

```properties
# 启用 R2DBC 模式
r2dbc=yes

# R2DBC 连接 URL
dburl=r2dbc:mysql://127.0.0.1:3306/test
```

### 读写分离

```properties
# 启用应用层读写分离
read_write_separate=yes
```

启用后，生成的 Manager 类将按别名注入读/写数据源。

## 目录与包名配置

```properties
# 基础包名
package_name=com.example.test

# 源代码目录
src_dir=/src/main/java

# 测试代码目录
test_dir=/src/test/java

# 资源目录
resource_dir=/src/main/resources

# Service 层目录（缺省时使用 src_dir）
service_dir=/src/main/java

# Controller 层目录（缺省时使用 src_dir）
controller_dir=/src/main/java

# DTO 目录（缺省时使用 src_dir）
dto_dir=/src/main/java

# Command 目录（缺省时使用 src_dir）
cmd_dir=/src/main/java
```

## 生成范围配置

### 表列表

```properties
# 指定要生成的表（支持正则表达式）
table_list=test_gen,t_edu

# 或者使用正则
# table_list=test(\w+)

# 排除的表列表（优先级高于 table_list）
exclude_table_list=PLAN_TABLE,T_SESSION
```

### 视图列表

```properties
# 虚拟视图（通过 SQL 定义，无需在数据库中创建视图）
view_list=my_view: select id, name from test_table where status = 1;
```

### 索引和外键

```properties
# 是否生成索引相关方法
indexes=yes

# 是否生成外键相关方法
foreign_keys=yes
```

## 行为开关

```properties
# 是否使用注解（Jakarta Validation / Swagger）
use_annotation=yes

# 是否使用 Swagger 注解
use_swagger=yes

# Swagger 版本（2 或 3）
swagger_version=3

# 是否使用自增主键
use_autoincrement=yes

# 是否全量生成（no 时进入增量模式）
generate_all=yes

# 是否生成 BaseList
generate_baselist=no

# 是否生成扩展表
kuozhanbiao=no

# 是否覆盖所有文件
over_write_all=yes
```

### 增量生成

当 `generate_all=no` 时，系统会：

1. 读取 `target/gen_table_data.cache` 缓存文件
2. 对比数据库当前结构与缓存
3. 仅对发生变化的表触发生成

删除缓存文件可触发全量生成。

## 安全与扩展配置

### 字段加密

```properties
# 指定需要加密的字段（格式：表名:字段1,字段2）
encrypted_fields=t_encrypt_test:personal_id
```

### 忽略字段

```properties
# 指定要忽略的字段（格式：表名:字段1,字段2）
ignore_fields=test_gen:age,birth
```

### 接口继承

```properties
# POJO 节点接口
pojo_node_interface=com.example.Node

# 指定表使用节点接口
pojo_node_interface_list=Business_Category,Forum
```

## ElasticSearch 配置

```properties
# 数据源别名
alias=my_content

# 类型：elasticsearch_mapping / elasticsearch_sample / json
type=elasticsearch_mapping

# ES 连接 URL
url=http://localhost:9200

# 索引列表
index_list=my-index-000001,all-content-info

# 主键字段
primary_key_fields=biz_id,global_id

# 忽略的字段
ignore_fields=keyword

# 集合字段
collection_fields=play_url
```

## 完整配置示例

### JDBC 配置示例

```properties
alias=test
className=com.mysql.cj.jdbc.Driver
dburl=jdbc:mysql://127.0.0.1:3306/test
username=test
password=test123
schema=test
package_name=com.example.test
src_dir=/src/main/java
table_list=test_gen,t_edu
exclude_table_list=PLAN_TABLE
use_annotation=yes
use_swagger=yes
swagger_version=3
generate_all=yes
over_write_all=no
read_write_separate=yes
indexes=yes
foreign_keys=yes
```

### R2DBC 配置示例

```properties
alias=test
r2dbc=yes
className=io.r2dbc.mysql.MySqlConnectionFactoryProvider
dburl=r2dbc:mysql://127.0.0.1:3306/test
username=test
password=test123
schema=test
package_name=com.example.test
src_dir=/src/main/java
table_list=test_gen,t_edu
use_annotation=yes
generate_all=yes
```

## 多数据源配置

可以在 `propPath` 中使用逗号分隔多个属性文件：

```bash
mvn tablegen:gen -DpropPath=db1.properties,db2.properties
```

插件会按顺序执行每个配置文件。

