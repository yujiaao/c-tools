package com.bixuebihui.jdbc;

import com.bixuebihui.datasource.BitmechanicDataSource;
import com.bixuebihui.datasource.DataSourceTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DbHelperTest {


    DbHelper h = new DbHelper();


    @Test
    public void testExecuteNoQueryBatch2() {

        String sql = "insert into t_log(lid, uid, content)values(?,?,?)";
        h.executeNoQuery("delete from t_log where lid<100000");
        int total=10;
        int res = h.executeNoQueryBatch(sql, total, index -> new Object[]{index+1, index+1, "test" + index}, null);

        long a = (long) h.executeScalar("select count(*) from t_log where lid<1000");
        Assertions.assertEquals(total, a);
    }

    @Test
    public void testInsertAndFetchId() throws Exception {
        String sql = "insert into t_log( uid, content)values(?,?)";
        h.executeNoQuery("delete from t_log where lid<100000");
        long res = h.insertAndFetchLastId(sql, new Object[]{10000, "test10000"}, null, null);
        Assertions.assertTrue(res > 1);

        long a = (long) h.executeScalar("select count(*) from t_log where lid="+res);
        Assertions.assertEquals(1, a);
    }


    @BeforeEach
    public void setUp() {
        BitmechanicDataSource ds = new BitmechanicDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigMaster());

        h.setDataSource(ds);
    }


    @Test
    public void testGetConnection() throws SQLException {

        h.getConnection();
    }


    @Test
    public void testFreeConnection() throws SQLException {
        Connection cn = h.getConnection();
        DbHelper.freeConnection(cn);

    }


    @Test
    public void testDbHelperConnection() throws SQLException {
        Connection cn = h.getConnection();
        IDbHelper h1 = new DbHelper(cn);
        Assertions.assertEquals(cn, h1.getConnection());

    }


    @Test
    public void testClose() throws SQLException {
        Connection cn = h.getConnection();
        cn.close();
        h.close();

    }


    @Test
    public void testExecuteScalarString() throws SQLException {
        String strSql = "select 1+2 from dual";
        String v = h.executeScalar(strSql).toString();
        Assertions.assertEquals(v, "3");
    }


    @Test
    public void testExeQuery() throws SQLException {
        String strSql = "select 1+2 from dual";
        List<Map<String, Object>> v = h.exeQuery(strSql);
        Assertions.assertEquals(1, v.size());
    }


}
