# 常见问题

## 如何重新生成但保留手写逻辑？

**A:** 关闭 `over_write_all` 并在自定义子类中扩展，生成器会只更新 `Base` 层代码。

例如：

```java
// 生成的 Base 类（会被覆盖）
public class TestGenBase {
    // ...
}

// 自定义子类（不会被覆盖）
public class TestGen extends TestGenBase {
    // 你的自定义逻辑
}
```

## 如何支持多数据库？

**A:** 在 `propPath` 中使用逗号分隔多个属性文件：

```bash
mvn tablegen:gen -DpropPath=mysql.properties,postgres.properties
```

插件会按顺序执行每个配置文件。

## 生成失败如何定位？

**A:** 按以下步骤排查：

1. 查看 `target/gen_table_data.cache` 文件，检查缓存状态
2. 查看 `logs` 目录下的日志文件
3. 开启 Maven 详细日志：`mvn tablegen:gen -X`
4. 检查数据库连接权限和表结构

## 如何自定义生成的代码？

**A:** 有以下几种方式：

1. **模板扩展**：在 `src/main/resources/templates/jdbc` 或 `templates/r2dbc` 目录下放置自定义模板文件（`.hbs` 格式），同名文件会覆盖默认模板

2. **继承扩展**：关闭 `over_write_all`，在子类中添加自定义方法

3. **Maven 插件扩展**：使用 `c-tablegen-mojo` 中的 `AddSourceMethodMojo` 自动向指定类添加方法

## 增量生成不工作怎么办？

**A:** 检查以下几点：

1. 确认 `generate_all=no` 已设置
2. 检查 `target/gen_table_data.cache` 文件是否存在
3. 如果缓存损坏，删除缓存文件重新生成
4. 确保数据库连接正常，可以读取表结构

## 如何支持 Spring 事务管理？

**A:** 如果使用 JDBC 连接，需要从 Spring 的 `DataSourceUtils` 获取连接：

```java
Connection conn = DataSourceUtils.getConnection(dataSource);
```

并配置事务管理器：

```xml
<bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>
```

## 生成的代码使用了哪些依赖？

**A:** 最小依赖包括：

- `c-dbtools` - 核心工具库
- 数据库驱动（如 `mysql-connector-java`）
- 可选：`hibernate-validator`（如果启用注解）
- 可选：`swagger-annotations`（如果启用 Swagger）

## 支持哪些数据库？

**A:** 目前支持：

- ✅ MySQL
- ✅ PostgreSQL
- ✅ H2
- ✅ Derby
- ✅ SQL Server
- ⚠️ Oracle（部分支持）
- ✅ Access

## 如何生成 ElasticSearch 映射？

**A:** 配置 `type=elasticsearch_mapping` 并设置 ES 相关配置：

```properties
type=elasticsearch_mapping
url=http://localhost:9200
index_list=my-index-000001
primary_key_fields=biz_id
```

## 虚拟视图如何使用？

**A:** 使用 `view_list` 配置，格式为 `视图名: SQL语句`：

```properties
view_list=my_view: select id, name from test_table where status = 1;
```

这样可以在不创建数据库视图的情况下生成对应的代码。

## 如何配置字段加密？

**A:** 使用 `encrypted_fields` 配置，格式为 `表名:字段1,字段2`：

```properties
encrypted_fields=t_encrypt_test:personal_id,phone_number
```

生成的代码会自动处理这些字段的加密/解密。

## 读写分离如何配置？

**A:** 设置 `read_write_separate=yes`，并在 Spring 配置中提供读/写两个数据源：

```java
@Bean
@Qualifier("test_read")
public DataSource readDataSource() {
    // 读数据源配置
}

@Bean
@Qualifier("test_write")
public DataSource writeDataSource() {
    // 写数据源配置
}
```

生成的 Manager 类会自动使用对应的数据源。

