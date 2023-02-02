package com.bixuebihui.jmesa;

import com.bixuebihui.BeanFactory;
import com.bixuebihui.jdbc.IDbHelper;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;


/**
 * 用于实现DataSource接口，用于JNDI查询，可实现容器外JUnit测试
 * 因为没有详细测试，请不要用于生产环境，仅用于单元测试
 *
 * @author Dr.Xing
 */
public class DataSourceAdapter implements DataSource {

    IDbHelper dbHelper = (IDbHelper) BeanFactory.createObjectById("dbHelper");

    private PrintWriter out = null;
    private int timeout = 0;


    @Override
    public Connection getConnection() throws SQLException {
        return dbHelper.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        return dbHelper.getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return out;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.out = out;
    }

    @Override
    public int getLoginTimeout() throws SQLException {

        return timeout;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        timeout = seconds;
    }


    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }


}
