package com.bixuebihui.datasource;

import com.bixuebihui.dbcon.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>HikariCPDataSource adapter class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class HikariCPDataSource extends HikariDataSource implements INamingPool {

	private String alias = "default_hikari_ds_name";

    /**
     * <p>getConnection.</p>
     *
     * @return a {@link Connection} object.
     * @throws SQLException if any.
     */
    @Override
    public synchronized Connection getConnection() throws SQLException {
        return super.getConnection();
    }

    /**
     * <p>freeConnection.</p>
     *
     * @param connection a {@link Connection} object.
     * @throws SQLException if any.
     */
    public static void freeConnection(Connection connection)
            throws SQLException {
        connection.close();
    }

	private DatabaseConfig lastSuccessConfig;


    /**
     * <p>Getter for the field <code>alias</code>.</p>
     *
     * @return a {@link String} object.
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

        this.setDriverClassName(cfg.getClassName());
        this.setJdbcUrl(cfg.getDburl());
        this.setUsername(cfg.getUsername());
        this.setPassword(cfg.getPassword());
        this.setMaximumPoolSize(cfg.getMaxActive());
        this.setMinimumIdle(cfg.getMaxIdle());
        this.setMaxLifetime(cfg.getMaxWaitTime());

        lastSuccessConfig = cfg;
    }

    /**
     * <p>getDatabaseConfig.</p>
     *
     * @return a {@link DatabaseConfig} object.
     */
    public synchronized DatabaseConfig getDatabaseConfig() {
        return lastSuccessConfig;
    }


}
