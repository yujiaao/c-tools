# 快速开始

C-Tools 是一个基于 JDBC/R2DBC 标准的轻量级 Java ORM 代码生成工具。它可以从数据库表自动生成 POJO、DAL、Service、Controller 等分层代码。

## 先决条件

- JDK 17+
- Maven 3.8+
- 数据库驱动（MySQL/H2/Derby/PostgreSQL/SQL Server/Oracle 部分支持）
- 可访问的数据库实例

## 安装

### Maven 依赖

在项目的 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.github.yujiaao</groupId>
    <artifactId>c-dbtools</artifactId>
    <version>1.16.3</version>
</dependency>
```

### Maven 插件

添加代码生成插件：

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

## 快速开始 - JDBC {#jdbc}

### 1. 创建配置文件

在项目根目录创建 `tablegen.properties` 文件：

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

### 2. 运行代码生成

执行以下命令生成代码：

```bash
mvn tablegen:gen
```

或者：

```bash
mvn com.github.yujiaao:tablegen-maven-plugin:1.16.3:gen -DpropPath=tablegen.properties
```

### 3. 查看生成的代码

生成的代码将位于 `src/main/java` 目录下，包括：

- `pojo/` - POJO 实体类
- `dal/` - 数据访问层
- `service/` - 服务层
- `controller/` - 控制器层（如果启用）

## 快速开始 - R2DBC {#r2dbc}

### 1. 创建配置文件

在项目根目录创建 `tablegen.properties` 文件，添加 `r2dbc=yes`：

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

### 2. 添加 R2DBC 依赖

```xml
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-mysql</artifactId>
    <version>0.8.0.RELEASE</version>
</dependency>
```

### 3. 运行代码生成

执行相同的 Maven 命令，生成的代码将使用 Reactor API（Mono/Flux）。

## 示例项目

可以参考以下示例项目：

- [c-tablegen-jdbc-demo](../c-tablegen-jdbc-demo) - JDBC 示例
- [c-tablegen-r2dbc-demo](../c-tablegen-r2dbc-demo) - R2DBC 示例

## 下一步

- 查看[配置说明](./configuration)了解详细的配置选项
- 查看[常见问题](./faq)解决遇到的问题

