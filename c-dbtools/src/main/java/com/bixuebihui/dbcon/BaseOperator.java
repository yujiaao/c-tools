package com.bixuebihui.dbcon;


import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * <p>BaseOperator class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class BaseOperator
{
    private IDbHelper dbHelper = null;
    protected Connection conn = null;
    protected Statement stmt = null;
    protected PreparedStatement pstmt = null;
    protected ResultSet rst = null;

    private static  final Logger LOG = LoggerFactory.getLogger(BaseOperator.class);

    /**
     * 通用关闭链接方法
     */
    protected void close() {
        DbUtils.closeQuietly(pstmt);
        DbUtils.closeQuietly(conn, stmt, rst);
    }


    /**
     * 关闭传进来的参数
     *
     * @param myconn 数据库连接
     * @param mystmt 语句
     * @param myrst 结果集
     * @param mypstmt 预编译语句
     */
    protected void close(Connection myconn, Statement mystmt, ResultSet myrst, PreparedStatement mypstmt) {
        DbUtils.closeQuietly(mypstmt);
        DbUtils.closeQuietly(myconn, mystmt, myrst);
    }

    /**
     * 通用事务回滚方法
     */
    protected void rollback() {
        try {
           if( conn!=null){
               conn.rollback();
           }
        } catch(SQLException e) {
            LOG.error("", e);
        }
    }

    /**
     * 通用获取数据库连接方法
     *
     * @return 连接
     */
    public Connection getConnection() {
        return
             dbHelper.getConnection();
    }

    /**
     * 通用获得结果集方法
     *
     * @param sqlstr sql语句
     * @return 结果集
     * @throws java.sql.SQLException 数据库出错
     */
    public ResultSet getResultSet(String sqlstr) throws SQLException {
        initConn();
        if(stmt==null) {
            stmt = conn.createStatement();
        }
        rst = stmt.executeQuery(sqlstr);
        return rst;
    }

    /**
     * 通用获得声明
     *
     * @return 语句
     * @throws java.sql.SQLException 数据库出错
     */
    public Statement getStatement() throws SQLException {
        initConn();
        stmt = conn.createStatement();
        return stmt;
    }

    /**
     * 通用获得预编译声明
     *
     * @param presqlstr 用于预编译的sql语句
     * @return 预编译语句
     * @throws java.sql.SQLException 数据库出错
     */
    public Statement getPreparedStatement(String presqlstr) throws SQLException {
        initConn();
        pstmt = conn.prepareStatement(presqlstr);
        return pstmt;
    }


    private synchronized void initConn() throws SQLException {
        if(dbHelper==null){
            throw new SQLException("dbHelper没有初始化");
        }

        if(conn==null || conn.isClosed()) {
            conn = dbHelper.getConnection();
        }
    }


    /**
     * <p>Getter for the field <code>dbHelper</code>.</p>
     *
     * @return a {@link IDbHelper} object.
     */
    public IDbHelper getDbHelper() {
        return dbHelper;
    }


    /**
     * <p>Setter for the field <code>dbHelper</code>.</p>
     *
     * @param dbHelper a {@link IDbHelper} object.
     */
    public void setDbHelper(IDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
}
