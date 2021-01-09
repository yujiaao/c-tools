package com.bixuebihui.sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * <p>ConnectionPoolManager class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public final class ConnectionPoolManager
        implements Driver, Runnable
{

    /**
     * Constant <code>PREFIX="jdbc:bitmechanic:pool:"</code>
     */
    public static final String PREFIX = "jdbc:bitmechanic:pool:";
    private boolean wantStop  =false;
    private static final int MAJOR_VERSION = 0;
    private static final int MINOR_VERSION = 92;
    private HashMap<String, ConnectionPool> aliasHash;
    private long sleepInterval;
    private boolean trace;
    Thread thread;

    Log log  = LogFactory.getLog(ConnectionPoolManager.class);


    /**
     * <p>addAlias.</p>
     *
     * @param _alias a {@link java.lang.String} object.
     * @param driverName a {@link java.lang.String} object.
     * @param dburl a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param maxActive a int.
     * @param timeoutMiliSeconds a int.
     * @param checkoutMilliSeconds a int.
     * @throws java.lang.ClassNotFoundException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.sql.SQLException if any.
     */
    public void addAlias(String _alias, String driverName, String dburl, String username, String password, int maxActive, int timeoutMiliSeconds,
                         int checkoutMilliSeconds)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException, InvocationTargetException {
        addAlias(_alias, driverName, dburl, username, password, maxActive, timeoutMiliSeconds, checkoutMilliSeconds, 0);
    }

    /**
     * <p>addAlias.</p>
     *
     * @param _alias a {@link java.lang.String} object.
     * @param driverName a {@link java.lang.String} object.
     * @param dburl a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param maxActive a int.
     * @param timeoutMiliSeconds a int.
     * @param checkoutMilliSeconds a int.
     * @param _maxCheckout a int.
     * @throws java.lang.ClassNotFoundException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.sql.SQLException if any.
     */
    public void addAlias(String _alias, String driverName, String dburl, String username, String password, int maxActive, int timeoutMiliSeconds,
                         int checkoutMilliSeconds, int _maxCheckout)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException, InvocationTargetException {
        addAlias(_alias, driverName, dburl, username, password, maxActive, timeoutMiliSeconds, checkoutMilliSeconds, _maxCheckout, true);

    }

    /**
     * <p>addAlias.</p>
     *
     * @param _alias a {@link java.lang.String} object.
     * @param driverName a {@link java.lang.String} object.
     * @param dburl a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param maxActive a int.
     * @param timeoutMiliSeconds a int.
     * @param checkoutMilliSeconds a int.
     * @param _maxCheckout a int.
     * @param cacheStatements a boolean.
     * @throws java.lang.ClassNotFoundException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.sql.SQLException if any.
     */
    public void addAlias(String _alias, String driverName, String dburl, String username, String password, int maxActive, int timeoutMiliSeconds,
                         int checkoutMilliSeconds, int _maxCheckout, boolean cacheStatements)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException, InvocationTargetException {
        DriverManager.registerDriver((Driver) Class.forName(driverName).getDeclaredConstructor().newInstance());

        ConnectionPool connectionpool = new ConnectionPool(_alias, dburl, username, password, maxActive, timeoutMiliSeconds, checkoutMilliSeconds, _maxCheckout);
        connectionpool.setTracing(true);
        connectionpool.setCacheStatements(cacheStatements);
        addAlias(connectionpool);
    }

    /**
     * <p>addAlias.</p>
     *
     * @param _alias a {@link java.lang.String} object.
     * @param driverName a {@link java.lang.String} object.
     * @param _url a {@link java.lang.String} object.
     * @param _username a {@link java.lang.String} object.
     * @param _password a {@link java.lang.String} object.
     * @param _maxConn a int.
     * @param _timeoutMililSeconds a int.
     * @param checkoutMilliSeconds a int.
     * @param _maxCheckout a int.
     * @param _prefetchSize a int.
     * @throws java.lang.ClassNotFoundException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.sql.SQLException if any.
     */
    public void addAlias(String _alias, String driverName, String _url,
                         String _username, String _password, int _maxConn, int _timeoutMililSeconds,
                         int checkoutMilliSeconds, int _maxCheckout, int _prefetchSize)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        addAlias(_alias, driverName, _url, _username, _password,
                _maxConn, _timeoutMililSeconds, checkoutMilliSeconds, _maxCheckout, _prefetchSize, true);
    }

    /**
     * <p>addAlias.</p>
     *
     * @param _alias a {@link java.lang.String} object.
     * @param driverName a {@link java.lang.String} object.
     * @param _url a {@link java.lang.String} object.
     * @param _username a {@link java.lang.String} object.
     * @param _password a {@link java.lang.String} object.
     * @param _maxConn a int.
     * @param _timeoutMililSeconds a int.
     * @param checkoutMilliSeconds a int.
     * @param _maxCheckout a int.
     * @param _prefetchSize a int.
     * @param _cacheStatements a boolean.
     * @throws java.lang.ClassNotFoundException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.sql.SQLException if any.
     */
    public void addAlias(String _alias, String driverName, String _url,
                         String _username, String _password, int _maxConn, int _timeoutMililSeconds,
                         int checkoutMilliSeconds, int _maxCheckout, int _prefetchSize, boolean _cacheStatements)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        DriverManager.registerDriver((Driver) Class.forName(driverName).newInstance());
        ConnectionPool connectionpool = new ConnectionPool(_alias, _url, _username, _password, _maxConn, _timeoutMililSeconds, checkoutMilliSeconds, _maxCheckout);
        connectionpool.setCacheStatements(_cacheStatements);
        connectionpool.setPrefetchSize(_prefetchSize);
        addAlias(connectionpool);
    }

    /**
     * <p>addAlias.</p>
     *
     * @param connectionpool a {@link ConnectionPool} object.
     */
    public synchronized void addAlias(ConnectionPool connectionpool) {
        aliasHash.put(connectionpool.getAlias(), connectionpool);
    }

    /**
     * <p>removeAlias.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @throws java.sql.SQLException if any.
     */
    public synchronized void removeAlias(String s)
            throws SQLException {
        ConnectionPool connectionpool = getPool(s);
        aliasHash.remove(s);
        connectionpool.removeAllConnections();
    }

    /**
     * <p>getPools.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ConnectionPool> getPools() {
        return aliasHash.values();
    }

    /**
     * <p>getPool.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link ConnectionPool} object.
     * @throws java.sql.SQLException if any.
     */
    public ConnectionPool getPool(String s)
            throws SQLException {
        ConnectionPool connectionpool = aliasHash.get(s);
        if(connectionpool == null) {
            throw new SQLException("No connection pool created for alias: " + s);
        } else {
            return connectionpool;
        }
    }

    /**
     * <p>run.</p>
     */
    @Override
    public void run() {
        while(!wantStop) {
            try {
                Thread.sleep(sleepInterval);

                for(ConnectionPool connectionPool: aliasHash.values()) {
                    connectionPool.reapIdleConnections();
                }
            } catch(InterruptedException interruptedexception) {
                log.info(interruptedexception);

                Thread.currentThread().interrupt();
            }

        }
    }

    /**
     * <p>setTracing.</p>
     *
     * @param flag a boolean.
     */
    public void setTracing(boolean flag) {
        if(flag != trace) {
            for(ConnectionPool connectionPool: getPools()){
                connectionPool.setTracing(flag);
            }
            String s = "off";
            if(flag) {
                s = "on";
            }
            log.info("ConnectionPoolManager: Tracing turned " + s);
            trace = flag;
        }
    }

    /**
     * <p>isTracing.</p>
     *
     * @return a boolean.
     */
    public boolean isTracing(){
        return trace;
    }

    /** {@inheritDoc} */
    @Override
    public Connection connect(String s, Properties properties)
            throws SQLException {
        if(!s.startsWith(PREFIX)) {
            return null;
        }
        if(s.length() <= PREFIX.length()) {
            throw new SQLException("Invalid URL: " + s + " -- No alias given");
        }
        String s1 = s.substring(PREFIX.length());
        if(trace){
            log.info("ConnectionPoolManager: connect() called for " + s1 + ".  calling pool.getConnection()");
        }
        ConnectionPool connectionpool = getPool(s1);
        return connectionpool.getConnection();
    }

    /** {@inheritDoc} */
    @Override
    public boolean acceptsURL(String s) {
        return s.startsWith(PREFIX);
    }

    /**
     * <p>getMajorVersion.</p>
     *
     * @return a int.
     */
    @Override
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    /**
     * <p>getMinorVersion.</p>
     *
     * @return a int.
     */
    @Override
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    /** {@inheritDoc} */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) {
        return new DriverPropertyInfo[0];
    }

    /**
     * <p>jdbcCompliant.</p>
     *
     * @return a boolean.
     */
    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    /**
     * <p>dumpInfo.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String dumpInfo() {
        String s = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        for(ConnectionPool connectionPool: getPools()) {
            sb.append(connectionPool.dumpInfo() + s);
        }

        return sb.toString();
    }

    /**
     * <p>Constructor for ConnectionPoolManager.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public ConnectionPoolManager()
            throws SQLException {
        aliasHash = new HashMap<>();
        DriverManager.registerDriver(this);
        trace = false;
    }

    /**
     * <p>Constructor for ConnectionPoolManager.</p>
     *
     * @param sleepIntervalInSeconds a int.
     * @throws java.sql.SQLException if any.
     */
    public ConnectionPoolManager(int sleepIntervalInSeconds)
            throws SQLException {
        this();
        sleepInterval = sleepIntervalInSeconds * 1000L;
        thread = new Thread(this,"jdbc-bitmechanic-pool-daemon");
        thread.setDaemon(true);
        thread.start();
    }


    /**
     * <p>destroy.</p>
     */
    public void destroy() {
        String prefix = getClass().getSimpleName() +" destroy() ";
        try {
            for(ConnectionPool value:aliasHash.values()){
                value.removeAllConnections();
            }
            wantStop = true;

            if(thread!=null) {
                thread.interrupt();
            }

            DriverManager.deregisterDriver(this);

            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while(drivers.hasMoreElements()){
                DriverManager.deregisterDriver(drivers.nextElement());
            }

        } catch(SQLException e) {
            log.debug(prefix + "Exception caught while deregistering JDBC drivers", e);
        }
        log.debug(prefix + "complete");
    }

    /**
     * <p>getParentLogger.</p>
     *
     * @return a {@link java.util.logging.Logger} object.
     * @throws java.sql.SQLFeatureNotSupportedException if any.
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

}
