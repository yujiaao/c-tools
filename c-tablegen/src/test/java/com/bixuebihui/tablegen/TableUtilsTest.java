package com.bixuebihui.tablegen;

import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import com.google.common.primitives.Primitives;
import org.apache.poi.util.ArrayUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import org.springframework.util.comparator.BooleanComparator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.Collections.binarySearch;
import static org.junit.jupiter.api.Assertions.*;

public class TableUtilsTest {
    static IDbHelper dbHelper;

    @BeforeAll
    public static void setUp() {
        TableGen t = new TableGen();
        t.init(getPath());
        dbHelper = t.getDbHelper(t.getDbConfig());
    }

    private static String getPath() {
        return TableUtilsTest.class.getResource("/tablegen.properties").getPath();
    }

    @Test
    public void testGetTableKeys() throws SQLException {

        try(Connection cn = dbHelper.getConnection()) {
            DatabaseMetaData meta = cn.getMetaData();
            try(ResultSet primaryKeys = meta.getPrimaryKeys(null, null, "employees")) {
                while (primaryKeys.next()) {
                    String primaryKeyColumn = primaryKeys.getString("COLUMN_NAME");
                    System.out.println("Primary Key Column: " + primaryKeyColumn);
                }


                List<String> v = TableUtils.getTableKeys(meta, null, null, "employees");
                for (String s : v) {
                    System.out.println(s);
                }
            }
        }
    }

    @Test
    public void testGetTableImportedKeys() throws SQLException {
        DatabaseMetaData metaData = dbHelper.getConnection().getMetaData();

        String tableName = "t_log";
        String schema = "test";
        StopWatch sw = new StopWatch();
        sw.start();
        List<ForeignKeyDefinition> v = TableUtils.getTableImportedKeys(metaData, null, schema, tableName);
        for (ForeignKeyDefinition s : v) {
            System.out.println(s);
        }
        sw.stop();
        System.out.println("use time: " + sw.getTotalTimeMillis() + "ms");
    }

    @Test
    public void testGetTableExportedKeys() throws SQLException {
        DatabaseMetaData metaData = dbHelper.getConnection().getMetaData();

        String tableName = "Forum";
        String schema = "test";
        String catalog = "test";
        StopWatch sw = new StopWatch();
        sw.start();
        List<ForeignKeyDefinition> v = TableUtils.getTableExportedKeys(metaData, catalog, schema, tableName);
        for (ForeignKeyDefinition s : v) {
            System.out.println(s);
        }
        sw.stop();
        System.out.println("use time: " + sw.getTotalTimeMillis() + "ms");
    }

    //我不想把短暂的人生投入到无限的忽悠事业中去
    //
    @Test
    public void testGetTableData() throws SQLException {
        TableGen t = new TableGen();
        t.init(getPath());

        DatabaseMetaData metaData = t.connect(dbHelper.getConnection());

        String schema = "autonews_autocode";
        String catalog = null;
        StopWatch sw = new StopWatch();
        sw.start();
        List<String> v = TableUtils.getTableData(metaData, catalog, schema, null, t.getConfig().tablesList, t.getConfig().excludeTablesList);
        for (String s : v) {
            System.out.println(s);
            TableInfo cols = TableUtils.getColumnData(dbHelper, metaData, catalog, schema, s,false);
            for (ColumnData col : cols.getFields()) {
                System.out.println(col);
            }
        }

        sw.stop();
        System.out.println("use time: " + sw.getTotalTimeMillis() + "ms");
    }

    @Test
    public void testRun() throws SQLException {

        TableGen.main(new String[]{getPath()});

    }

    @Test
    public void testMatchTableName() {
        Map<String, String> inlist = new HashMap<>();
        inlist.put("WPVM_(\\w+)", "WPVM_(\\w+)");
        inlist.put("DIC_(\\w+)", "DIC_(\\w+)");
        String tableName = "WPVM_COMPANYINFO";

        assertTrue(TableUtils.matchTableName(inlist, tableName));
        tableName = "DIC_INSURANCE";

        assertTrue(TableUtils.matchTableName(inlist, tableName));
        tableName = "MYDIC_INSURANCE";

        assertFalse(TableUtils.matchTableName(inlist, tableName));

    }

