package com.bixuebihui.tablegen;

import com.bixuebihui.jdbc.BaseDao;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static com.bixuebihui.db.Dialect.MYSQL;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OnFlyViewUtilsTest {
    static IDbHelper dbHelper;

    @BeforeAll
    public static void setUp() {
        TableGen t = new TableGen();
        t.init(TableUtilsTest.class.getResource("/tablegen.properties").getPath());
        dbHelper = t.getDbHelper(t.getDbConfig());
    }

    @Test
    void getColumnData() {
        String sql = "select test_gen.*, concat(t_edu.degree) degree_plus from test_gen left join t_edu on test_gen.edu_id=t_edu.id limit 1";
        List<TableInfo> list = dbHelper.executeQuery(sql, null, new RowMapperResultReader<>((rs, index, set) -> OnFlyViewUtils.getColumnData(rs.getMetaData(), "test", MYSQL)));

        TableInfo cols = list.get(0);
        assertEquals("Type : INT(11) Name : id", cols.getFields().get(0).toString());
        assertEquals("Type : VARCHAR(100) Name : degree_plus", cols.getFields().get(cols.getFields().size()-1).toString());

        for (ColumnData col : cols.getFields()) {
            System.out.println(col);
        }

    }

    @Test
    void getColumnDataAs() {
        String sql = "select test_gen.*, degree AS degree_plus from test_gen left join t_edu on test_gen.edu_id=t_edu.id limit 1";
        List<TableInfo> list = dbHelper.executeQuery(sql, null,
                new RowMapperResultReader<>((rs, index, set) ->
                        OnFlyViewUtils.getColumnData(rs.getMetaData(), "test", MYSQL)));

        TableInfo cols = list.get(0);
        assertEquals("Type : INT(11) Name : id", cols.getFields().get(0).toString());
        assertEquals("Type : VARCHAR(100) Name : degree_plus", cols.getFields().get(cols.getFields().size()-1).toString());

        for (ColumnData col : cols.getFields()) {
            System.out.println(col);
        }

    }
}
