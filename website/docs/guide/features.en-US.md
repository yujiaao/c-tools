# Features

Code and configuration examples for TableGear (c-tools / c-tablegen). Samples are from `c-tablegen-jdbc-demo` and `c-tablegen-r2dbc-demo`.

> Maven versions sync from the root `pom.xml` before build. Current: **{{CTOOLS_VERSION}}**.

## Multi-protocol: JDBC / R2DBC / ElasticSearch

```properties
# JDBC
alias=test
className=com.mysql.cj.jdbc.Driver
dburl=jdbc:mysql://127.0.0.1:3306/test
package_name=com.bixuebihui.test
table_list=test_gen,t_edu
```

```properties
# R2DBC
r2dbc=yes
dburl=r2dbc:mysql://127.0.0.1:3306/test
```

```bash
mvn com.github.yujiaao:tablegen-maven-plugin:{{CTOOLS_VERSION}}:gen -DpropPath=tablegen.properties
```

## Layered output

One `tablegen.properties` run produces pojo, dal, business (Manager), dto, converter, service, and controller packages.

## ActiveRecord queries

```java
tEduManager.ar().query(searchCmd).findAllWithCount();
```

## Virtual views (`view_list`)

```properties
view_list=my_test_view: select edu_id from test_gen left join t_edu on test_gen.edu_id=t_edu.id;
```

Generates `view/dal/MyTestViewList` extending `AbstractViewBaseDao`.

## Incremental generation

```properties
generate_all=no
over_write_all=no
```

Uses `target/gen_table_data.cache` and `DbDiff` to regenerate only changed tables.

## Field encryption

```properties
encrypted_fields=test_gen:name;t_edu:degree
```

MapStruct converters get `toDtoDec` / `toDaoEnc` helpers with `EncryptDecryptService`.

## Read/write split

```properties
read_write_separate=yes
alias=test
```

Managers inject `@Qualifier("test") DataSource`.

## R2DBC + Reactor

Managers use `ConnectionFactory`; DAL returns `Mono` / `Flux`.

## BOM (recommended)

```xml
<dependency>
    <groupId>com.github.yujiaao</groupId>
    <artifactId>c-tools-bom</artifactId>
    <version>{{CTOOLS_VERSION}}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

See [Quick start](./getting-started) and [Configuration](./configuration).
