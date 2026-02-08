package com.bixuebihui.jdbc;

import com.bixuebihui.DbException;
import com.bixuebihui.shardingjdbc.core.constant.SQLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * jdbc 执行与 Spring 事务管理集成的 DbHelper 实现
 * 使用 Spring 的事务管理功能时，请遵循以下规则：
 * 必须使用 DataSourceUtils.getConnection(dataSource) 获取连接，而不是 dataSource.getConnection()
 * 不要手动调用 connection.commit() 或 connection.rollback()
 * 不要手动关闭连接，由 Spring 负责管理连接的生命周期
 * 所有 JDBC 操作必须在被 @Transactional 注解标记的方法内部执行
 * 如果方法内部抛出未捕获的异常（通常是运行时异常），事务会自动回滚
 */
public class SpringDbHelper extends MSDbHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SpringDbHelper.class);

    public SpringDbHelper() {
        super();
    }

    @Override
    public Connection getConnection(boolean readOnly) {
        if (!readOnly) {
            DML_FLAG.set(true);
            return DataSourceUtils.getConnection(getMasterDatasource());
        } else if (isMasterRoute(SQLType.DQL)) {
            return DataSourceUtils.getConnection(getMasterDatasource());
        }
        Connection conn = super.getConnection();
        try{
            conn.setReadOnly(true);
        }catch (SQLException e){
            LOG.warn("set connection readOnly failed", e);
        }
        return conn;
    }

    /**
     * <p>getConnection.</p>
     *
     * @param sqlType a {@link SQLType} object.
     * @return a {@link java.sql.Connection} object.
     * @throws java.sql.SQLException if any.
     */
    public Connection getConnection(final SQLType sqlType) throws SQLException {
        if (isMasterRoute(sqlType)) {
            DML_FLAG.set(true);
            return  DataSourceUtils.getConnection(getMasterDatasource());
        }
        return getConnection();
    }

    //DataSourceUtils.doReleaseConnection(@Nullable Connection con, @Nullable DataSource dataSource)

    @Override
    public void close() {

        try {
//            DataSourceUtils.releaseConnection(conn, getMasterDatasource());
        } finally {
            DML_FLAG.remove();
        }
    }
}
