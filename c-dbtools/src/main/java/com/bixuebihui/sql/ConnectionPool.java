// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-1-4 11:28:19
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ConnectionPool.java

package com.bixuebihui.sql;

import com.bixuebihui.util.JavaAlarm;
import com.bixuebihui.util.TimeoutException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executor;

/**
 * <p>ConnectionPool class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class ConnectionPool {

    private static final int MIN_TIMEOUT_MILLI_SECONDS = 50;
    private static final int CONNECTION_TIMEOUT = 100;
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionPool.class);
    /**
     * Constant <code>CONNECTION_POOL="ConnectionPool: "</code>
     */
    public static final String CONNECTION_POOL = "ConnectionPool: ";
    Executor executor = Runnable::run;
    private final int maxPrepStmts;

    /**
     * <p>Setter for the field <code>cacheStatements</code>.</p>
     *
     * @param cacheStatements a boolean.
     */
    public void setCacheStatements(boolean cacheStatements) {
        this.cacheStatements = cacheStatements;
    }

    /**
     * <p>isCacheStatements.</p>
     *
     * @return a boolean.
     */
    public boolean isCacheStatements() {
        return cacheStatements;
    }

    /**
     * <p>setTracing.</p>
     *
     * @param flag a boolean.
     */
    public synchronized void setTracing(boolean flag) {
        trace = flag;
    }

    /**
     * <p>Getter for the field <code>alias</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * <p>Getter for the field <code>numRequests</code>.</p>
     *
     * @return a int.
     */
    public int getNumRequests() {
        return numRequests;
    }

    /**
     * <p>Getter for the field <code>numWaits</code>.</p>
     *
     * @return a int.
     */
    public int getNumWaits() {
        return numWaits;
    }

    /**
     * <p>getNumCheckoutTimeouts.</p>
     *
     * @return a int.
     */
    public int getNumCheckoutTimeouts() {
        return numCheckoutTimeout;
    }

    /**
     * <p>Getter for the field <code>maxConn</code>.</p>
     *
     * @return a int.
     */
    public int getMaxConn() {
        return maxConn;
    }

    /**
     * <p>size.</p>
     *
     * @return a int.
     */
    public int size() {
        return connVector.size();
    }

    /**
     * <p>Constructor for ConnectionPool.</p>
     *
     * @param alias a {@link java.lang.String} object.
     * @param url a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param maxConn a int.
     * @param timeoutMilliSeconds a int.
     * @param checkoutMilliSeconds a int.
     * @param maxCheckout a int.
     * @param maxPrepStmts a int.
     */
    public ConnectionPool(String alias, String url, String username,
                          String password, int maxConn, int timeoutMilliSeconds, int checkoutMilliSeconds,
                          int maxCheckout, int maxPrepStmts) {
        numConnectionFaults = 0;
        prefetchSize = -1;
        this.timeoutMilliSeconds = timeoutMilliSeconds;
        if (this.timeoutMilliSeconds < CONNECTION_TIMEOUT) {
            this.timeoutMilliSeconds = CONNECTION_TIMEOUT;
        }

        this.checkoutMilliSeconds = checkoutMilliSeconds;
        this.alias = alias;
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxConn = maxConn;
        this.maxCheckout = maxCheckout;
        numRequests = 0;
        numWaits = 0;
        numCheckoutTimeout = 0;
        connVector = new ArrayList<>(maxConn);

        trace = false;
        cacheStatements = true;

        this.maxPrepStmts = maxPrepStmts;
    }

    private void debug(Exception e) {
        if (trace) {
            LOG.debug("exception",e);
        }
    }

    private void warn(String warning) {

        LOG.warn(CONNECTION_POOL + alias + " " + warning);
    }

    private void error(String errormsg) {
        LOG.error(CONNECTION_POOL + alias + " " + errormsg);
    }

    /**
     * <p>reapIdleConnections.</p>
     */
    public synchronized void reapIdleConnections() {
        debug(" reapIdleConnections() starting, size=" + size());
        long currentTimeInMillis = System.currentTimeMillis();
        long keepToTime = currentTimeInMillis - timeoutMilliSeconds; //空保持时间
        long checkoutTime = currentTimeInMillis - checkoutMilliSeconds; //单次租用期限
        for (int i = 0; i < connVector.size(); ) {
            PooledConnection pooledconnection = connVector.get(i);
            if (pooledconnection.isLocked() && pooledconnection.getLastAccess() < checkoutTime) {
                numCheckoutTimeout++;
                warn(" Warning: found timed-out connection\n"
                        + " (链接获取时间LastAccess=" + pooledconnection.getLastAccess() + " 超出最大租用时间=" + checkoutMilliSeconds + " )\n"
                        + pooledconnection.dumpInfo());
                removeConnection(pooledconnection);
                notifyAll();
            } else if (pooledconnection.getLastAccess() < keepToTime) {
                if (pooledconnection.getLock()) {
                    removeConnection(pooledconnection);
                    pooledconnection.releaseLock();
                    notifyAll();
                } else if (timeoutMilliSeconds > MIN_TIMEOUT_MILLI_SECONDS) {

                    error("force stop connection = " + pooledconnection);
                    LOG.error(pooledconnection.dumpInfo());

                    try {
                        pooledconnection.abort(executor);
                    } catch (SQLException e) {
                        LOG.warn("abort", e);
                    }

                    removeConnection(pooledconnection);
                    pooledconnection.releaseLock();
                    notifyAll();
                } else {
                    error("find timeout connection = " + pooledconnection + ", but timeoutMilliSeconds is too small or net set :" + timeoutMilliSeconds);
                }
            } else {
                i++;
            }
        }

        debug(" reapIdleConnections() finished");
    }

    private void debug(String msg) {
        if (trace) {
            LOG.debug(CONNECTION_POOL + alias + " " + msg);
        }
    }

    private void removeConnection(PooledConnection pooledconnection) {
        try {
            connVector.remove(pooledconnection);
            new JavaAlarm(pooledconnection, timeoutMilliSeconds);
        } catch (TimeoutException e1) {
            debug(e1);
            //try to abort
            try {
                warn("force close timeout connection " + pooledconnection + " timeout=" + timeoutMilliSeconds);
                pooledconnection.abort(executor);
                pooledconnection.closeStatements();
                pooledconnection.close();
                connVector.remove(pooledconnection);
            } catch (SQLException e) {
                LOG.warn("force close", e);
            }
        }
    }

    /**
     * <p>removeAllConnections.</p>
     */
    public synchronized void removeAllConnections() {
        debug(" removeAllConnections() called");
        PooledConnection pooledconnection;
        for (; !connVector.isEmpty(); removeConnection(pooledconnection)) {
            pooledconnection = connVector.get(0);
        }

    }

    private synchronized Connection releaseConnection(PooledConnection pooledconnection) {
        if (trace) {
            pooledconnection.setTraceException(ExceptionUtils.getStackTrace(new Throwable()));
        }
        return pooledconnection;
    }


    /**
     * Set the setNetworkTimeout little bit longer than my own SQL execution timeout
     *
     * @return timeoutMilliSeconds*15/10
     */
    private int getNetworkTimeout() {
        return timeoutMilliSeconds * 15 / 10;
    }

    /**
     * <p>getConnection.</p>
     *
     * @return a {@link java.sql.Connection} object.
     * @throws java.sql.SQLException if any.
     */
    public synchronized Connection getConnection()
            throws SQLException {
        debug(" getConnection() called");
        numRequests++;
        do {
            do {
                for (int i = 0; i < connVector.size(); i++) {
                    PooledConnection pooledconnection = connVector.get(i);
                    if (pooledconnection.getLock()) {
                        return releaseConnection(pooledconnection);
                    }
                }

                debug(" all connections locked.  calling" + " createConnection()");
                if (connVector.size() < maxConn) {
                    debug(" opening new connection to database size=" + size());
                    Connection connection = createDriverConnection();
                    debug(" finished opening new connection");

                    PooledConnection pooledconnection1 = new PooledConnection(connection, this, maxPrepStmts);
                    pooledconnection1.getLock();

                    if (timeoutMilliSeconds > MIN_TIMEOUT_MILLI_SECONDS) {
                        try {
                            pooledconnection1.setNetworkTimeout(executor, getNetworkTimeout());
                            debug("create connection with timeoutMiliSeconds = " + timeoutMilliSeconds);
                        } catch (SQLException e) {
                            if (trace) {
                                LOG.warn("network timeout", e);
                            }
                            break;
                        }
                    } else {
                        debug(" timeoutMiliSeconds = " + timeoutMilliSeconds + ", which is too small to set ");
                    }

                    connVector.add(pooledconnection1);
                    return releaseConnection(pooledconnection1);
                }
                try {
                    debug(" pool is full.  calling wait()");
                    numWaits++;
                    wait();
                } catch (InterruptedException interruptedexception) {
                    debug("Get Connection interrupted");
                    Thread.currentThread().interrupt();
                }
            } while (!trace);
            debug(" awoken from wait().  trying to grab an available connection");
        } while (true);
    }

    /**
     * <p>returnConnection.</p>
     *
     * @param pooledconnection a {@link PooledConnection} object.
     */
    public synchronized void returnConnection(PooledConnection pooledconnection) {
        if (maxCheckout > 0 && pooledconnection.getCheckoutCount() >= maxCheckout) {
            debug(" connection checked out max #" + maxCheckout + " of times." + " closing it.");
            removeConnection(pooledconnection);
        }
        debug(" releasing lock and calling notifyAll()");
        pooledconnection.releaseLock();
        notifyAll();
    }

    Connection createDriverConnection()
            throws SQLException {
        Connection cn = DriverManager.getConnection(url, username, password);
        if (timeoutMilliSeconds > MIN_TIMEOUT_MILLI_SECONDS) {
            try {
                cn.setNetworkTimeout(executor, getNetworkTimeout());
            } catch (Exception e) {
                if (trace) {
                    LOG.warn("timeout", e);
                }
            }
        }
        return cn;
    }

    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String toString() {
        return "{ url:'" + url + "', username:'" + username + "', password:'" + StringUtils.abbreviate(password, 4) + "'}";
    }

    /**
     * <p>Setter for the field <code>prefetchSize</code>.</p>
     *
     * @param prefetchSize a int.
     */
    public void setPrefetchSize(int prefetchSize) {
        this.prefetchSize = prefetchSize;
    }

    /**
     * <p>Getter for the field <code>prefetchSize</code>.</p>
     *
     * @return a int.
     */
    public int getPrefetchSize() {
        return prefetchSize;
    }


    /**
     * <p>Constructor for ConnectionPool.</p>
     *
     * @param alias a {@link java.lang.String} object.
     * @param url a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param maxConn a int.
     * @param timeoutMilliSeconds a int.
     * @param checkoutMilliSeconds a int.
     * @param maxCheckout a int.
     */
    public ConnectionPool(String alias, String url, String username,
                          String password, int maxConn, int timeoutMilliSeconds, int checkoutMilliSeconds,
                          int maxCheckout) {
        this(alias, url, username, password, maxConn, timeoutMilliSeconds, checkoutMilliSeconds, maxCheckout, 200);
    }

    /**
     * <p>dumpInfo.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String dumpInfo() {
        String s = System.getProperty("line.separator");
        String s1 = "Pool: " + this + s;
        s1 += "\tAlias: " + getAlias() + s;
        s1 += "\tMax connections: " + getMaxConn() + s;
        s1 += "\tCheckouts: " + getNumRequests() + s;
        s1 += "\tThread waits: " + getNumWaits() + s;
        s1 += "\tConnections found closed: " + numConnectionFaults + s;
        s1 += "\tConnections reaped by timeout: " + getNumCheckoutTimeouts() + s;
        s1 += "\tConnections currently in pool: " + size() + s;
        StringBuilder sb = new StringBuilder(s1);
        synchronized (connVector) {
            for (PooledConnection pooledConnection : connVector) {
                if (pooledConnection != null) {
                    sb.append(pooledConnection.dumpInfo());
                }
            }
        }

        return sb.toString();
    }

    List<PooledConnection> connVector;
    String url;
    private final String username;
    private final String password;
    private final String alias;
    private final int maxConn;
    private int timeoutMilliSeconds;
    private final int checkoutMilliSeconds;
    private int numCheckoutTimeout;
    private int numRequests;
    private int numWaits;
    private final int maxCheckout;
    private boolean cacheStatements;
    int numConnectionFaults;
    private boolean trace;
    private int prefetchSize;
}
