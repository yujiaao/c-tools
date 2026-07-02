# Quick Start

C-Tools is a lightweight Java ORM code generator based on JDBC/R2DBC standards. It can automatically generate layered code including POJO, DAL, Service, Controller, etc. from database tables.

> Maven versions are synced from the root `pom.xml` before `npm run build` (currently **{{CTOOLS_VERSION}}**). See [Features](./features).

## Prerequisites

- JDK 17+
- Maven 3.8+
- Database driver (MySQL/H2/Derby/PostgreSQL/SQL Server/Oracle partial support)
- Accessible database instance

## Installation

### Recommended: BOM

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

### Maven Plugin

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.yujiaao</groupId>
            <artifactId>tablegen-maven-plugin</artifactId>
            <version>{{CTOOLS_VERSION}}</version>
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

```bash
mvn tablegen:gen
```

Or with explicit plugin coordinates:

```bash
mvn com.github.yujiaao:tablegen-maven-plugin:{{CTOOLS_VERSION}}:gen -DpropPath=tablegen.properties
```

### 3. View Generated Code

The generated code will be located in the `src/main/java` directory, including:

- `pojo/` - POJO entity classes
- `dal/` - Data access layer
- `business/` - Manager for custom extensions
- `service/`, `controller/` - Optional REST layers

## Quick Start - R2DBC {#r2dbc}

Add `r2dbc=yes` and switch the driver/URL to the R2DBC protocol. Generated APIs use `Mono` / `Flux`. See [Features](./features).

## Sample Projects

- [c-tablegen-jdbc-demo](https://github.com/yujiaao/c-tools/tree/main/c-tablegen-jdbc-demo)
- [c-tablegen-r2dbc-demo](https://github.com/yujiaao/c-tools/tree/main/c-tablegen-r2dbc-demo)

## Next Steps

- [Features](./features)
- [Configuration](./configuration)
- [FAQ](./faq)
