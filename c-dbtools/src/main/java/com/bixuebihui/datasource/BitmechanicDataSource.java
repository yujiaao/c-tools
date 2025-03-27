package com.bixuebihui.datasource;

import com.bixuebihui.ConnectionManager;
import com.bixuebihui.dbcon.DatabaseConfig;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>BitmechanicDataSource class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class BitmechanicDataSource implements DataSource, INamingPool {

	private String alias = "default_bit_ds_name";

    /**
     * <p>getConnection.</p>
     *
     * @return a {@link java.sql.Connection} object.
     * @throws java.sql.SQLException if any.
     */
    @Override
    public synchronized Connection getConnection() throws SQLException {
        return ConnectionManager.getConnection(alias);
    }

    /**
     * <p>freeConnection.</p>
     *
     * @param connection a {@link java.sql.Connection} object.
     * @throws java.sql.SQLException if any.
     */
    public static void freeConnection(Connection connection)
            throws SQLException {
        ConnectionManager.freeConnection(connection);
    }

	private PrintWriter out = null;
	private int timeout = 0;
	private DatabaseConfig lastSuccessConfig;

    /**
     * <p>getLogWriter.</p>
     *
     * @return a {@link java.io.PrintWriter} object.
     * @throws java.sql.SQLException if any.
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return out;
    }

    /**
     * <p>getLoginTimeout.</p>
     *
     * @return a int.
     */
    @Override
    public int getLoginTimeout() {

        return timeout;
    }

    /** {@inheritDoc} */
    @Override
    public void setLogWriter(PrintWriter out) {
        this.out = out;
    }

    /** {@inheritDoc} */
    @Override
    public void setLoginTimeout(int seconds) {
        timeout = seconds;
    }

    /** {@inheritDoc} */
    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        throw new SQLException("Not implemented this method!");
    }

    /**
     * <p>Getter for the field <code>alias</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String getAlias() {
        return alias;
    }

    /** {@inheritDoc} */
    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setDatabaseConfig(DatabaseConfig cfg) {
        String alias = cfg==null?null:cfg.getAlias();
        if(alias!=null && alias.equals(this.getAlias())) {
            //避免重复初始化同一连接池
            return;
        }

        this.setAlias(cfg.getAlias());

        try {
            ConnectionManager.addAlias(cfg);
            lastSuccessConfig = cfg;
        } catch (IllegalAccessException | ClassNotFoundException | SQLException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            getParentLogger().log(Level.WARNING,"连接池配置出错", e);
        }
    }

    /**
     * <p>getDatabaseConfig.</p>
     *
     * @return a {@link DatabaseConfig} object.
     */
    public synchronized DatabaseConfig getDatabaseConfig() {
        return lastSuccessConfig;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWrapperFor(Class<?> iface)
            throws SQLException{
        throw new SQLException(" not know how to implement");
    }

    /** {@inheritDoc} */
    @Override
    public <T> T unwrap(Class<T> iface)
            throws SQLException{
        throw new SQLException(" not know how to implement");
    }

    /**
     * <p>getParentLogger.</p>
     *
     * @return a {@link java.util.logging.Logger} object.
     */
    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(getAlias());
    }

}
