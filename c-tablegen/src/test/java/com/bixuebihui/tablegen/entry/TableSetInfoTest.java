package com.bixuebihui.tablegen.entry;

import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.TableGen;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.bixuebihui.tablegen.entry.TableSetInfo.getPos;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableSetInfoTest {
    static IDbHelper dbHelper ;

    @BeforeAll
    public static void setUp() {
        TableGen t = new TableGen();
        t.init(TableSetInfoTest.class.getResource("/tablegen.properties").getPath());
        dbHelper = t.getDbHelper(t.getDbConfig());
    }


    /**
     * TABLE_CAT	TABLE_SCHEM	TABLE_NAME	NON_UNIQUE	INDEX_QUALIFIER	INDEX_NAME	TYPE	ORDINAL_POSITION	COLUMN_NAME	ASC_OR_DESC	CARDINALITY	PAGES	FILTER_CONDITION
     * test	        null	    test_gen	0	        test	        PRIMARY	    3	    1	                id	        A	5	null	null
     * test	        null	    test_gen	1	        test	        ind_test_gen	3	1	                edu_id	    A	2	null	null
     * test	        null	    test_gen	1	        test	        ind_test_gen	3	2	                name	    A	2	null	null
     * test	        null	    test_gen	1	        test	        t_edu_id	3	    1	                edu_id	    A	2	null	null
     */
    @Test
    void testIndex() {
        try {
            DatabaseMetaData dbmd = dbHelper.getConnection().getMetaData();
            ResultSet rs = dbmd.getIndexInfo(null,  null, "test_gen", false, false);
            ResultSetMetaData rsmd = rs.getMetaData();

            // Display the result set data.
            int cols = rsmd.getColumnCount();
            for(int i=1; i<=cols; i++) {
                System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println("");

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getString(i)+"\t");
                }
                System.out.println("");
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTableDataExt() {
    }

    @Test
    void setTableDataExt() {
    }

    @Test
    void getForeignKeyImCache() {
    }

    @Test
    void getKeyCache() {
    }

    @Test
    void getIndexCache() {
    }

    @Test
    void getForeignKeyExCache() {
    }

    @Test
    void getTableInfos() {
    }

    @Test
    void setTableInfos() {
    }

    @Test
    void getTableCols() {
    }

    @Test
    void tableName2ClassName() {
    }

    @Test
    void getInterface() {
    }

    @Test
    void getColumnsExtInfo() {
    }

    @Test
    void getTableData() {
    }

    @Test
    void testGetTableDataExt() {
    }

    @Test
    void setupMetatable() {
    }

    @Test
    void insertOrUpdateMetatable() {
    }

    @Test
    void initTableData() {
    }

    @Test
    void getExtraTableDataFromXml() {
    }



    @Test
    void testGetPos() {
        List<String> list = new ArrayList<>();
        list.add(getPos(list,2),"second");
        list.add(getPos(list, 0),"start");
        list.add(getPos(list,3),"third");
        list.add(getPos(list,1),"first");
        assertEquals("[start, first, second, third]",list.toString());
    }


    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', textBlock = """
            "select top 10 * from abc", "select top 10 * from abc"
            "select * from abc", "select top 1 * from abc"
            "select ABC from abc", "select top 1 ABC from abc"
            "select [ABC],'def' from abc", "select top 1 [ABC],'def' from abc"
            "sELect [ABC],'def' from abc", "select top 1 [ABC],'def' from abc"
            "sELect [ABC],'def' from (select * from Table1)", "select top 1 [ABC],'def' from (select * from Table1)"
            """
    )
    void insertTop1(String sql, String result) {
        assertEquals(result, TableSetInfo.insertTop1(sql));
    }

    @Test
    void getTableKeys() {
    }

    @Test
    void getTableImportedKeys() {
    }

    @Test
    void getTableExportedKeys() {
    }

    @Test
    void getTableIndexes() throws SQLException {
        TableSetInfo setInfo = new TableSetInfo();

        TableGen t = new TableGen();
        t.init(TableSetInfoTest.class.getResource("/tablegen.properties").getPath());

        String testGen = "test_gen";
        setInfo.initIndex(t.getConfig(), dbHelper, testGen);
        System.out.println(setInfo.getTableIndexes(testGen));
    }

    @Test
    void replaceComment() {
        String comment ="'审核状态 0未审核 1通过审核 2不通过审核',";
        String s = TableSetInfo.replaceComment(comment);
        assertEquals("审核状态 0未审核 1通过审核 2不通过审核", s);

        String sr = "\"业务线OGC,PGC,UGC\",";
        String s2 = TableSetInfo.replaceComment(sr);
        assertEquals("业务线OGC,PGC,UGC", s2);
    }

    @Test
    void findFieldComment(){
        String sql = "CREATE TABLE `mytable` (\n" +
                "  `type` varchar(65532) NOT NULL COMMENT \"业务A,B,C\",\n `id` varchar(65532) NOT NULL COMMENT \"id\" );";
        List<ColumnData> fields = List.of(new ColumnData("type", "varchar", 65532, false),
                new ColumnData("id", "varchar", 65532, false));

        TableSetInfo.findFieldComment("mytable", fields, sql);
        assertEquals("业务A,B,C", fields.get(0).getComment());
        assertEquals("id", fields.get(1).getComment());

    }
}
