package com.bixuebihui.r2dbc.dbcon;


import com.bixuebihui.DbException;
import com.bixuebihui.r2dbc.sql.IDbHelper;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;


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
    protected Result rst = null;

    private static  final Logger LOG = LoggerFactory.getLogger(BaseOperator.class);

    public void close(){
        conn.close();
    }

    /**
     * 通用事务回滚方法
     */
    protected void rollback() {
       conn.rollbackTransaction();
    }

    /**
     * 通用获取数据库连接方法
     *
     * @return 连接
     */
    public Mono<Connection> getConnection() {
        return
             dbHelper.getConnection();
    }

    /**
     * 通用获得结果集方法
     *
     * @param sqlstr sql语句
     * @return 结果集
     */
    public Result getResultSet(String sqlstr)  {
        initConn();
        if(stmt==null) {
            stmt = conn.createStatement(sqlstr);
        }
        rst = Mono.from(stmt.execute()).block();
        return rst;
    }

    /**
     * 通用获得声明
     *
     * @return 语句
     */
    public Statement getStatement(String sql) {
        initConn();
        stmt = conn.createStatement(sql);
        return stmt;
    }

    /**
     * 通用获得预编译声明
     *
     * @param presqlstr 用于预编译的sql语句
     * @return 预编译语句
     */
    public Statement getPreparedStatement(String presqlstr) {
        initConn();
        stmt = conn.createStatement(presqlstr);
        return stmt;
    }


    private synchronized void initConn()  {
        if(dbHelper==null){
            throw new DbException("dbHelper没有初始化");
        }

        if(conn==null) {
            conn = dbHelper.getConnection().block();
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
