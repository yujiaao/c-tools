package com.bixuebihui.jmesa;

import com.bixuebihui.sql.ConnectionPool;
import com.bixuebihui.sql.PooledConnection;
import com.foundationdb.sql.StandardException;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class MiniSqlParserTest {

    @Test
    void testParse() throws StandardException {
        EasyTable.MiniSqlParser result = EasyTable.MiniSqlParser.parseOld("select 1 as a from A");
        Assertions.assertEquals("a", result.tableName);
        Assertions.assertEquals("[value: 1\n" +
                "type: INTEGER NOT NULL\n" +
                "]", List.of(result.colLabels).toString());
        Assertions.assertEquals("[a]", List.of(result.colNames).toString());
    }

    @Test
    void testParseNew() throws JSQLParserException {
        String sql = " SELECT '2021-01-01' as StartDate, b, C as EndDate from ADTable with(nolock) group by AuthorId, AuthorName";
        EasyTable.MiniSqlParser result = EasyTable.MiniSqlParser.parse(sql);
        Assertions.assertEquals("ADTable", result.tableName);
        Assertions.assertEquals("[StartDate, b, EndDate]", List.of(result.colLabels).toString());
        Assertions.assertEquals("[StartDate, b, EndDate]", List.of(result.colNames).toString());
    }

    @Test
    void testGetByDb() {
//        EasyTable.MiniSqlParser result = EasyTable.MiniSqlParser.getByDb(new PooledConnection(null, new ConnectionPool("alias", "url", "username", "password", 0, 0, 0, 0, 0), 0), "sql");
//        Assertions.assertEquals(new EasyTable.MiniSqlParser(), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
