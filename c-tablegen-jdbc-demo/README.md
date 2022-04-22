# Use maven plugin to generate ORM code

## To generator ORM java code

* Copy tablegen.properties file to your project root dir.
* Modify your pom.xml, add plugin configuration as 

```xml
<build>
        <plugins>
            <plugin>
                <groupId>com.github.yujiaao</groupId>
                <artifactId>tablegen-maven-plugin</artifactId>
                <version>1.10.0.4</version>

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
          <!-- ... other plugins -->
        </plugins>
    </build>          
          
```

* Open `tablegen.properties` in your favorite editor, change the database connection string according your db configuration
* Run `mvn tablegen:gen` to generate code.


## To run project
To run the project your need runtime dependency, edit your project `pom.xml` and add the follows as minimum:
```xml
<dependency>
    <groupId>com.github.yujiaao</groupId>
    <artifactId>c-dbtools</artifactId>
    <version>1.10.0.4</version>
</dependency>
```
* and Rock On!


## Integration with spring transaction manger

 
see here for more info:
https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/transaction.html

Simple to say,  if you use JDBC connection, and you want spring to manager transaction, 
then you need get connection throw:
```java
org.springframework.jdbc.datasource.DataSourceUtils
```
like that:

```java
Connection conn = DataSourceUtils.getConnection(dataSource);
```

and you need declare a transaction manager bean:

```
 <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${jdbc.driverClassName}" />
    <property name="url" value="${jdbc.url}" />
    <property name="username" value="${jdbc.username}" />
    <property name="password" value="${jdbc.password}" />
</bean>


<bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>
```

