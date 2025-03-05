package com.bixuebihui.tablegen;

import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.bixuebihui.db.Dialect.MYSQL;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OnFlyViewUtilsTest {
    static IDbHelper dbHelper;
    static boolean isR2dbc = false;
    static TableGen t = new TableGen();

    @BeforeAll
    public static void setUp() {
        t.init(TableUtilsTest.class.getResource("/tablegen.properties").getPath());
        dbHelper = t.getDbHelper(t.getDbConfig());
    }

    @Test
    void getColumnData() throws SQLException {
        String sql = "select test_gen.*, concat(t_edu.degree) degree_plus from test_gen left join t_edu on test_gen.edu_id=t_edu.id limit 1";
        Connection cn = dbHelper.getConnection();
        DatabaseMetaData meta = cn.getMetaData();

        List<TableInfo> list = dbHelper.executeQuery(sql, null, new RowMapperResultReader<>(null) {
            @Override
            public List<TableInfo> processResultSet(ResultSet rs, Set<String> fields) throws SQLException {
                return
                        List.of(OnFlyViewUtils.getColumnData(rs.getMetaData(), "test", MYSQL, t.getConfig(), cn, dbHelper, meta));
            }
        });

        TableInfo cols = list.get(0);
        assertEquals("id", cols.getFields().get(0).getName());
        assertEquals("degree_plus", cols.getFields().get(cols.getFields().size() - 1).getName());

        for (ColumnData col : cols.getFields()) {
            System.out.println(col);
        }

    }

    @Test
    void getColumnDataAs() throws SQLException {
        String sql = "select test_gen.*, degree AS degree_plus from test_gen left join t_edu on test_gen.edu_id=t_edu.id limit 1";
        Connection cn = dbHelper.getConnection();
        DatabaseMetaData meta = cn.getMetaData();

        List<TableInfo> list = dbHelper.executeQuery(sql, null,
                new RowMapperResultReader<>(null, Collections.emptyList()) {
                    @Override
                    public List<TableInfo> processResultSet(ResultSet rs, Set<String> fields) throws SQLException {
                        return
                                List.of(OnFlyViewUtils.getColumnData(rs.getMetaData(), "test", MYSQL, t.getConfig(), cn, dbHelper, meta));

                    }
                }

        );

        TableInfo cols = list.get(0);
        assertEquals("id", cols.getFields().get(0).getName());
        assertEquals("degree_plus", cols.getFields().get(cols.getFields().size() - 1).getName());

        for (ColumnData col : cols.getFields()) {
            System.out.println(col);
        }

    }
}
