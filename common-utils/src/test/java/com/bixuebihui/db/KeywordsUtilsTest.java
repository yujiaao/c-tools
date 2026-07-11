package com.bixuebihui.db;

import junit.framework.TestCase;

import static com.bixuebihui.db.Dialect.MYSQL;
import static com.bixuebihui.db.Dialect.ORACLE;
import static com.bixuebihui.db.Dialect.SQLSERVER;
import static com.bixuebihui.db.Dialect.SQLSERVER_2005_AND_UP;
import static com.bixuebihui.db.Dialect.SQLSERVER_2010_AND_UP;

public class KeywordsUtilsTest extends TestCase {

    public void testIsYes() {
        assertTrue(KeywordsUtils.isYes("Y"));
        assertTrue(KeywordsUtils.isYes("YES"));
        assertFalse(KeywordsUtils.isYes("N"));
        assertFalse(KeywordsUtils.isYes(null));
    }

    public void testIsSqlKeyword() {
        assertTrue(KeywordsUtils.isSqlKeyword("select"));
        assertTrue(KeywordsUtils.isSqlKeyword("SELECT"));
        assertFalse(KeywordsUtils.isSqlKeyword("username"));
    }

    public void testDecorFieldIfIsKeyword() {
        assertEquals("username", KeywordsUtils.decorFieldIfIsKeyword("username", MYSQL));
        assertEquals("[select]", KeywordsUtils.decorFieldIfIsKeyword("select", SQLSERVER));
        assertEquals("[select]", KeywordsUtils.decorFieldIfIsKeyword("select", SQLSERVER_2005_AND_UP));
        assertEquals("[select]", KeywordsUtils.decorFieldIfIsKeyword("select", SQLSERVER_2010_AND_UP));
        assertEquals("`select`", KeywordsUtils.decorFieldIfIsKeyword("select", MYSQL));
        assertEquals("select", KeywordsUtils.decorFieldIfIsKeyword("select", ORACLE));
    }
}
