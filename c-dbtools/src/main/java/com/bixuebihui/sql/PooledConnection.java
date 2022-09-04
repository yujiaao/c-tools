// Decompiled  Date: 2005-1-4 11:28:31

package com.bixuebihui.sql;

import com.bixuebihui.algorithm.LRULinkedHashMap;
import com.bixuebihui.algorithm.RemoveActionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;


/**
 * <p>PooledConnection class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class PooledConnection implements Connection, Runnable {

	public static final int MAX_RETRIES_TO_CREATE_CONN = 3;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PooledConnection [maxPrepStmts=" + maxPrepStmts + ", pool=" + pool + ", conn=" + conn + ", locked="
				+ locked + ",\n lastAccess=" + lastAccess + ", lastCheckin=" + lastCheckin + ", checkoutCount="
				+ checkoutCount + ",\n theStatement=" + theStatement + ", totalStatements=" + totalStatements
				+ ", preparedCalls=" + preparedCalls + ", preparedStatementHits=" + preparedStatementHits
				+ ", preparedStatementMisses=" + preparedStatementMisses + ", preparedStatements=" + preparedStatements
				+ ",\n traceException=" + traceException + ", prepStmts=" + prepStmts + "]";
	}

	private int maxPrepStmts = 200;

	/**
	 * <p>createBlob.</p>
	 *
	 * @return a {@link java.sql.Blob} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public Blob createBlob() throws SQLException {
		return conn.createBlob();
	}

	/**
	 * <p>createClob.</p>
	 *
	 * @return a {@link java.sql.Clob} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public Clob createClob() throws SQLException {
		return conn.createClob();
	}

	/**
	 * <p>getClientInfo.</p>
	 *
	 * @return a {@link java.util.Properties} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public Properties getClientInfo() throws SQLException {
		return conn.getClientInfo();
	}

	/** {@inheritDoc} */
	@Override
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		conn.setClientInfo(arg0);

	}

	/** {@inheritDoc} */
	@Override
	public String getClientInfo(String name) throws SQLException {
		return conn.getClientInfo(name);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		return conn.isValid(timeout);
	}

	private static final Logger LOG  = LoggerFactory.getLogger(PooledConnection.class);

	/**
	 * <p>getLock.</p>
	 *
	 * @return a boolean.
	 */
	public synchronized boolean getLock() {
		if (locked) {
			return false;
		} else {
			locked = true;
			checkoutCount++;
			lastAccess = System.currentTimeMillis();
			return true;
		}
	}

	/**
	 * <p>isLocked.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * <p>Getter for the field <code>checkoutCount</code>.</p>
	 *
	 * @return a int.
	 */
	public int getCheckoutCount() {
		return checkoutCount;
	}

	/**
	 * <p>Getter for the field <code>lastAccess</code>.</p>
	 *
	 * @return a long.
	 */
	public long getLastAccess() {
		return lastAccess;
	}

	/**
	 * <p>Getter for the field <code>lastCheckin</code>.</p>
	 *
	 * @return a long.
	 */
	public long getLastCheckin() {
		return lastCheckin;
	}

	/**
	 * <p>closeStatements.</p>
	 *
	 * @throws java.sql.SQLException if any.
	 */
	public void closeStatements() throws SQLException {
		if (theStatement != null){
			try{
				theStatement.cancel();
				theStatement.getStatement().close();
			}catch(SQLException e){
				LOG.warn("closeStatements", e);
			}
		}
		for (PooledPreparedStatement pooledPreparedStatement : prepStmts.values()) {
			try {
				pooledPreparedStatement.getStatement().cancel();
				pooledPreparedStatement.getStatement().close();
			} catch (SQLException e) {
				LOG.warn("close pooled prepared statement", e);
			}
		}

	}

	/**
	 * <p>releaseLock.</p>
	 */
	protected synchronized void releaseLock() {
		lastCheckin = System.currentTimeMillis();
		locked = false;
	}

	/**
	 * <p>getNativeConnection.</p>
	 *
	 * @return a {@link java.sql.Connection} object.
	 */
	public Connection getNativeConnection() {
		return conn;
	}

	/**
	 * <p>close.</p>
	 *
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public void close() throws SQLException {
		lastAccess = System.currentTimeMillis();
		pool.returnConnection(this);
	}

	/**
	 * <p>run.</p>
	 */
	@Override
	public void run() {
		try {
			closeStatements();
		} catch (SQLException e) {
			LOG.warn("closeStatements", e);
		}
		try {
			conn.close();
		} catch (SQLException e1) {
			LOG.warn("conn.close", e1);
		}
	}

	/**
	 * <p>dumpInfo.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String dumpInfo() {
		String s = System.getProperty("line.separator");
		String s1 = "\t\tConnection: " + this + s;
		if (pool.isCacheStatements()) {
			s1 += "\t\t\tPrepared Statements Hits: " + preparedStatementHits
					+ s;
			s1 += "\t\t\tPrepared Statements Misses: "
					+ preparedStatementMisses + s;
		} else {
			s1 += "\t\t\tPrepared Statements Requested: " + preparedStatements
					+ s;
		}
		s1 += "\t\t\tLast Checkout: " + getLastAccess() + ": "
				+ new java.util.Date(getLastAccess()) + s;
		s1 += "\t\t\tLast Checkin : " + getLastCheckin() + ": "
				+ new java.util.Date(getLastCheckin()) + s;
		s1 += "\t\t\tCurrent Time : " + System.currentTimeMillis() + ": "
				+ new java.util.Date(System.currentTimeMillis() ) + s;
		s1 += "\t\t\t"
				+ (isLocked() ? "Connection IS checked out."
				: "Connection is NOT checked out.") + s;
		s1 += "\t\t\tCheckout Stack Trace: ";
		s1 += traceException==null? "Stacktrace  NOT set." : traceException;
		s1 += s;
		if (theStatement != null) {
            s1 += theStatement.dumpInfo();
        }
		return s1;
	}

	/**
	 * <p>createStatement.</p>
	 *
	 * @return a {@link java.sql.Statement} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public Statement createStatement() throws SQLException {
		guardConnection();
		totalStatements++;
		if (pool.isCacheStatements()) {
			if (theStatement == null) {
                theStatement = new PooledStatement(conn.createStatement());
            }
			return theStatement;
		} else {
			return conn.createStatement();
		}
	}

	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String s) throws SQLException {
		guardConnection();

		if (pool.isCacheStatements()) {
			PooledPreparedStatement obj = prepStmts.get(s);

			//added by xwx 2016-1-26
			if(obj!=null && obj.isClosed()){
				//may be due to network problem , the statement is closed.
				prepStmts.remove(s);
				obj =null;
			}

			if (obj == null) {
				obj = new PooledPreparedStatement(conn.prepareStatement(s));
				synchronized (prepStmts) {
					prepStmts.put(s, obj);
				}
				preparedStatementMisses++;
			} else {
				preparedStatementHits++;
			}
			return  obj;
		} else {
			preparedStatements++;
			return conn.prepareStatement(s);
		}
	}

	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String s, int resultSetType,
											  int resultSetConcurrency) throws SQLException {
		guardConnection();

		if (pool.isCacheStatements()) {
            preparedStatementMisses++;
        } else {
            preparedStatements++;
        }
		return conn.prepareStatement(s,
				resultSetType, resultSetConcurrency);
	}

	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String s) throws SQLException {
		guardConnection();

		preparedCalls++;
		return conn.prepareCall(s);
	}

	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String s, int i, int j)
			throws SQLException {
		guardConnection();

		preparedCalls++;
		return conn.prepareCall(s, i, j);
	}

	/** {@inheritDoc} */
	@Override
	public Statement createStatement(int i, int j) throws SQLException {
		totalStatements++;
		return conn.createStatement(i, j);
	}

	/** {@inheritDoc} */
	@Override
	public String nativeSQL(String s) throws SQLException {
		return conn.nativeSQL(s);
	}

	/**
	 * <p>getAutoCommit.</p>
	 *
	 * @return a boolean.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		return conn.getAutoCommit();
	}

	/** {@inheritDoc} */
	@Override
	public void setAutoCommit(boolean flag) throws SQLException {
		conn.setAutoCommit(flag);
	}

	/**
	 * <p>commit.</p>
	 *
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public void commit() throws SQLException {
		conn.commit();
	}

	/**
	 * <p>rollback.</p>
	 *
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public void rollback() throws SQLException {
		conn.rollback();
	}

	/**
	 * <p>isClosed.</p>
	 *
	 * @return a boolean.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return conn.isClosed();
	}

	/**
	 * <p>getMetaData.</p>
	 *
	 * @return a {@link java.sql.DatabaseMetaData} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return conn.getMetaData();
	}

	/**
	 * <p>isReadOnly.</p>
	 *
	 * @return a boolean.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public boolean isReadOnly() throws SQLException {
		return conn.isReadOnly();
	}

	/** {@inheritDoc} */
	@Override
	public void setReadOnly(boolean flag) throws SQLException {
		conn.setReadOnly(flag);
	}

	/**
	 * <p>getCatalog.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public String getCatalog() throws SQLException {
		return conn.getCatalog();
	}

	/** {@inheritDoc} */
	@Override
	public void setCatalog(String s) throws SQLException {
		conn.setCatalog(s);
	}

	/**
	 * <p>getTransactionIsolation.</p>
	 *
	 * @return a int.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public int getTransactionIsolation() throws SQLException {
		return conn.getTransactionIsolation();
	}

	/** {@inheritDoc} */
	@Override
	public void setTransactionIsolation(int i) throws SQLException {
		conn.setTransactionIsolation(i);
	}

	/**
	 * <p>getWarnings.</p>
	 *
	 * @return a {@link java.sql.SQLWarning} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		return conn.getWarnings();
	}

	/**
	 * <p>Setter for the field <code>traceException</code>.</p>
	 *
	 * @param exception a {@link java.lang.String} object.
	 */
	protected void setTraceException(String exception) {
		traceException = exception;
	}


	/**
	 * <p>Constructor for PooledConnection.</p>
	 *
	 * @param connection a {@link java.sql.Connection} object.
	 * @param connectionpool a {@link ConnectionPool} object.
	 */
	public PooledConnection(Connection connection, ConnectionPool connectionpool) {
		theStatement = null;
		conn = connection;
		pool = connectionpool;
		locked = false;
		lastAccess = 0L;
		checkoutCount = 0;
		totalStatements = 0;
		prepStmts = new LRULinkedHashMap<>(
				maxPrepStmts, new RemoveActionImpl());
	}

	/**
	 * <p>Constructor for PooledConnection.</p>
	 *
	 * @param connection a {@link java.sql.Connection} object.
	 * @param connectionpool a {@link ConnectionPool} object.
	 * @param maxPrepStmts a int.
	 */
	public PooledConnection(Connection connection,
							ConnectionPool connectionpool, int maxPrepStmts) {
		theStatement = null;
		conn = connection;
		pool = connectionpool;
		locked = false;
		lastAccess = 0L;
		checkoutCount = 0;
		totalStatements = 0;
		this.maxPrepStmts = maxPrepStmts;
		prepStmts = new LRULinkedHashMap<>(
				maxPrepStmts, new RemoveActionImpl());
	}

	/**
	 * <p>guardConnection.</p>
	 */
	public void guardConnection(){
		boolean isClosed;
		try {
			isClosed = conn.isClosed();
		} catch (SQLException sqlexception) {
			isClosed = true;
		}
		if (isClosed) {
			pool.numConnectionFaults++;
			String method = "PooledConnection.guardConnection(): ";
			LOG.warn(method
					+ "found closed Connection. "
					+ "Statement information follows. Attempting to recover.");
			if (theStatement != null) {
                LOG.warn(method
                        + theStatement.dumpInfo());
            } else {
                LOG.warn(method+" statement was null");
            }
			theStatement = null;
			for (int i = 0; i < MAX_RETRIES_TO_CREATE_CONN; i++) {
				try {
					conn = pool.createDriverConnection();
					LOG.info(method + "Recovered connection");
					return;
				} catch (SQLException sqlexception1) {
					LOG.error(method + "failed to create connection on try #" + i);
				}
			}
		}
	}

	private final ConnectionPool pool;
	private Connection conn;
	private boolean locked;
	private long lastAccess;
	private long lastCheckin;
	private int checkoutCount;
	private PooledStatement theStatement;
	int totalStatements;
	int preparedCalls;
	int preparedStatementHits;
	int preparedStatementMisses;
	int preparedStatements;
	private String traceException;
	private final Map<String, PooledPreparedStatement> prepStmts;

	// set save point
	// added by [xing]

	/**
	 * <p>clearWarnings.</p>
	 *
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public void clearWarnings() throws SQLException {
		conn.clearWarnings();
	}

	/** {@inheritDoc} */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrecy, int resultSetHoldability) throws SQLException {
		return conn.createStatement(resultSetType, resultSetConcurrecy, resultSetHoldability);
	}

	/**
	 * <p>getHoldability.</p>
	 *
	 * @return a int.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public int getHoldability() throws SQLException {
		return conn.getHoldability();
	}

	/** {@inheritDoc} */
	@Override
	public void setHoldability(int holdability) throws SQLException {
		conn.setHoldability(holdability);
	}

	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrecy, int resultSetHoldability)
			throws SQLException {
		return conn.prepareCall(sql,resultSetType, resultSetConcurrecy, resultSetHoldability);
	}

	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, int i, int j, int k)
			throws SQLException {
		return conn.prepareStatement(sql, i, j, k);
	}

	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		return conn.prepareStatement(sql, autoGeneratedKeys);
	}

	/**
	 * <p>prepareStatement.</p>
	 *
	 * @param sql a {@link java.lang.String} object.
	 * @param x an array of  int.
	 * @return a {@link java.sql.PreparedStatement} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int[] x)
			throws SQLException {
		return conn.prepareStatement(sql, x);
	}

	/**
	 * <p>prepareStatement.</p>
	 *
	 * @param p a {@link java.lang.String} object.
	 * @param x an array of {@link java.lang.String} objects.
	 * @return a {@link java.sql.PreparedStatement} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public PreparedStatement prepareStatement(String p, String[] x)
			throws SQLException {
		return conn.prepareStatement(p, x);
	}

	/** {@inheritDoc} */
	@Override
	public void releaseSavepoint(Savepoint savepoint)
			throws SQLException {
		LOG.info(savepoint.toString());
		conn.releaseSavepoint(savepoint);
	}

	/** {@inheritDoc} */
	@Override
	public void rollback(Savepoint p) throws SQLException {
		conn.rollback(p);
	}

	/**
	 * <p>setSavepoint.</p>
	 *
	 * @return a {@link java.sql.Savepoint} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public Savepoint setSavepoint() throws SQLException {
		return conn.setSavepoint();
	}

	/** {@inheritDoc} */
	@Override
	public Savepoint setSavepoint(String p) throws SQLException {
		return conn.setSavepoint(p);
	}

	/**
	 * <p>createArrayOf.</p>
	 *
	 * @param typeName a {@link java.lang.String} object.
	 * @param elements an array of {@link java.lang.Object} objects.
	 * @return a {@link java.sql.Array} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return conn.createArrayOf(typeName, elements);
	}

	/**
	 * <p>createStruct.</p>
	 *
	 * @param typeName a {@link java.lang.String} object.
	 * @param attributes an array of {@link java.lang.Object} objects.
	 * @return a {@link java.sql.Struct} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return conn.createStruct(typeName, attributes);
	}

	/**
	 * <p>createNClob.</p>
	 *
	 * @return a {@link java.sql.NClob} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public NClob createNClob() throws SQLException {
		return conn.createNClob();
	}

	/**
	 * <p>createSQLXML.</p>
	 *
	 * @return a {@link java.sql.SQLXML} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public SQLXML createSQLXML() throws SQLException {
		return conn.createSQLXML();
	}

	/** {@inheritDoc} */
	@Override
	public void setClientInfo(String arg0, String arg1)
			throws SQLClientInfoException {
		conn.setClientInfo(arg0, arg1);

	}

	/** {@inheritDoc} */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return conn.unwrap(iface);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return conn.isWrapperFor(iface);
	}

	/** {@inheritDoc} */
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		conn.setTypeMap(map);
	}

	/**
	 * <p>getSchema.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public String getSchema() throws SQLException {
		return conn.getSchema();
	}

	/** {@inheritDoc} */
	@Override
	public void setSchema(String schema) throws SQLException {
		conn.setSchema(schema);

	}

	/** {@inheritDoc} */
	@Override
	public void abort(Executor executor) throws SQLException {
		if(this.theStatement!=null && !this.theStatement.isClosed()){
			try{
				this.theStatement.cancel();
				this.theStatement.close();
			}catch(SQLException e){
				LOG.warn("abort executor",e);
			}
		}
		conn.abort(executor);
	}

	/** {@inheritDoc} */
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		conn.setNetworkTimeout(executor, milliseconds);

	}

	/**
	 * <p>getNetworkTimeout.</p>
	 *
	 * @return a int.
	 * @throws java.sql.SQLException if any.
	 */
	@Override
	public int getNetworkTimeout() throws SQLException {
		return conn.getNetworkTimeout();
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return conn.getTypeMap();
	}

}
