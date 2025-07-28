package com.bixuebihui.jdbc;

import com.bixuebihui.DbException;
import com.bixuebihui.shardingjdbc.core.constant.SQLType;
import com.bixuebihui.shardingjdbc.core.hint.HintManagerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Master-Slaver mode database helper class.
 * Master connection manager for update, insert and delete
 * Slaver connection for select only.
 *
 * @author Wanxiang.Xing@gmail.com
 * @version $Id: $Id
 */
public class MSDbHelper extends DbHelper {
    private static final Logger LOG = LoggerFactory.getLogger(MSDbHelper.class);
	/**
	 * The DML_FLAG adopted from ShadingJDBC
	 */
    private static final ThreadLocal<Boolean> DML_FLAG = ThreadLocal.withInitial(() -> false);
    /**
     * reset DML flag.
     */
    public static void resetDMLFlag() {
        DML_FLAG.remove();
    }

	/**
	 * master database for change
	 */
	private DataSource masterDatasource = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(boolean readOnly)  {
        try {
            if (!readOnly) {
                DML_FLAG.set(true);
                return masterDatasource.getConnection();
            } else if (isMasterRoute(SQLType.DQL)) {
                return masterDatasource.getConnection();
            }
            Connection conn = super.getConnection();
            try{
                conn.setReadOnly(true);
            }catch (SQLException e){
                LOG.warn("set connection readOnly failed", e);
            }
            return conn;
        }catch (SQLException e){
            throw new DbException(e);
        }
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
            return masterDatasource.getConnection();
        }
        return super.getConnection();
    }

    private boolean isMasterRoute(final SQLType sqlType) {
        return SQLType.DQL != sqlType || DML_FLAG.get() || HintManagerHolder.isMasterRouteOnly();
    }

    /**
     * <p>getConnection.</p>
     *
     * @return a {@link java.sql.Connection} object.
     */
    @Override
    public Connection getConnection() {
        return  getConnection(false);
    }

    /**
     * <p>Getter for the field <code>masterDatasource</code>.</p>
     *
     * @return a {@link javax.sql.DataSource} object.
     */
    public DataSource getMasterDatasource() {
        return masterDatasource;
    }

    /**
     * <p>Setter for the field <code>masterDatasource</code>.</p>
     *
     * @param masterDatasource a {@link javax.sql.DataSource} object.
     */
    public void setMasterDatasource(DataSource masterDatasource) {
        this.masterDatasource = masterDatasource;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        HintManagerHolder.clear();
        resetDMLFlag();
        super.close();
    }
}