    @Test
    public void testMatchTableName1() {
        Map<String, String> inlist = new HashMap<>();
        inlist.put("tbl_MMResult_(\\w+)", "tbl_MMResult_(\\w+)");
        inlist.put("tbl_SMResult_(\\w+)", "tbl_SMResult_(\\w+)");
        String tableName = "tbl_MMResult_0101";

        assertTrue(TableUtils.matchTableName(inlist, tableName));

        tableName = "tbl_SMResult_0102";

        assertTrue(TableUtils.matchTableName(inlist, tableName));

    }


    @Test
    public void testGetColumnData11() throws SQLException {
        DatabaseMetaData metaData = dbHelper.getConnection().getMetaData();
        String tableName = "VRMS_ALLOWANCE";
        String catalog = "WESTPLAN";
        List<ColumnData> v = TableUtils.getColumnData(dbHelper, metaData, catalog, null, tableName,false).getFields();
        for (ColumnData c : v) {
            System.out.println(c);
        }
    }

    @Test
    public void testGetColumnData() throws SQLException {
        DatabaseMetaData meta = dbHelper.getConnection().getMetaData();

        //create table test1(id int auto_increment, num decimal(10,6) , primary key(id));

        String tableName = "t_metatable";
        List<ColumnData> v = TableUtils.getColumnData(dbHelper,  meta,null, null, tableName,false).getFields();
        for (ColumnData s : v) {
            System.out.println(s + ", java type = " + s.getJavaType());
        }
    }

    @org.junit.jupiter.api.Test
    void getTableImportedKeys() throws SQLException {
        String tableName = "employees";
        List<ForeignKeyDefinition> res = TableUtils.getTableImportedKeys(dbHelper.getConnection().getMetaData(),
                "test", "test", tableName);
        assertEquals(1, res.size());

        Map<String, List<ForeignKeyDefinition>> map = TableUtils.getAllMySQLImportKeys(dbHelper, "test");

        List<ForeignKeyDefinition> newList = map.get(tableName);


        ////这里getTableImportedKeys sFKTable返回的表名是小写的(5.6.21-enterprise-commercial-advanced mac os),不知为什么?
        //
        assertEquals(res.get(0).toString().toLowerCase(), newList.get(0).toString().toLowerCase());

    }

    @org.junit.jupiter.api.Test
    void getTableExportedKeys() throws SQLException {
        List<ForeignKeyDefinition> res = TableUtils.getTableExportedKeys(dbHelper.getConnection().getMetaData(),
                "test", "test", "departments");
        assertEquals(1, res.size());
    }

    @org.junit.jupiter.api.Test
    void getAllMySQLExportKeys() {
        Map<String, List<ForeignKeyDefinition>> res = TableUtils.getAllMySQLImportKeys(dbHelper, "test");
        for (List<ForeignKeyDefinition> list : res.values()) {
            for (ForeignKeyDefinition fk : list) {
                System.out.println(fk.toString());
            }
        }
        assertTrue(res.size() > 1);
    }

    @org.junit.jupiter.api.Test
    void getAllMySQLImportKeys() throws SQLException {
        Map<String, List<ForeignKeyDefinition>> res = TableUtils.getAllMySQLExportKeys(dbHelper, "test");
        for (List<ForeignKeyDefinition> list : res.values()) {
            for (ForeignKeyDefinition fk : list) {
                System.out.println(fk.toString());
            }
        }
        assertTrue(res.size() > 1);
    }


    /**
     * 问题测试！
     */
    @Test void testTrue(){
        {
            boolean[] test1 = new boolean[]{false, true, false};
            boolean hasTrue = Arrays.asList(test1).contains(true);
            assertFalse(hasTrue);
            System.out.println("expect 1 = "+Arrays.asList(test1).size());

        }
        {
            Boolean[] test1 = new Boolean[]{false, true, false};
            boolean hasTrue = Arrays.asList(test1).contains(true);
            assertTrue(hasTrue);
            System.out.println("expect 3 = "+Arrays.asList(test1).size());
        }
    }
}
