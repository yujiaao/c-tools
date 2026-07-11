package com.bixuebihui.db;

import junit.framework.TestCase;

public class DialectTest extends TestCase {

    public void testDetectDbType() {
        assertEquals(Dialect.UNKNOWN, Dialect.detectDbType("com.example.Driver"));
        assertEquals(Dialect.ORACLE, Dialect.detectDbType("oracle.jdbc.OracleDriver"));
        assertEquals(Dialect.DERBY, Dialect.detectDbType("org.apache.derby.jdbc.EmbeddedDriver"));
        assertEquals(Dialect.MYSQL, Dialect.detectDbType("com.mysql.cj.jdbc.Driver"));
        assertEquals(Dialect.SQLSERVER, Dialect.detectDbType("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
        assertEquals(Dialect.SQLSERVER_2010_AND_UP, Dialect.detectDbType("com.microsoft.sql server.jdbc.Driver"));
        assertEquals(Dialect.POSTGRESQL, Dialect.detectDbType("org.postgresql.Driver"));
        assertEquals(Dialect.H2, Dialect.detectDbType("org.h2.Driver"));
        assertEquals(Dialect.ACCESS, Dialect.detectDbType("net.ucanaccess.jdbc.UcanaccessDriver"));
    }
}
