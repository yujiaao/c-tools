# Database Code Generator 

Yet another ORM framework for java


[![Maven Central](https://img.shields.io/maven-central/v/com.github.yujiaao/c-tools.svg)](http://search.maven.org/#search%7Cga%7C1%7Cc-tools)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v/https/oss.sonatype.org/content/repositories/releases/com/github/yujiaao/c-tools/maven-metadata.xml.svg)](https://oss.sonatype.org/content/repositories/releases/com/github/yujiaao/c-tools)
[![Build Status](https://travis-ci.com/yujiaao/c-tools.svg?branch=master)](https://travis-ci.com/yujiaao/c-tools)
[![codecov](https://codecov.io/gh/yujiaao/c-tools/branch/master/graph/badge.svg?token=NIRX0PUZ2A)](https://codecov.io/gh/yujiaao/c-tools)


## Get Starting 
To generate code please refer [`c-tablegen-demo`](c-tablegen-demo) project.

### feature list

 - generate code from database table, view, and procedure via jdbc metadata.
 - support incrementally update database schema by separate generated code with custom inherited classes.
 - support generate code from SQL on the fly as a virtual database view, suitable for use when create a database view is not desirable.
 - support primary key, custom sequence, export and import indices.
 - support MySQL, H2, Derby, Postgres, Access, MS SQL Server, Oracle(partially).
 - support application side Read/Write separate data sources.
 
### NOT use
 This project not use `Hibernate` & `MyBatis` for ORM framework, but can work together with them.
 The optional validation framework is used Hibernate-validation project.


## A small and fast Connection Pool: BitmechanicDataSource

## A ruby like ActiveRecord usage for generated code

## A json table export adapter : c-jmesa

## TODO
add async support for r2dbc.

## version 2.0 
migrate to Java 11

use dependencies:

 - io.swagger.core.v3:swagger-annotation:2.1.10
 - mysql:mysql-connector-java:8.0.26
 - org.hibernate.validator:hibernate-validator:6.2.0.Final


