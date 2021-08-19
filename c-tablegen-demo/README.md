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
                <version>1.6.4</version>

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
* and Rock On!

## To run project
To run the project your need runtime dependency, edit your project and add the follows as minimum:

                <dependency>
                    <groupId>com.github.yujiaao</groupId>
                    <artifactId>c-dbtools</artifactId>
                    <version>1.6.4</version>
                </dependency>
