# FAQ

## How to regenerate while preserving custom logic?

**A:** Turn off `over_write_all` and extend in custom subclasses. The generator will only update the `Base` layer code.

For example:

```java
// Generated Base class (will be overwritten)
public class TestGenBase {
    // ...
}

// Custom subclass (will not be overwritten)
public class TestGen extends TestGenBase {
    // Your custom logic
}
```

## How to support multiple databases?

**A:** Use comma-separated property files in `propPath`:

```bash
mvn tablegen:gen -DpropPath=mysql.properties,postgres.properties
```

The plugin will execute each configuration file in order.

## How to troubleshoot generation failures?

**A:** Follow these steps:

1. Check the `target/gen_table_data.cache` file to inspect cache status
2. Check log files in the `logs` directory
3. Enable Maven verbose logging: `mvn tablegen:gen -X`
4. Check database connection permissions and table structure

## How to customize generated code?

**A:** There are several ways:

1. **Template Extension**: Place custom template files (`.hbs` format) in `src/main/resources/templates/jdbc` or `templates/r2dbc` directory. Files with the same name will override default templates.

2. **Inheritance Extension**: Turn off `over_write_all` and add custom methods in subclasses.

3. **Maven Plugin Extension**: Use `AddSourceMethodMojo` in `c-tablegen-mojo` to automatically add methods to specified classes.

## What if incremental generation doesn't work?

**A:** Check the following:

1. Confirm `generate_all=no` is set
2. Check if `target/gen_table_data.cache` file exists
3. If cache is corrupted, delete the cache file and regenerate
4. Ensure database connection is normal and can read table structure

## How to support Spring transaction management?

**A:** If using JDBC connections, you need to get connections from Spring's `DataSourceUtils`:

```java
Connection conn = DataSourceUtils.getConnection(dataSource);
```

And configure a transaction manager:

```xml
<bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>
```

## What dependencies does the generated code use?

**A:** Minimum dependencies include:

- `c-dbtools` - Core utility library
- Database driver (e.g., `mysql-connector-java`)
- Optional: `hibernate-validator` (if annotations are enabled)
- Optional: `swagger-annotations` (if Swagger is enabled)

## Which databases are supported?

**A:** Currently supported:

- ✅ MySQL
- ✅ PostgreSQL
- ✅ H2
- ✅ Derby
- ✅ SQL Server
- ⚠️ Oracle (partial support)
- ✅ Access

## How to generate ElasticSearch mappings?

**A:** Configure `type=elasticsearch_mapping` and set ES-related configurations:

```properties
type=elasticsearch_mapping
url=http://localhost:9200
index_list=my-index-000001
primary_key_fields=biz_id
```

## How to use virtual views?

**A:** Use `view_list` configuration, format is `view_name: SQL statement`:

```properties
view_list=my_view: select id, name from test_table where status = 1;
```

This allows generating corresponding code without creating database views.

## How to configure field encryption?

**A:** Use `encrypted_fields` configuration, format is `table:field1,field2`:

```properties
encrypted_fields=t_encrypt_test:personal_id,phone_number
```

The generated code will automatically handle encryption/decryption of these fields.

## How to configure read-write separation?

**A:** Set `read_write_separate=yes` and provide read/write data sources in Spring configuration:

```java
@Bean
@Qualifier("test_read")
public DataSource readDataSource() {
    // Read data source configuration
}

@Bean
@Qualifier("test_write")
public DataSource writeDataSource() {
    // Write data source configuration
}
```

The generated Manager class will automatically use the corresponding data source.

