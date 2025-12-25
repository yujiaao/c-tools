# Configuration

This document describes the configuration options in the `tablegen.properties` file.

## Data Source Configuration

### Basic Connection Configuration

```properties
# Data source alias, used for Spring @Qualifier
alias=test

# JDBC driver class name
className=com.mysql.cj.jdbc.Driver

# Database connection URL
dburl=jdbc:mysql://127.0.0.1:3306/test

# Database username
username=test

# Database password
password=test123

# Database schema
schema=test

# Table owner (required for some databases)
table_owner=test

# Database catalog
catalog=test
```

### R2DBC Configuration

```properties
# Enable R2DBC mode
r2dbc=yes

# R2DBC connection URL
dburl=r2dbc:mysql://127.0.0.1:3306/test
```

### Read-Write Separation

```properties
# Enable application-level read-write separation
read_write_separate=yes
```

When enabled, the generated Manager class will inject read/write data sources by alias.

## Directory and Package Configuration

```properties
# Base package name
package_name=com.example.test

# Source code directory
src_dir=/src/main/java

# Test code directory
test_dir=/src/test/java

# Resource directory
resource_dir=/src/main/resources

# Service layer directory (defaults to src_dir if not specified)
service_dir=/src/main/java

# Controller layer directory (defaults to src_dir if not specified)
controller_dir=/src/main/java

# DTO directory (defaults to src_dir if not specified)
dto_dir=/src/main/java

# Command directory (defaults to src_dir if not specified)
cmd_dir=/src/main/java
```

## Generation Scope Configuration

### Table List

```properties
# Specify tables to generate (supports regular expressions)
table_list=test_gen,t_edu

# Or use regex
# table_list=test(\w+)

# Excluded table list (higher priority than table_list)
exclude_table_list=PLAN_TABLE,T_SESSION
```

### View List

```properties
# Virtual views (defined by SQL, no need to create views in database)
view_list=my_view: select id, name from test_table where status = 1;
```

### Indexes and Foreign Keys

```properties
# Whether to generate index-related methods
indexes=yes

# Whether to generate foreign key-related methods
foreign_keys=yes
```

## Behavior Switches

```properties
# Whether to use annotations (Jakarta Validation / Swagger)
use_annotation=yes

# Whether to use Swagger annotations
use_swagger=yes

# Swagger version (2 or 3)
swagger_version=3

# Whether to use auto-increment primary key
use_autoincrement=yes

# Whether to generate all (no means incremental mode)
generate_all=yes

# Whether to generate BaseList
generate_baselist=no

# Whether to generate extension tables
kuozhanbiao=no

# Whether to overwrite all files
over_write_all=yes
```

### Incremental Generation

When `generate_all=no`, the system will:

1. Read the `target/gen_table_data.cache` cache file
2. Compare the current database structure with the cache
3. Only trigger generation for tables that have changed

Delete the cache file to trigger full generation.

## Security and Extension Configuration

### Field Encryption

```properties
# Specify fields to encrypt (format: table:field1,field2)
encrypted_fields=t_encrypt_test:personal_id
```

### Ignore Fields

```properties
# Specify fields to ignore (format: table:field1,field2)
ignore_fields=test_gen:age,birth
```

### Interface Inheritance

```properties
# POJO node interface
pojo_node_interface=com.example.Node

# Specify tables to use node interface
pojo_node_interface_list=Business_Category,Forum
```

## ElasticSearch Configuration

```properties
# Data source alias
alias=my_content

# Type: elasticsearch_mapping / elasticsearch_sample / json
type=elasticsearch_mapping

# ES connection URL
url=http://localhost:9200

# Index list
index_list=my-index-000001,all-content-info

# Primary key fields
primary_key_fields=biz_id,global_id

# Ignored fields
ignore_fields=keyword

# Collection fields
collection_fields=play_url
```

## Complete Configuration Examples

### JDBC Configuration Example

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

### R2DBC Configuration Example

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

## Multi-DataSource Configuration

You can use comma-separated property files in `propPath`:

```bash
mvn tablegen:gen -DpropPath=db1.properties,db2.properties
```

The plugin will execute each configuration file in order.

