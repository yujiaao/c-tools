# Quick Start

C-Tools is a lightweight Java ORM code generator based on JDBC/R2DBC standards. It can automatically generate layered code including POJO, DAL, Service, Controller, etc. from database tables.

## Prerequisites

- JDK 17+
- Maven 3.8+
- Database driver (MySQL/H2/Derby/PostgreSQL/SQL Server/Oracle partial support)
- Accessible database instance

## Installation

### Maven Dependency

Add the following dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.github.yujiaao</groupId>
    <artifactId>c-dbtools</artifactId>
    <version>1.16.3</version>
</dependency>
```

### Maven Plugin

Add the code generation plugin:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.yujiaao</groupId>
            <artifactId>tablegen-maven-plugin</artifactId>
            <version>1.16.3</version>
            <executions>
                <execution>
                    <id>first-execution</id>
                    <goals>
                        <goal>gen</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <propPath>${project.basedir}/tablegen.properties</propPath>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Quick Start - JDBC {#jdbc}

### 1. Create Configuration File

Create a `tablegen.properties` file in the project root:

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
```

### 2. Run Code Generation

Execute the following command to generate code:

```bash
mvn tablegen:gen
```

Or:

```bash
mvn com.github.yujiaao:tablegen-maven-plugin:1.16.3:gen -DpropPath=tablegen.properties
```

### 3. View Generated Code

The generated code will be located in the `src/main/java` directory, including:

- `pojo/` - POJO entity classes
- `dal/` - Data access layer
- `service/` - Service layer
- `controller/` - Controller layer (if enabled)

## Quick Start - R2DBC {#r2dbc}

### 1. Create Configuration File

Create a `tablegen.properties` file in the project root, add `r2dbc=yes`:

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
```

### 2. Add R2DBC Dependency

```xml
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-mysql</artifactId>
    <version>0.8.0.RELEASE</version>
</dependency>
```

### 3. Run Code Generation

Execute the same Maven command, the generated code will use Reactor API (Mono/Flux).

## Example Projects

You can refer to the following example projects:

- [c-tablegen-jdbc-demo](../----------------------------------c-tablegen-jdbc-demo) - JDBC example
- [c-tablegen-r2dbc-demo](../c-tablegen-r2dbc-demo) - R2DBC example

## Next Steps

- Check [Configuration Guide](./configuration) for detailed configuration options
- Check [FAQ](./faq) to resolve issues

